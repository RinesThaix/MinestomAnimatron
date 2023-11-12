package sexy.kostya.animatron.integration;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sexy.kostya.animatron.ModelEngine;
import sexy.kostya.animatron.animation.Animation;
import sexy.kostya.animatron.animation.AnimationQueue;
import sexy.kostya.animatron.model.Bone;
import sexy.kostya.animatron.model.Model;
import sexy.kostya.animatron.model.ViewableModel;
import sexy.kostya.animatron.util.Quaternion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class ModelView implements ViewableModel {

    private final Model          model;
    private final Entity         entity;
    private final EntityInjector injector;
    private final Vec            globalOffset;

    private final Map<String, VirtualBone> bones          = new HashMap<>();
    private final AnimationQueue           animationQueue = new AnimationQueue(this);

    public ModelView(@NotNull Model model, @NotNull Entity entity, @NotNull Vec globalOffset) {
        this.model = model;
        this.entity = entity;
        this.injector = new EntityInjector();
        this.globalOffset = globalOffset;

        entity.setInvisible(true);
        entity.setBoundingBox(model.getBoundingBoxWidth(), model.getBoundingBoxHeight(), model.getBoundingBoxWidth());
    }

    public ModelView(@NotNull Model model, @NotNull Entity entity) {
        this(model, entity, Vec.ZERO);
    }

    @NotNull
    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public int getBoneID(@NotNull String boneName) {
        final VirtualBone bone = bones.get(boneName);
        if (bone == null) {
            return -1;
        }
        return bone.id;
    }

    @Override
    public void colorize(int r, int g, int b) {
        final Color color = new Color(r, g, b);
        for (VirtualBone bone : bones.values()) {
            bone.colorize(color);
        }
    }

    @Override
    public void colorizeBone(@NotNull String boneName, int r, int g, int b) {
        VirtualBone bone = bones.get(boneName);
        if (bone != null) {
            bone.colorize(r, g, b);
        }
    }

    @Override
    public void moveBone(@NotNull String boneName, Vec pos) {
        VirtualBone bone = bones.get(boneName);
        if (bone != null) {
            Pos result = entity.getPosition()
                    .add(pos)
                    .add(globalOffset)
                    .add(0.0, bone.small ? .63 : 1.452, 0.0);
            bone.updatePosition(result);
        }
    }

    @Override
    public void rotateBone(@NotNull String boneName, Vec rot) {
        VirtualBone bone = bones.get(boneName);
        if (bone != null) {
            bone.updateHeadRotation(new Vec(
                    Math.toDegrees(rot.x()),
                    Math.toDegrees(rot.y()),
                    Math.toDegrees(rot.z())
            ));
        }
    }

    @Nullable
    @Override
    public Animation getAnimation(@NotNull String animationName) {
        return model.getAnimation(animationName);
    }

    @Override
    public void playAnimation(@NotNull Animation animation, float rate) {
        animationQueue.pushAnimation(animation, rate);
    }

    @Override
    public void switchAnimation(@NotNull Animation from, @NotNull Animation to, float rate) {
        animationQueue.switchAnimation(from, to, rate);
    }

    @Override
    public void stopAnimation(@NotNull Animation animation) {
        animationQueue.stopAnimation(animation);
    }

    @Override
    public void stopAllAnimations() {
        animationQueue.stopAllAnimations();
    }

    @Override
    public void tickAnimations() {
        animationQueue.tick(Math.toRadians(entity.getPosition().yaw()));
    }

    public @NotNull
    EntityInjector getInjector() {
        return injector;
    }

    public @NotNull
    Entity getEntity() {
        return entity;
    }

    public final class EntityInjector {

        public void tick() {
            tickAnimations();
        }

        public void addViewer(Player player) {
            for (VirtualBone bone : bones.values()) {
                bone.show(player);
            }
        }

        public void removeViewer(Player player) {
            for (VirtualBone bone : bones.values()) {
                bone.hide(player);
            }
        }

        public CompletableFuture<Void> setInstance(Instance instance, Pos spawnPosition, CompletableFuture<Void> superResult) {
            return superResult.thenRun(() -> {
                final double yawRadians = Math.toRadians(spawnPosition.yaw());
                for (Bone bone : model.getBones().values()) {
                    summonBone(yawRadians, spawnPosition, bone, Vec.ZERO);
                }
                ModelEngine.viewManager().register(ModelView.this);
            });
        }

        public void remove() {
            ModelEngine.viewManager().unregister(ModelView.this);
        }

        public void synchronizePosition() {
            updatePos(entity.getPosition(), null);
        }

        public void refreshPosition(Pos newPosition, boolean ignoreView, BiConsumer<Pos, Boolean> superCall) {
            final Pos previous = entity.getPosition();
            superCall.accept(newPosition, ignoreView);
            final double yaw = ignoreView ? previous.yaw() : newPosition.yaw();
            updatePos(newPosition, yaw);
        }

        private void updatePos(Pos pos, Double yaw) {
            final double yawRadians = Math.toRadians(yaw == null ? pos.yaw() : yaw);
            for (Bone bone : model.getBones().values()) {
                updatePos0(yawRadians, pos, bone, Vec.ZERO);
            }
        }

        private void updatePos0(double yaw, Pos pos, Bone bone, Vec parentOffset) {
            final Vec offset      = parentOffset.add(bone.getLocalOffsetX(), bone.getLocalOffsetY(), bone.getLocalOffsetZ());
            final Vec relativePos = Quaternion.rotateAroundY(offset, yaw);

            VirtualBone virtualBone = bones.get(bone.getName());
            if (virtualBone != null) {
                virtualBone.updatePosition(pos.add(relativePos).add(globalOffset));
            }

            for (Bone child : bone.getChild().values()) {
                updatePos0(yaw, pos, child, offset);
            }
        }

        private void summonBone(double yaw, Pos pos, Bone bone, Vec parentOffset) {
            final Vec offset      = parentOffset.add(bone.getLocalOffsetX(), bone.getLocalOffsetY(), bone.getLocalOffsetZ());
            final Vec relativePos = Quaternion.rotateAroundY(offset, yaw);

            final VirtualBone children = new VirtualBone(entity, bone);
            children.updatePosition(pos.add(relativePos).add(globalOffset));
            children.endDamage();

            bones.put(bone.getName(), children);

            for (Player viewer : entity.getViewers()) {
                children.show(viewer);
            }

            for (Bone child : bone.getChild().values()) {
                summonBone(yaw, pos, child, offset);
            }
        }

    }

}
