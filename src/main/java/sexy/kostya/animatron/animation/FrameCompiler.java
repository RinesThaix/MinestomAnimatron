package sexy.kostya.animatron.animation;

import java.util.List;

public interface FrameCompiler {

    void put(AnimationFrame.Type type, AnimationFrame frame);

    List<Keyframe> compile();

}
