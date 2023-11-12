package sexy.kostya.animatron.gen.component.resourcepack.element;

public class Rotation
{
    private Float angle;
    private String axis;
    private Float[] origin;
    
    public Rotation() {
        this.angle = null;
        this.axis = null;
        this.origin = null;
    }
    
    public void setAngle(float angle, final String cubeName, final String partName) {
        if (angle % 22.5 != 0.0 || angle > 45.0f || angle < -45.0f) {
            throw new IllegalArgumentException("Invalid angle " + angle);
//            angle = 0.0f;
        }
        this.angle = angle;
    }
    
    public void setAxis(final String axis) {
        this.axis = axis;
    }
    
    public void setOrigin(final float x, final float y, final float z) {
        this.origin = new Float[] { x, y, z };
    }
    
    public void addOrigin(final float... offset) {
        final Float[] origin = this.origin;
        origin[0] += offset[0];
        final Float[] origin2 = this.origin;
        origin2[1] += offset[1];
        final Float[] origin3 = this.origin;
        origin3[2] += offset[2];
    }
    
    public void shrinkOrigin(final float ratio) {
        this.origin[0] = this.shrink(this.origin[0], ratio);
        this.origin[1] = this.shrink(this.origin[1], ratio);
        this.origin[2] = this.shrink(this.origin[2], ratio);
    }
    
    public Float getAngle() {
        return this.angle;
    }
    
    public String getAxis() {
        return this.axis;
    }
    
    public Float getOrigin(int i) {
        if (this.origin.length == 0) {
            return null;
        }
        i = Math.max(0, Math.min(this.origin.length - 1, i));
        return this.origin[i];
    }
    
    private float shrink(final float p, final float r) {
        return 8.0f * (1.0f - r) + r * p;
    }
}
