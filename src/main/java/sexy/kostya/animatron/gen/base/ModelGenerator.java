package sexy.kostya.animatron.gen.base;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ModelGenerator
{
    public static final float largeRatio = 0.42857143f;
    public static final float smallRatio = 0.6f;
    public static final float largeDisplay = 3.7333333f;
    public static final float smallDisplay = 3.8095f;
    
    void init();
    
    void generateModels();
    
    void clearCache();
    
    File generateZipped() throws IOException;
    
    String getNamespace();
    
    Gson getGson();
    
    List<ModelReader> getReaders();
    
    File getPluginFolder();
    
    File getItemModel();
    
    File getInput();
    
    File getOutput();
    
    BaseItem getBaseItem();
    
    boolean isInit();
}
