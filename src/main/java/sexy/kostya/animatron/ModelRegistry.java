package sexy.kostya.animatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sexy.kostya.animatron.animation.FrameCompiler;
import sexy.kostya.animatron.animation.Keyframe;
import sexy.kostya.animatron.gen.FrameCompilerSerializer;
import sexy.kostya.animatron.model.Model;
import sexy.kostya.animatron.util.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ModelRegistry {

    private final static Gson GSON = new GsonBuilder().registerTypeAdapter(FrameCompiler.class, new FrameCompilerSerializer()).create();

    private final Map<String, Model> registeredModels = new HashMap<>();

    ModelRegistry() throws IOException {
        File cache = new File("animatron", "cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        for (File modelFile : cache.listFiles()) {
            registerModel(modelFile);
        }
    }

    private void registerModel(File modelFile) throws IOException {
        if (modelFile.isDirectory() || !FilenameUtils.isExtension(modelFile.getName(), "model")) {
            return;
        }
        try (final FileReader reader = new FileReader(modelFile)) {
            final String modelId = FilenameUtils.removeExtension(modelFile.getName());
            final Model  model   = GSON.fromJson(reader, Model.class);
            registeredModels.put(modelId, model);
        }
    }

    public @NotNull Collection<String> getRegisteredModels() {
        return registeredModels.keySet();
    }

    public @Nullable Model getModel(String id) {
        return registeredModels.get(id);
    }

}
