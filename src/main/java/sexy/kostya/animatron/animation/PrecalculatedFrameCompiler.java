package sexy.kostya.animatron.animation;

import net.minestom.server.coordinate.Vec;

import java.util.*;

public class PrecalculatedFrameCompiler implements FrameCompiler {

    private final Map<AnimationFrame.Type, List<AnimationFrame>> entries = new EnumMap<>(AnimationFrame.Type.class);

    private final static double T0 = 0.0;
    private final static double T2 = 1.0;
    private final static double T3 = 2.0;
    private final static double T4 = 3.0;

    @Override
    public void put(AnimationFrame.Type type, AnimationFrame frame) {
        final List<AnimationFrame> list = entries.computeIfAbsent(type, k -> new ArrayList<>());
        list.add(frame);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public List<Keyframe> compile() {
        final List<Keyframe> frames = new ArrayList<>();
        Map<AnimationFrame.Type, List<Vec>> typedFrames = new EnumMap(AnimationFrame.Type.class);
        try {
            entries.forEach((type, list) -> {
                if (list.isEmpty()) {
                    return;
                }
                list.sort(Comparator.comparingInt(AnimationFrame::getIndex));
                int index = 0;
                AnimationFrame previous = list.get(index++);
                AnimationFrame next = previous;
                if (previous.getIndex() > 0) {
                    previous = new AnimationFrame(
                            0,
                            type == AnimationFrame.Type.SCALE ? Vec.ONE : Vec.ZERO,
                            previous.getInterpolation()
                    );
                }
                int tick = 0;
                final List<Vec> output = new ArrayList<>();
                while (true) {
                    if (tick < next.getIndex()) {
                        double ratio = (double) (tick - previous.getIndex()) / (next.getIndex() - previous.getIndex());
                        final Vec interpolated;
                        if (next.getInterpolation() == Interpolation.CATMULLROM) {
                            double T5 = (T3 - T2) * ratio + T2;

                            Vec previous2 = output.isEmpty() ? previous.getValue() : output.get(output.size() - 1);
                            Vec next2 = index >= list.size() - 1 ? next.getValue() : list.get(index + 1).getValue();

                            Vec a2 = previous2.mul((T2 - T5) / (T2 - T0))
                                    .add(previous.getValue().mul((T5 - T0) / (T2 - T0)));
                            Vec a3 = previous.getValue().mul((T3 - T5) / (T3 - T2))
                                    .add(next.getValue().mul((T5 - T2) / (T3 - T2)));
                            Vec a4 = next.getValue().mul((T4 - T5) / (T4 - T3))
                                    .add(next2.mul((T5 - T3) / (T4 - T3)));
                            Vec b2 = a2.mul((T3 - T5) / (T3 - T0))
                                    .add(a3.mul((T5 - T0) / (T3 - T0)));
                            Vec b3 = a3.mul((T4 - T5) / (T4 - T2))
                                    .add(a4.mul((T5 - T2) / (T4 - T2)));

                            interpolated = b2.mul((T3 - T5) / (T3 - T2))
                                    .add(b3.mul((T5 - T2) / (T3 - T2)));
                        } else {
                            interpolated = previous.getValue().add(next.getValue().sub(previous.getValue()).mul(ratio));
                        }
                        output.add(interpolated);
                    } else {
                        output.add(next.getValue());
                        if (index == list.size()) {
                            break;
                        }
                        previous = next;
                        next = list.get(index++);
                    }
                    tick++;
                }
                typedFrames.put(type, output);
            });
            int max = typedFrames.values().stream().mapToInt(Collection::size).max().orElse(0);
            for (int tick = 0; tick < max; ++tick) {
                final Vec rot = get(AnimationFrame.Type.ROTATION, tick, typedFrames);
                frames.add(new Keyframe(
                        get(AnimationFrame.Type.POSITION, tick, typedFrames).div(16),
                        new Vec(
                                Math.toRadians(rot.x()),
                                Math.toRadians(rot.y()),
                                Math.toRadians(rot.z())
                        ),
                        get(AnimationFrame.Type.SCALE, tick, typedFrames)
                ));
            }
        } finally {
            entries.clear();
        }
        return frames;
    }

    private Vec get(AnimationFrame.Type type, int index, Map<AnimationFrame.Type, List<Vec>> typedFrames) {
        List<Vec> list = typedFrames.get(type);
        if (list == null) {
            return type == AnimationFrame.Type.SCALE ? Vec.ONE : Vec.ZERO;
        }
        return index >= list.size() ? list.get(list.size() - 1) : list.get(index);
    }

}
