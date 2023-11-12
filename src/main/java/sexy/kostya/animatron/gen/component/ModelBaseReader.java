package sexy.kostya.animatron.gen.component;


import sexy.kostya.animatron.gen.base.ModelGenerator;
import sexy.kostya.animatron.gen.base.ModelReader;

import java.io.File;

public abstract class ModelBaseReader implements ModelReader
{
    protected File javaModelFolder;
    protected File javaTextureFolder;
    
    @Override
    public void initFolder(final ModelGenerator generator) {
        this.javaModelFolder = new File(generator.getPluginFolder(), "resource pack/assets/" + generator.getNamespace() + "/models");
        if (!this.javaModelFolder.exists()) {
            this.javaModelFolder.mkdirs();
        }
    }
}
