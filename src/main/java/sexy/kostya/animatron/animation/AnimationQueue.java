package sexy.kostya.animatron.animation;

import net.minestom.server.coordinate.Vec;
import sexy.kostya.animatron.model.Bone;
import sexy.kostya.animatron.model.ViewableModel;
import sexy.kostya.animatron.util.Quaternion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AnimationQueue {

    private final ViewableModel         model;
    private final List<AnimationPlayer> players = new ArrayList<>();

    public AnimationQueue(ViewableModel model) {
        this.model = model;
    }

    public synchronized void pushAnimation(Animation animation, float rate) {
        for (AnimationPlayer player : players) {
            if (player.animation == animation && Float.compare(player.rate, rate) == 0) {
                return;
            }
        }
        AnimationPlayer player = new AnimationPlayer(animation, rate);
        animation.getFramesByBone().forEach((name, frames) -> player.windows.put(name, new AnimationWindow(frames.compile())));
        players.add(player);
    }

    public synchronized void switchAnimation(Animation from, Animation to, float rate) {
        stopAnimation(from);
        pushAnimation(to, rate);
    }

    public synchronized void stopAnimation(Animation animation) {
        players.removeIf(player -> player.animation == animation);
    }

    public synchronized void stopAllAnimations() {
        players.clear();
    }

    public void tick(double yaw) {
        for (Bone bone : model.getModel().getBones().values()) {
            updateBone(yaw, null, bone, Vec.ZERO, Vec.ZERO);
        }
    }

    private void updateBone(double yaw, Bone parent, Bone bone, Vec parentRotation, Vec parentPosition) {
        List<Keyframe> frames = next(bone.getName());

        final Vec position;
        final Vec rotation;
        if (frames.isEmpty()) {
            position = Keyframe.INITIAL.getValue(AnimationFrame.Type.POSITION);
            rotation = Keyframe.INITIAL.getValue(AnimationFrame.Type.ROTATION);
        } else {
            position = frames.stream().map(f -> f.getValue(AnimationFrame.Type.POSITION)).reduce(Vec::add).get();
            rotation = frames.stream().map(f -> f.getValue(AnimationFrame.Type.ROTATION)).reduce(Vec::add).get();
        }

        final Vec localPosition = position.mul(1.0, 1.0, -1.0).add(bone.getLocalOffsetX(), bone.getLocalOffsetY(), bone.getLocalOffsetZ());
        final Vec localRotation = rotation.add(bone.getLocalRotationX(), bone.getLocalRotationY(), bone.getLocalRotationZ());

        final Vec globalPosition;
        final Vec globalRotation;
        if (parent == null) {
            globalPosition = Quaternion.rotateAroundY(localPosition, yaw);
            globalRotation = localRotation;
        } else {
            globalPosition = Quaternion.rotateAroundY(
                    Quaternion.rotate(localPosition, parentRotation),
                    yaw
            ).add(parentPosition);
            globalRotation = Quaternion.combine(localRotation, parentRotation);
        }

        model.moveBone(bone.getName(), globalPosition);
        model.rotateBone(bone.getName(), globalRotation);

        for (Bone child : bone.getChild().values()) {
            updateBone(yaw, bone, child, globalRotation, globalPosition);
        }
    }

    private List<Keyframe> next(String boneName) {
        final List<Keyframe>  result      = new ArrayList<>();
        List<AnimationPlayer> toBeRemoved = new ArrayList<>(0);
        for (AnimationPlayer player : players) {
            result.add(nextFrame(player, boneName, toBeRemoved));
        }
        if (!toBeRemoved.isEmpty()) {
            synchronized (this) {
                players.removeAll(toBeRemoved);
            }
        }
        return result;
    }

    private Keyframe nextFrame(AnimationPlayer player, final String boneName, List<AnimationPlayer> toBeRemoved) {
        final AnimationWindow window = player.windows.get(boneName);
        if (window == null) {
            return Keyframe.INITIAL;
        }
        final int size = window.list.size();
        while (true) {
            final int index = Math.round(window.index);
            if (index < size) {
                final Keyframe result = window.list.get(index);
                window.index += player.rate;
                return result;
            }
            if (size == 0) {
                throw new IllegalStateException(
                        String.format(
                                "Faced an empty animation for bone %s",
                                boneName
                        )
                );
            }
            if (index >= player.animation.getLength()) {
                if (player.animation.getLoopMode() == LoopMode.ONCE) {
                    toBeRemoved.add(player);
                    return Keyframe.INITIAL;
                } else { // loop
                    player.windows.values().forEach(w -> w.index = 0);
                }
            } else {
                return window.list.get(window.list.size() - 1);
            }
        }
    }

    private final static class AnimationPlayer {

        private final Animation                    animation;
        private final float                        rate;
        private final Map<String, AnimationWindow> windows;

        public AnimationPlayer(Animation animation, float rate, Map<String, AnimationWindow> windows) {
            this.animation = animation;
            this.rate = rate;
            this.windows = windows;
        }

        public AnimationPlayer(Animation animation, float rate) {
            this(animation, rate, new HashMap<>());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AnimationPlayer that = (AnimationPlayer) o;

            return Float.compare(rate, that.rate) == 0 && animation.equals(that.animation);
        }

        @Override
        public int hashCode() {
            int result = animation.hashCode();
            result = 31 * result + (rate != +0.0f ? Float.floatToIntBits(rate) : 0);
            return result;
        }

    }

    private final static class AnimationWindow {

        private final List<Keyframe> list;
        private       float            index;

        public AnimationWindow(List<Keyframe> list, int index) {
            this.list = list;
            this.index = index;
        }

        public AnimationWindow(List<Keyframe> list) {
            this(list, 0);
        }

        @Override
        public String toString() {
            return String.format(
                    "AnimationWindow(%.0f/%d)",
                    index,
                    list.size()
            );
        }

    }

}
