package sexy.kostya.animatron.gen.component.resourcepack.element;

public class Display
{
    private final float[] translation;
    private final float[] rotation;
    private final float[] scale;
    
    public Display() {
        this.translation = new float[] { 0.0f, -6.4f, 0.0f };
        this.rotation = new float[] { 0.0f, 0.0f, 0.0f };
        this.scale = new float[3];
    }
    
    public void moveTranslation(final String partName, final float... offsets) {
        final float[] translation = this.translation;
        final int n = 0;
        translation[n] -= offsets[0] * this.scale[0];
        final float[] translation2 = this.translation;
        final int n2 = 1;
        translation2[n2] -= offsets[1] * this.scale[1];
        final float[] translation3 = this.translation;
        final int n3 = 2;
        translation3[n3] -= offsets[2] * this.scale[2];
        if (!this.checkTranslation()) {
            throw new IllegalArgumentException("Invalid translation for display: " + translation[0] + ", " + translation[1] + ", " + translation[2] + ".");
        }
    }
    
    public void setTranslation(final float x, final float y, final float z) {
        this.translation[0] = x;
        this.translation[1] = y;
        this.translation[2] = z;
    }
    
    public void setRotation(final float x, final float y, final float z) {
        this.rotation[0] = x;
        this.rotation[1] = y;
        this.rotation[2] = z;
    }
    
    public void setScale(final float x, final float y, final float z) {
        this.scale[0] = x;
        this.scale[1] = y;
        this.scale[2] = z;
    }
    
    public boolean checkTranslation() {
        return Math.abs(this.translation[0]) <= 80.0f && Math.abs(this.translation[1]) <= 80.0f && Math.abs(this.translation[2]) <= 80.0f;
    }
    
    public float[] getTranslation() {
        return this.translation;
    }
    
    public float[] getScale() {
        return this.scale;
    }
}
