package sexy.kostya.animatron.gen;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minestom.server.coordinate.Vec;
import sexy.kostya.animatron.animation.AnimationFrame;
import sexy.kostya.animatron.animation.FrameCompiler;
import sexy.kostya.animatron.animation.Keyframe;
import sexy.kostya.animatron.animation.StaticFrameCompiler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FrameCompilerSerializer implements JsonSerializer<FrameCompiler>, JsonDeserializer<FrameCompiler> {

    private final static AnimationFrame.Type[] TYPES = AnimationFrame.Type.values();

    @Override
    public FrameCompiler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final List<Keyframe> frames = new ArrayList<>();
        final JsonArray array = json.getAsJsonArray();
        for (JsonElement el : array) {
            final JsonArray frame = el.getAsJsonArray();
            final Vec[] values = new Vec[TYPES.length];
            for (int i = 0; i < TYPES.length; ++i) {
                final JsonArray type = frame.get(i).getAsJsonArray();
                values[i] = new Vec(type.get(0).getAsDouble(), type.get(1).getAsDouble(), type.get(2).getAsDouble());
            }
            frames.add(new Keyframe(values));
        }
        return new StaticFrameCompiler(frames);
    }

    @Override
    public JsonElement serialize(FrameCompiler src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonArray result = new JsonArray();
        for (Keyframe frame : src.compile()) {
            final JsonArray frameArray = new JsonArray();
            for (AnimationFrame.Type type : TYPES) {
                final JsonArray typeArray = new JsonArray();
                final Vec value = frame.getValue(type);
                typeArray.add(value.x());
                typeArray.add(value.y());
                typeArray.add(value.z());
                frameArray.add(typeArray);
            }
            result.add(frameArray);
        }
        return result;
    }

}
