package sexy.kostya.animatron.gen.element;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BBCube implements BBChild
{
    public String                    name;
    public float[]                   from;
    public float[]                   to;
    public final float[]             rotation;
    public final float[]             origin;
    public float                     inflate;
    public final Map<String, BBFace> faces;
    public UUID                      uuid;
    
    public BBCube() {
        this.rotation = new float[] { 0.0f, 0.0f, 0.0f };
        this.origin = new float[] { 0.0f, 0.0f, 0.0f };
        this.inflate = 0.0f;
        this.faces = new ConcurrentHashMap<String, BBFace>();
    }
    
    public String getAxis(final String partName) {
        if (this.rotation.length == 0) {
            return "x";
        }
        for (int i = 0; i < 3; ++i) {
            if (rotation[i] < 1e-5) {
                rotation[i] = 0F;
            }
        }
        if (!(this.rotation[0] != 0.0f ^ this.rotation[1] != 0.0f ^ this.rotation[2] != 0.0f) && (this.rotation[0] != 0.0f || this.rotation[1] != 0.0f)) {
//            throw new IllegalArgumentException(
//                    String.format(
//                            "Cube can not be rotated in multiple axis: [%.2f; %.2f; %.2f]",
//                            rotation[0],
//                            rotation[1],
//                            rotation[2]
//                    )
//            );
        }
        if (this.rotation[0] != 0.0f) {
            return "x";
        }
        if (this.rotation[1] != 0.0f) {
            return "y";
        }
        if (this.rotation[2] != 0.0f) {
            return "z";
        }
        return "x";
    }
}
