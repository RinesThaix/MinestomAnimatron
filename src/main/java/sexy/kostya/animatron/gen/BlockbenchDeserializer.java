package sexy.kostya.animatron.gen;

import com.google.gson.*;
import net.minestom.server.coordinate.Vec;
import sexy.kostya.animatron.animation.*;
import sexy.kostya.animatron.gen.element.BBCube;
import sexy.kostya.animatron.gen.element.BBOutliner;
import sexy.kostya.animatron.gen.element.BBTexture;
import sexy.kostya.animatron.util.Base64Image;
import sexy.kostya.animatron.util.FilenameUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockbenchDeserializer implements JsonDeserializer<BlockbenchModel>
{
    private final Gson              gson;
    private final Map<UUID, BBCube> cubes;
    private final Map<UUID, String> uuidPart;
    private int                     id;
    
    public BlockbenchDeserializer() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(BBOutliner.class, new BBOutlinerDeserializer())
                .registerTypeAdapter(BBTexture.class, new BBTextureDeserializer())
                .registerTypeAdapter(Animation.class, new BBAnimationDeserializer()).create();
        this.cubes = new ConcurrentHashMap<>();
        this.uuidPart = new ConcurrentHashMap<>();
        this.id = 0;
    }
    
    public BlockbenchModel deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final BlockbenchModel model = new BlockbenchModel();
        final JsonObject jObj = json.getAsJsonObject();
        if (jObj.has("meta")) {
            final JsonObject meta = jObj.get("meta").getAsJsonObject();
            model.boxUV = meta.get("box_uv").getAsBoolean();
        }
        if (jObj.has("name")) {
            model.name = jObj.get("name").getAsString();
        }
        if (jObj.has("resolution")) {
            final JsonObject size = jObj.get("resolution").getAsJsonObject();
            model.width = size.get("width").getAsInt();
            model.height = size.get("height").getAsInt();
        }
        if (jObj.has("elements")) {
            final JsonElement ele = jObj.get("elements");
            if (ele.isJsonArray()) {
                final BBCube[] bbc = this.gson.fromJson(ele, BBCube[].class);
                for (final BBCube cube : bbc) {
                    model.cubes.add(cube);
                    this.cubes.put(cube.uuid, cube);
                }
            }
        }
        if (jObj.has("outliner")) {
            final JsonElement out = jObj.get("outliner");
            if (out.isJsonArray()) {
                final JsonArray array = out.getAsJsonArray();
                for (final JsonElement ele2 : array) {
                    try {
                        final UUID uuid = this.gson.fromJson(ele2, UUID.class);
                        model.elements.add(this.cubes.get(uuid));
                    }
                    catch (JsonSyntaxException ignored) {
                        final BBOutliner outliner = this.gson.fromJson(ele2, BBOutliner.class);
                        this.uuidPart.put(outliner.uuid, outliner.name);
                        model.elements.add(outliner);
                    }
                }
            }
        }
        if (jObj.has("textures")) {
            final JsonElement tex = jObj.get("textures");
            if (tex.isJsonArray()) {
                final JsonArray array = tex.getAsJsonArray();
                for (final JsonElement ele2 : array) {
                    final BBTexture texture = this.gson.fromJson(ele2, BBTexture.class);
                    model.textures.add(texture);
                }
            }
        }
        if (jObj.has("mcmetas")) {
            final JsonElement meta2 = jObj.get("mcmetas");
            if (meta2.isJsonObject()) {
                final JsonObject object = meta2.getAsJsonObject();
                for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    final String uuid2 = entry.getKey();
                    final String data = entry.getValue().toString();
                    model.mcmetas.put(uuid2, data);
                }
            }
        }
        if (jObj.has("animations")) {
            final JsonElement ani = jObj.get("animations");
            if (ani.isJsonArray()) {
                final JsonArray array = ani.getAsJsonArray();
                for (final JsonElement ele2 : array) {
                    final Animation animation = this.gson.fromJson(ele2, Animation.class);
                    final JsonObject eleObj = ele2.getAsJsonObject();
                    if (eleObj.has("name")) {
                        model.animations.put(eleObj.get("name").getAsString(), animation);
                    }
                }
            }
        }
        this.cubes.clear();
        this.uuidPart.clear();
        this.id = 0;
        return model;
    }
    
    private class BBOutlinerDeserializer implements JsonDeserializer<BBOutliner>
    {
        public BBOutliner deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final BBOutliner outliner = new BBOutliner();
            final JsonObject jObj     = json.getAsJsonObject();
            if (jObj.has("name")) {
                outliner.name = jObj.get("name").getAsString();
            }
            if (jObj.has("origin")) {
                outliner.origin = BlockbenchDeserializer.this.gson.fromJson(jObj.get("origin"), float[].class);
            }
            if (jObj.has("rotation")) {
                outliner.rotation = BlockbenchDeserializer.this.gson.fromJson(jObj.get("rotation"), float[].class);
            }
            if (jObj.has("uuid")) {
                outliner.uuid = UUID.fromString(jObj.get("uuid").getAsString());
            }
            if (jObj.has("children")) {
                final JsonElement out = jObj.get("children");
                if (out.isJsonArray()) {
                    final JsonArray array = out.getAsJsonArray();
                    for (final JsonElement ele : array) {
                        try {
                            final UUID uuid = BlockbenchDeserializer.this.gson.fromJson(ele, UUID.class);
                            outliner.children.add(BlockbenchDeserializer.this.cubes.get(uuid));
                        }
                        catch (JsonSyntaxException ignored) {
                            final BBOutliner sub = BlockbenchDeserializer.this.gson.fromJson(ele, BBOutliner.class);
                            BlockbenchDeserializer.this.uuidPart.put(sub.uuid, sub.name);
                            outliner.children.add(sub);
                        }
                    }
                }
            }
            return outliner;
        }
    }
    
    private class BBTextureDeserializer implements JsonDeserializer<BBTexture>
    {
        public BBTexture deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final BBTexture  texture = new BBTexture();
            final JsonObject jObj    = json.getAsJsonObject();
            if (jObj.has("name")) {
                texture.name = FilenameUtils.removeExtension(jObj.get("name").getAsString()).toLowerCase();
            }
            if (jObj.has("folder")) {
                texture.folder = jObj.get("folder").getAsString();
            }
            if (jObj.has("namespace")) {
                String namespace = jObj.get("namespace").getAsString();
                if (namespace.equals("minecraft")) {
                    texture.namespace = namespace;
                } else {
                    texture.namespace = "";
                    if (namespace.isEmpty()) {
                        namespace = "generic";
                    }
                    texture.name = namespace + "__" + texture.name;
                }
            }
            if (jObj.has("frametime")) {
                texture.frametime = jObj.get("frametime").getAsFloat();
            }
            texture.id = Integer.toString(BlockbenchDeserializer.this.id++);
            texture.uuid = jObj.get("uuid").getAsString();
            if (jObj.has("source")) {
                final String source = jObj.get("source").getAsString();
                texture.texture = Base64Image.toImage(source);
            }
            return texture;
        }
    }
    
    private class BBAnimationDeserializer implements JsonDeserializer<Animation>
    {
        public Animation deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jObj = json.getAsJsonObject();
            LoopMode loopMode = LoopMode.ONCE;
            if (jObj.has("loop")) {
                loopMode = LoopMode.valueOf(jObj.get("loop").getAsString().toUpperCase(Locale.ROOT));
            }
            boolean override = false;
            if (jObj.has("override")) {
                override = jObj.get("override").getAsBoolean();
            }
            int length = 0;
            if (jObj.has("length")) {
                length = Math.round(20 * jObj.get("length").getAsFloat());
            }
            Map<String, FrameCompiler> framesByBone = new HashMap<>();
            if (jObj.has("animators")) {
                final JsonObject animators = jObj.get("animators").getAsJsonObject();
                for (final UUID uuid : BlockbenchDeserializer.this.uuidPart.keySet()) {
                    if (!animators.has(uuid.toString())) {
                        continue;
                    }
                    final JsonObject partTimeline = animators.get(uuid.toString()).getAsJsonObject();
                    final FrameCompiler compiler = getFrameCompiler(partTimeline);
                    framesByBone.put(uuidPart.get(uuid), compiler);
                }
            }
            return new Animation(
                    loopMode,
                    length,
                    override,
                    framesByBone
            );
        }
        
        private FrameCompiler getFrameCompiler(final JsonObject partTimeline) {
            final FrameCompiler result = new PrecalculatedFrameCompiler();
            if (partTimeline.has("keyframes")) {
                final JsonElement keyframes = partTimeline.get("keyframes");
                if (keyframes.isJsonArray()) {
                    final JsonArray array = keyframes.getAsJsonArray();
                    for (final JsonElement ele : array) {
                        final JsonObject key = ele.getAsJsonObject();
                        if (key.has("channel") && key.has("data_points")) {
                            if (!key.has("time")) {
                                continue;
                            }
                            final JsonObject data = key.get("data_points").getAsJsonArray().get(0).getAsJsonObject();
                            final float time = key.get("time").getAsFloat();
                            final boolean smooth = !key.get("interpolation").getAsString().equals("linear");
                            final AnimationFrame.Type type = AnimationFrame.Type.valueOf(key.get("channel").getAsString().toUpperCase(Locale.ROOT));

                            Vec value = new Vec(
                                    parse(data, "x"),
                                    parse(data, "y"),
                                    parse(data, "z")
                            );

                            result.put(type, new AnimationFrame(
                                    Math.round(20 * time),
                                    value,
                                    smooth ? Interpolation.CATMULLROM : Interpolation.LINEAR
                            ));
                        }
                    }
                }
            }
            return result;
        }

        private double parse(JsonObject data, String key) {
            if (!data.has(key)) {
                return 0.0;
            }
            final JsonElement el = data.get(key);
            if (!el.isJsonPrimitive()) {
                return 0.0;
            }
            final JsonPrimitive primitive = el.getAsJsonPrimitive();
            if (primitive.isString()) {
                final String value = primitive.getAsString().replace(",", ".").trim();
                if (value.isEmpty()) {
                    return 0.0;
                }
                return Double.parseDouble(value);
            } else {
                return primitive.getAsDouble();
            }
        }
    }
}
