package sexy.kostya.animatron.gen.component.element;

public class ModelOverride
{
    private final Predicate predicate;
    private final String    model;
    
    public ModelOverride(final int data, final String model) {
        this.predicate = new Predicate(data);
        this.model = model;
    }
    
    public int getCustomModelData() {
        return this.predicate.getCustomModelData();
    }
    
    public String getModel() {
        return this.model;
    }
}
