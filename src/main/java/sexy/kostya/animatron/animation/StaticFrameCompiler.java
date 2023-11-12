package sexy.kostya.animatron.animation;

import java.util.List;

public final class StaticFrameCompiler implements FrameCompiler {

    private final List<Keyframe> frames;

    public StaticFrameCompiler(List<Keyframe> frames) {
        this.frames = frames;
    }

    @Override
    public void put(AnimationFrame.Type type, AnimationFrame frame) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyframe> compile() {
        return frames;
    }

}
