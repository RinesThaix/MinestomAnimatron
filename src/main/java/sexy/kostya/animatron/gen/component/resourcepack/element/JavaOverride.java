package sexy.kostya.animatron.gen.component.resourcepack.element;

public class JavaOverride
{
    private Predicate predicate;
    private String    model;
    
    public JavaOverride(final int data, final String model) {
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
