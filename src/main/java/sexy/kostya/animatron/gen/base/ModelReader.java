package sexy.kostya.animatron.gen.base;

import java.io.File;

public interface ModelReader
{
    void initFolder(final ModelGenerator p0);
    
    void generate(final ModelGenerator p0, final File p1) throws Exception;
    
    boolean check(final File p0);
}
