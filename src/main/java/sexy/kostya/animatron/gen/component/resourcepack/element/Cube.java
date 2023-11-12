package sexy.kostya.animatron.gen.component.resourcepack.element;

import java.util.Map;
import java.util.TreeMap;

public class Cube
{
    private String name;
    private Float[] from;
    private       Float[]           to;
    private       Rotation          rotation;
    private final Map<String, Face> faces;
    
    public Cube() {
        this.name = null;
        this.from = null;
        this.to = null;
        this.rotation = null;
        this.faces = new TreeMap<String, Face>();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setFrom(final float x, final float y, final float z) {
        this.from = new Float[] { x, y, z };
    }
    
    public void setTo(final float x, final float y, final float z) {
        this.to = new Float[] { x, y, z };
    }
    
    public boolean addFrom(final String partName, final float... offsets) {
        final Float[] from = this.from;
        from[0] += offsets[0];
        final Float[] from2 = this.from;
        from2[1] += offsets[1];
        final Float[] from3 = this.from;
        from3[2] += offsets[2];
        return this.from[0] <= 32.0f && this.from[0] >= -16.0f && this.from[1] <= 32.0f && this.from[1] >= -16.0f && this.from[2] <= 32.0f && this.from[2] >= -16.0f;
    }
    
    public boolean addTo(final String partName, final float... offsets) {
        final Float[] to = this.to;
        to[0] += offsets[0];
        final Float[] to2 = this.to;
        to2[1] += offsets[1];
        final Float[] to3 = this.to;
        to3[2] += offsets[2];
        return this.to[0] <= 32.0f && this.to[0] >= -16.0f && this.to[1] <= 32.0f && this.to[1] >= -16.0f && this.to[2] <= 32.0f && this.to[2] >= -16.0f;
    }
    
    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
    }
    
    public void addFace(final String dir, final Face face) {
        this.faces.put(dir, face);
    }
    
    public void removeFace(final String dir) {
        this.faces.remove(dir);
    }
    
    public void shrinkCube(final float ratio) {
        this.from[0] = this.shrink(this.from[0], ratio);
        this.from[1] = this.shrink(this.from[1], ratio);
        this.from[2] = this.shrink(this.from[2], ratio);
        this.to[0] = this.shrink(this.to[0], ratio);
        this.to[1] = this.shrink(this.to[1], ratio);
        this.to[2] = this.shrink(this.to[2], ratio);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Float getFrom(int i) {
        if (this.from.length == 0) {
            return null;
        }
        i = Math.max(0, Math.min(this.from.length - 1, i));
        return this.from[i];
    }
    
    public Float getTo(int i) {
        if (this.to.length == 0) {
            return null;
        }
        i = Math.max(0, Math.min(this.to.length - 1, i));
        return this.to[i];
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    private float shrink(final float p, final float r) {
        return 8.0f * (1.0f - r) + r * p;
    }
}
