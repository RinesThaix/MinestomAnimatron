package sexy.kostya.animatron.gen;

import sexy.kostya.animatron.animation.Animation;
import sexy.kostya.animatron.gen.element.BBChild;
import sexy.kostya.animatron.gen.element.BBCube;
import sexy.kostya.animatron.gen.element.BBTexture;

import java.util.*;

public class BlockbenchModel {

    public       boolean                boxUV;
    public       String                 name;
    public       int                    width;
    public       int                    height;
    public final List<BBCube>           cubes;
    public final List<BBChild>          elements;
    public final List<BBTexture>        textures;
    public final Map<String, String>    mcmetas;
    public final Map<String, Animation> animations;

    public BlockbenchModel() {
        this.cubes = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.textures = new ArrayList<>();
        this.mcmetas = new HashMap<>();
        this.animations = new LinkedHashMap<>();
    }

}
