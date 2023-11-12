package sexy.kostya.animatron.gen.component.resourcepack;

import sexy.kostya.animatron.gen.component.resourcepack.element.Cube;
import sexy.kostya.animatron.gen.component.resourcepack.element.Display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JavaElement
{
    private transient String           file_name;
    private transient Integer[]        texture_size;
    private final Map<String, String>  textures;
    private final List<Cube>           elements;
    private final Map<String, Display> display;
    
    public JavaElement() {
        this.texture_size = null;
        this.textures = new ConcurrentHashMap<String, String>();
        this.elements = new ArrayList<Cube>();
        this.display = new ConcurrentHashMap<String, Display>();
        final Display head = new Display();
        this.display.put("head", head);
        head.setScale(3.7333333f, 3.7333333f, 3.7333333f);
    }
    
    public void setFileName(final String file) {
        this.file_name = file.toLowerCase();
    }
    
    public void setTextureSize(final int sizeX, final int sizeY) {
        this.texture_size = new Integer[] { sizeX, sizeY };
    }
    
    public void addTexture(final String name, final String location) {
        this.textures.put(name, location);
    }
    
    public void removeTexture(final String name) {
        this.textures.remove(name);
    }
    
    public void addElements(final Cube cube) {
        this.elements.add(cube);
    }
    
    public boolean normalize(final String partName) {
        final float[]         offset = { 0.0f, 0.0f, 0.0f };
        final Map<Cube, Cube> cubes  = new HashMap<Cube, Cube>();
        for (final Cube original : this.elements) {
            final Cube cube = new Cube();
            cube.setFrom(original.getFrom(0), original.getFrom(1), original.getFrom(2));
            cube.setTo(original.getTo(0), original.getTo(1), original.getTo(2));
            cube.shrinkCube(0.6f);
            cubes.put(cube, original);
            for (int i = 0; i < 3; ++i) {
                if (cube.getFrom(i) + offset[i] > 32.0f) {
                    final float[] array = offset;
                    final int n = i;
                    array[n] -= cube.getFrom(i) + offset[i] - 32.0f;
                }
                if (cube.getFrom(i) + offset[i] < -16.0f) {
                    final float[] array2 = offset;
                    final int n2 = i;
                    array2[n2] -= cube.getFrom(i) + offset[i] + 16.0f;
                }
                if (cube.getTo(i) + offset[i] > 32.0f) {
                    final float[] array3 = offset;
                    final int n3 = i;
                    array3[n3] -= cube.getTo(i) + offset[i] - 32.0f;
                }
                if (cube.getTo(i) + offset[i] < -16.0f) {
                    final float[] array4 = offset;
                    final int n4 = i;
                    array4[n4] -= cube.getTo(i) + offset[i] + 16.0f;
                }
            }
        }
        for (final Cube cube2 : cubes.keySet()) {
            final boolean from = cube2.addFrom(partName, offset);
            final boolean to = cube2.addTo(partName, offset);
            if (!from || !to) {
                return this.shrinkLarge(partName);
            }
        }
        for (final Cube cube2 : cubes.keySet()) {
            final Cube original2 = cubes.get(cube2);
            original2.setFrom(cube2.getFrom(0), cube2.getFrom(1), cube2.getFrom(2));
            original2.setTo(cube2.getTo(0), cube2.getTo(1), cube2.getTo(2));
            original2.getRotation().shrinkOrigin(0.6f);
            original2.getRotation().addOrigin(offset);
        }
        if (offset[0] != 0.0f || offset[1] != 0.0f || offset[2] != 0.0f) {
            this.display.get("head").moveTranslation(partName, offset);
        }
        this.display.get("head").setScale(3.8095f, 3.8095f, 3.8095f);
        return true;
    }
    
    public String getFileName() {
        return this.file_name;
    }
    
    public int getTextureSizeX() {
        return (this.texture_size == null) ? 0 : this.texture_size[0];
    }
    
    public int getTextureSizeY() {
        return (this.texture_size == null) ? 0 : this.texture_size[1];
    }
    
    protected boolean shrinkLarge(final String partName) {
        final float[] offset = { 0.0f, 0.0f, 0.0f };
        for (final Cube cube : this.elements) {
            cube.shrinkCube(0.42857143f);
            for (int i = 0; i < 3; ++i) {
                if (cube.getFrom(i) + offset[i] > 32.0f) {
                    final float[] array = offset;
                    final int n = i;
                    array[n] -= cube.getFrom(i) + offset[i] - 32.0f;
                }
                if (cube.getFrom(i) + offset[i] < -16.0f) {
                    final float[] array2 = offset;
                    final int n2 = i;
                    array2[n2] -= cube.getFrom(i) + offset[i] + 16.0f;
                }
                if (cube.getTo(i) + offset[i] > 32.0f) {
                    final float[] array3 = offset;
                    final int n3 = i;
                    array3[n3] -= cube.getTo(i) + offset[i] - 32.0f;
                }
                if (cube.getTo(i) + offset[i] < -16.0f) {
                    final float[] array4 = offset;
                    final int n4 = i;
                    array4[n4] -= cube.getTo(i) + offset[i] + 16.0f;
                }
            }
        }
        for (final Cube cube : this.elements) {
            if (cube.addFrom(partName, offset) && cube.addTo(partName, offset)) {
                cube.getRotation().shrinkOrigin(0.42857143f);
                cube.getRotation().addOrigin(offset);
            }
        }
        if (offset[0] != 0.0f || offset[1] != 0.0f || offset[2] != 0.0f) {
            this.display.get("head").moveTranslation(partName, offset);
        }
        this.display.get("head").setScale(3.7333333f, 3.7333333f, 3.7333333f);
        return false;
    }
}
