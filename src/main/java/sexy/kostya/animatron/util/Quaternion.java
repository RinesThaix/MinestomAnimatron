package sexy.kostya.animatron.util;

import net.minestom.server.coordinate.Vec;

public final class Quaternion {

    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Vec rotateAroundY(final Vec vec, double angle) {
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        final double x = vec.x() * cos - vec.z() * sin;
        final double z = vec.x() * sin + vec.z() * cos;
        return new Vec(x, vec.y(), z);
    }

    public static Vec rotateAroundZ(final Vec vec, final double angle) {
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        final double x = vec.x() * cos + vec.y() * sin;
        final double y = -vec.x() * sin + vec.y() * cos;
        return new Vec(x, y, vec.z());
    }

    public static Vec rotateAroundX(final Vec vec, final double angle) {
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        final double y = vec.y() * cos - vec.z() * sin;
        final double z = vec.y() * sin + vec.z() * cos;
        return new Vec(vec.x(), y, z);
    }

    public static Vec rotate(Vec vec, final Vec rot) {
        vec = rotateAroundX(vec, rot.x());
        vec = rotateAroundY(vec, rot.y());
        vec = rotateAroundZ(vec, rot.z());
        return vec;
    }

    public static Vec combine(Vec origin, Vec delta) {
        return fromEuler(origin).mul(fromEuler(delta)).toEuler();
    }

    public static Quaternion fromEuler(Vec euler) {
        final double cosX = Math.cos(euler.x() / 2);
        final double cosY = Math.cos(-euler.y() / 2);
        final double cosZ = Math.cos(euler.z() / 2);

        final double sinX = Math.sin(euler.x() / 2);
        final double sinY = Math.sin(-euler.y() / 2);
        final double sinZ = Math.sin(euler.z() / 2);

        return new Quaternion(
                sinX * cosY * cosZ + cosX * sinY * sinZ,
                cosX * sinY * cosZ - sinX * cosY * sinZ,
                cosX * cosY * sinZ + sinX * sinY * cosZ,
                cosX * cosY * cosZ - sinX * sinY * sinZ
        );
    }

    public Vec toEuler() {
        final double singularity = x * z + y * w;
        if (singularity > .499) {
            return new Vec(Math.atan2(x, w), Math.PI / 2, 0.0);
        }
        if (singularity < -.499) {
            return new Vec(-Math.atan2(x, w), -Math.PI / 2, 0.0);
        }
        return new Vec(
                Math.atan2(w * x * 2 - y * z * 2, 1 - 2 * (x * x + y * y)),
                -Math.asin(singularity * 2),
                Math.atan2(w * z * 2 - x * y * 2, 1 - 2 * (z * z + y * y))
        );
    }

    public Quaternion mul(Quaternion other) {
        return new Quaternion(
                x * other.w + w * other.x + y * other.z - z * other.y,
                y * other.w + w * other.y + z * other.x - x * other.z,
                z * other.w + w * other.z + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

}
