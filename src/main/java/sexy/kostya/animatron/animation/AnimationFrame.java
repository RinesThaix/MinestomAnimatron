package sexy.kostya.animatron.animation;

import net.minestom.server.coordinate.Vec;

public final class AnimationFrame {

    private final int index;
    private final Vec value;
    private final Interpolation interpolation;

    public AnimationFrame(int index, Vec value, Interpolation interpolation) {
        this.index = index;
        this.value = value;
        this.interpolation = interpolation;
    }

    public int getIndex() {
        return index;
    }

    public Vec getValue() {
        return value;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public String toString() {
        return "AnimationFrame{" +
                "index=" + index +
                ", value=" + value +
                '}';
    }

    public enum Type {
        POSITION,
        ROTATION,
        SCALE
    }

}
