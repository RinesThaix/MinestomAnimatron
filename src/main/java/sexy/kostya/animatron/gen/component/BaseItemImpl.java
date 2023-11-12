package sexy.kostya.animatron.gen.component;

import sexy.kostya.animatron.gen.base.BaseItem;
import sexy.kostya.animatron.gen.component.element.ModelOverride;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseItemImpl implements BaseItem
{
    private int data;
    private final String parent = "item/generated";
    private final Map<String, String> textures;
    private final List<ModelOverride> overrides;
    
    public BaseItemImpl() {
        this.data = 0;
        this.textures = new ConcurrentHashMap<String, String>();
        this.overrides = new ArrayList<ModelOverride>();
    }
    
    @Override
    public void forceSetData(final int data) {
        this.data = data;
    }
    
    @Override
    public int addOverride(final String model) {
        this.overrides.add(new ModelOverride(++this.data, model));
        return this.data;
    }
    
    public String getParent() {
        return "item/generated";
    }
}
