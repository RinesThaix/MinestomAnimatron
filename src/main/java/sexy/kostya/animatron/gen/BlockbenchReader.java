package sexy.kostya.animatron.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sexy.kostya.animatron.gen.base.ModelGenerator;
import sexy.kostya.animatron.gen.component.ModelBaseReader;
import sexy.kostya.animatron.gen.component.resourcepack.JavaElement;
import sexy.kostya.animatron.gen.element.BBTexture;
import sexy.kostya.animatron.model.Model;
import sexy.kostya.animatron.util.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class BlockbenchReader extends ModelBaseReader {

    private final Gson gson;

    public BlockbenchReader() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(BlockbenchModel.class, new BlockbenchDeserializer())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void initFolder(final ModelGenerator gen) {
        super.initFolder(gen);
        this.javaTextureFolder = new File(gen.getPluginFolder(), "resource pack/assets/");
        if (!this.javaTextureFolder.exists() && !this.javaTextureFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create assets folder");
        }
    }

    @Override
    public void generate(final ModelGenerator gen, final File file) throws Exception {
        final String          modelName   = FilenameUtils.removeExtension(file.getName()).toLowerCase();
        final FileReader      modelReader = new FileReader(file);
        final BlockbenchModel bbmodel     = this.gson.fromJson(modelReader, BlockbenchModel.class);
        modelReader.close();
        if (bbmodel.boxUV) {
            throw new IllegalArgumentException("Box UV is not supported yet");
        }
        if (bbmodel.textures.isEmpty()) {
            throw new IllegalArgumentException("Missing textures");
        }
        final BlockbenchWorker blockbenchWorker = new BlockbenchWorker(gen, bbmodel);
        final Model            apiModel         = new Model();
        final File             modelFolder      = this.initSubFolder(this.javaModelFolder, modelName);
        for (final JavaElement part : blockbenchWorker.getParts()) {
            final File modelFile = new File(modelFolder, part.getFileName() + ".json");
            if (!modelFile.exists()) {
                final FileWriter modelWriter = new FileWriter(modelFile);
                modelWriter.write(gen.getGson().toJson(part));
                modelWriter.close();
            }
            apiModel.addItemModelID(part.getFileName(), gen.getBaseItem().addOverride(gen.getNamespace() + ":" + modelName + "/" + part.getFileName()));
        }
        apiModel.setBones(blockbenchWorker.getBones());
        apiModel.reassignId();
        apiModel.setBoundingBox(blockbenchWorker.getWidth(), blockbenchWorker.getHeight());
        for (final String boneId : blockbenchWorker.getSubBoundingBox().keySet()) {
            final Float[] size = blockbenchWorker.getSubBoundingBox().get(boneId);
            apiModel.setSubBoundingBox(boneId, size[0], size[1], size[2]);
        }
        apiModel.setEyeHeight(blockbenchWorker.getEyeHeight());
        apiModel.setAnimation(bbmodel.animations);
        for (final BBTexture texture : bbmodel.textures) {
            if (!texture.name.equals(texture.name.toLowerCase())) {
                throw new IllegalArgumentException(String.format(
                        "Invalid directory for texture %s of model %s",
                        texture.name,
                        modelName
                ));
            }
            if (texture.namespace.equals("minecraft") || !texture.namespace.equals(gen.getNamespace())) {
                if (texture.namespace.equals(texture.namespace.toLowerCase())) {
                    continue;
                }
                throw new IllegalArgumentException(String.format(
                        "Invalid namespace for texture %s of model %s: %s",
                        texture.name,
                        modelName,
                        texture.namespace
                ));
            } else {
                final File tex = new File(this.javaTextureFolder, String.format("%s/textures/%s", texture.namespace, texture.folder));
                if (!tex.exists() && !tex.mkdirs()) {
                    throw new IllegalStateException("Failed to create texture folder.");
                } else {
                    final File png = new File(tex, String.format("%s.png", texture.name));
                    ImageIO.write(texture.texture, "png", png);

                    final String mcmeta;
                    if (bbmodel.mcmetas.containsKey(texture.uuid)) {
                        mcmeta = bbmodel.mcmetas.get(texture.uuid);
                    } else if (texture.frametime != 0F) {
                        mcmeta = String.format("{\"animation\": {\"frametime\": %s}}", texture.frametime);
                    } else {
                        continue;
                    }
                    final FileWriter mcmetaWriter = new FileWriter(new File(tex, String.format("%s.png.mcmeta", texture.name)));
                    mcmetaWriter.write(mcmeta);
                    mcmetaWriter.close();
                }
            }
        }
        final FileWriter apiModelWriter = new FileWriter(new File(gen.getOutput(), modelName + ".model"));
        apiModelWriter.write(gen.getGson().toJson(apiModel));
        apiModelWriter.close();
    }

    @Override
    public boolean check(final File file) {
        return !file.isDirectory() && FilenameUtils.isExtension(file.getName(), "bbmodel");
    }

    private File initSubFolder(final File file, final String modelName) {
        final File folder = new File(file, modelName);
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdirs();
        return folder;
    }

}
