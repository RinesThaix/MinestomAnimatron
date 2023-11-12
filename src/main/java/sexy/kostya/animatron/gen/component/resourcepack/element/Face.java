package sexy.kostya.animatron.gen.component.resourcepack.element;

public class Face
{
    private Float[] uv;
    private int rotation;
    private String texture;
    private Integer tintindex;
    
    public Face() {
        this.uv = null;
        this.rotation = 0;
        this.texture = null;
        this.tintindex = 0;
    }
    
    public void setUV(final float ax, final float ay, final float bx, final float by) {
        this.uv = new Float[] { ax, ay, bx, by };
    }
    
    public void setRotation(final int rotation) {
        this.rotation = rotation;
    }
    
    public void setTexture(final String texture) {
        this.texture = texture;
    }
    
    public void setTintIndex(final int i) {
        this.tintindex = i;
    }
    
    public Float getUV(int i) {
        if (this.uv.length == 0) {
            return null;
        }
        i = Math.max(0, Math.min(this.uv.length - 1, i));
        return this.uv[i];
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public String getTexture() {
        return this.texture;
    }
    
    public Integer getTintIndex() {
        return this.tintindex;
    }
}
