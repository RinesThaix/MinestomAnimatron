package sexy.kostya.animatron.gen.element;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BBOutliner implements BBChild
{
    public String              name;
    public float[]             origin;
    public float[]             rotation;
    public UUID                uuid;
    public final List<BBChild> children;
    
    public BBOutliner() {
        this.origin = new float[] { 0.0f, 0.0f, 0.0f };
        this.rotation = new float[] { 0.0f, 0.0f, 0.0f };
        this.children = new ArrayList<>();
    }
}
