package sexy.kostya.animatron.animation;

import net.minestom.server.coordinate.Vec;

import java.util.Arrays;

public final class Keyframe {

    public final static Keyframe INITIAL = new Keyframe(Vec.ZERO, Vec.ZERO, Vec.ONE);

    private final Vec[] values;

    public Keyframe(Vec[] values) {
        this.values = values;
    }

    public Keyframe(Vec position, Vec rotation, Vec scale) {
        this(new Vec[]{position, rotation, scale});
    }

    public Vec getValue(AnimationFrame.Type type) {
        return values[type.ordinal()];
    }

    @Override
    public String toString() {
        return "Keyframe{" +
                "values=" + Arrays.toString(values) +
                '}';
    }

}
