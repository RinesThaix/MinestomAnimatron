package sexy.kostya.animatron.animation;

import java.util.Map;

public final class Animation {

    private final LoopMode                   loopMode;
    private final int                        length;
    private final boolean                    override;
    private final Map<String, FrameCompiler> framesByBone;

    public Animation(LoopMode loopMode, int length, boolean override, Map<String, FrameCompiler> framesByBone) {
        this.loopMode = loopMode;
        this.length = length;
        this.override = override;
        this.framesByBone = framesByBone;
    }

    public LoopMode getLoopMode() {
        return loopMode;
    }

    public int getLength() {
        return length;
    }

    public Map<String, FrameCompiler> getFramesByBone() {
        return framesByBone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Animation animation = (Animation) o;

        if (length != animation.length) {
            return false;
        }
        if (loopMode != animation.loopMode) {
            return false;
        }
        return framesByBone.equals(animation.framesByBone);
    }

    @Override
    public int hashCode() {
        int result = loopMode.hashCode();
        result = 31 * result + length;
        result = 31 * result + framesByBone.hashCode();
        return result;
    }

}
