package sexy.kostya.animatron.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sexy.kostya.animatron.animation.FrameCompiler;
import sexy.kostya.animatron.gen.base.BaseItem;
import sexy.kostya.animatron.gen.base.ModelGenerator;
import sexy.kostya.animatron.gen.base.ModelReader;
import sexy.kostya.animatron.gen.component.BaseItemImpl;
import sexy.kostya.animatron.util.FilenameUtils;
import sexy.kostya.animatron.util.IOUtils;
import sexy.kostya.animatron.util.NameFileComparator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BaseModelGenerator implements ModelGenerator {

    private final String            namespace;
    private       Gson              gson;
    private       File              resource;
    private       File              itemModel;
    private       File              inputModelFolder;
    private       File              outputModelFolder;
    private       File              zippedResourcePack;
    private       BaseItemImpl      baseItem;
    private final List<ModelReader> readers;
    private       boolean           init;

    public BaseModelGenerator(String namespace) {
        this.namespace = namespace;
        this.readers = new ArrayList<>();
        this.init = false;
    }

    @Override
    public void init() {
        this.gson = new GsonBuilder().registerTypeAdapter(FrameCompiler.class, new FrameCompilerSerializer()).create();
        this.getReaders().add(new BlockbenchReader());
        this.init = true;
    }

    @Override
    public void generateModels() {
        this.itemModel = new File(this.resource, "resource pack/assets/minecraft/models/item/leather_horse_armor.json");
        if (this.notExist(this.resource, this.inputModelFolder, this.outputModelFolder)) {
            this.initFolders();
        }
        final InputStream inputStream        = BaseModelGenerator.class.getResourceAsStream("/template/leather_horse_armor.json");
        final Reader      itemTemplateReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        this.baseItem = this.gson.fromJson(itemTemplateReader, BaseItemImpl.class);
        final File[] files = this.inputModelFolder.listFiles();
        if (files == null) {
            return;
        }
        final List<File> models  = Arrays.asList(files);
        final List<File> folders = new ArrayList<File>();
        models.sort(NameFileComparator.NAME_COMPARATOR);
        for (final File model : models) {
            if (model.isDirectory()) {
                folders.add(model);
            } else {
                boolean      hasGenerate = false;
                final String modelName   = FilenameUtils.removeExtension(model.getName()).toLowerCase();
                for (final ModelReader reader : this.getReaders()) {
                    if (!reader.check(model)) {
                        continue;
                    }
                    try {
                        reader.generate(this, model);
                        hasGenerate = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    break;
                }
                if (hasGenerate) {
                    continue;
                }
                throw new IllegalArgumentException("Failed to determine model format: " + model.getName());
            }
        }
        for (int i = 0; i < folders.size(); ++i) {
            this.getBaseItem().forceSetData((i + 1) * 1000);
            final File[] cFiles = folders.get(i).listFiles();
            if (cFiles == null) {
                return;
            }
            final List<File> cModels = Arrays.asList(cFiles);
            cModels.sort(NameFileComparator.NAME_COMPARATOR);
            for (final File model2 : cModels) {
                if (model2.isDirectory()) {
                    continue;
                }
                boolean      hasGenerate2 = false;
                final String modelName2   = FilenameUtils.removeExtension(model2.getName()).toLowerCase();
                for (final ModelReader reader2 : this.getReaders()) {
                    if (!reader2.check(model2)) {
                        continue;
                    }
                    try {
                        reader2.generate(this, model2);
                        hasGenerate2 = true;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        continue;
                    }
                    break;
                }
                if (hasGenerate2) {
                    continue;
                }
                throw new IllegalArgumentException("Failed to determine model format: " + model2.getName());
            }
        }
        try {
            final FileWriter itemWriter = new FileWriter(this.itemModel);
            itemWriter.write(getGson().toJson(this.baseItem));
            itemWriter.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    @Override
    public void clearCache() {
        if (this.notExist(this.outputModelFolder)) {
            this.initFolders();
        }
        final File[] fs = this.outputModelFolder.listFiles();
        if (fs == null) {
            return;
        }
        for (final File f : fs) {
            if (FilenameUtils.isExtension(f.getName(), "model") && !f.delete()) {
                throw new IllegalStateException("Failed to delete cache " + f.getName());
            }
        }
    }

    @Override
    public File generateZipped() throws IOException {
        if (this.notExist(this.zippedResourcePack)) {
            this.initFolders();
        }
        final File             source    = new File(this.resource, "resource pack");
        final FileOutputStream zippedFOS = new FileOutputStream(this.zippedResourcePack);
        final ZipOutputStream  zipOut    = new ZipOutputStream(zippedFOS);
        final File[]           files     = source.listFiles();
        if (files == null) {
            return null;
        }
        for (final File file : files) {
            this.zipFile(file, file.getName(), zipOut);
        }
        zipOut.close();
        zippedFOS.close();
        return this.zippedResourcePack;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public Gson getGson() {
        return this.gson;
    }

    @Override
    public List<ModelReader> getReaders() {
        return this.readers;
    }

    @Override
    public File getPluginFolder() {
        return this.resource;
    }

    @Override
    public File getItemModel() {
        return this.itemModel;
    }

    @Override
    public File getInput() {
        return this.inputModelFolder;
    }

    @Override
    public File getOutput() {
        return this.outputModelFolder;
    }

    @Override
    public BaseItem getBaseItem() {
        return this.baseItem;
    }

    @Override
    public boolean isInit() {
        return this.init;
    }

    private void initFolders() {
        this.resource = new File("animatron");
        if (!this.resource.exists() && !this.resource.mkdirs()) {
            throw new IllegalStateException("Data folder can not be generated");
        }
        this.itemModel = new File(this.resource, "resource pack/assets/minecraft/models/item/leather_horse_armor.json");
        if (!this.itemModel.getParentFile().exists() && !this.itemModel.getParentFile().mkdirs()) {
            throw new IllegalStateException("leather_horse_armor.json's folder can not be generated");
        }
        final File packpng = new File(this.resource, "resource pack/pack.png");
        if (!packpng.exists()) {
            try {
                final OutputStream writer = new FileOutputStream(packpng);
                final InputStream  reader = BaseModelGenerator.class.getResourceAsStream("/pack.png");
                if (reader != null) {
                    IOUtils.copy(reader, writer);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final File packmeta = new File(this.resource, "resource pack/pack.mcmeta");
        if (!packmeta.exists()) {
            try {
                final OutputStream writer2 = new FileOutputStream(packmeta);
                final InputStream  reader2 = BaseModelGenerator.class.getResourceAsStream("/pack.mcmeta");
                if (reader2 != null) {
                    IOUtils.copy(reader2, writer2);
                }
                writer2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        this.inputModelFolder = new File(this.resource, "blueprints");
        if (!this.inputModelFolder.exists() && !this.inputModelFolder.mkdirs()) {
            throw new IllegalStateException("Blueprints folder can not be generated");
        }
        this.outputModelFolder = new File(this.resource, "cache");
        if (!this.outputModelFolder.exists() && !this.outputModelFolder.mkdirs()) {
            throw new IllegalStateException("Cache folder can not be generated");
        }
        this.zippedResourcePack = new File(this.resource, "resource pack.zip");
        for (final ModelReader reader3 : this.readers) {
            reader3.initFolder(this);
        }
    }

    private boolean notExist(final File... files) {
        for (final File file : files) {
            if (file == null || !file.exists()) {
                return true;
            }
        }
        return false;
    }

    private void zipFile(final File fileToZip, final String fileName, final ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            final File[] children = fileToZip.listFiles();
            if (children != null) {
                for (final File childFile : children) {
                    this.zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        final FileInputStream fis      = new FileInputStream(fileToZip);
        final ZipEntry        zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        final byte[] bytes = new byte[1024];
        int          length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

}
