package sexy.kostya.animatron;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sexy.kostya.animatron.gen.BaseModelGenerator;
import sexy.kostya.animatron.gen.base.ModelGenerator;
import sexy.kostya.animatron.integration.ViewManager;

import java.io.File;
import java.io.IOException;

public final class ModelEngine {

    public static @NotNull ModelRegistry registry() throws IOException {
        return new ModelRegistry();
    }

    public static @Nullable File generate(String namespace, boolean generateZip) throws IOException {
        final ModelGenerator generator = new BaseModelGenerator(namespace);
        generator.init();
        generator.clearCache();
        generator.generateModels();
        if (generateZip) {
            return generator.generateZipped();
        } else {
            return null;
        }
    }

    public static @NotNull ViewManager viewManager() {
        return ViewManager.INSTANCE;
    }

}
