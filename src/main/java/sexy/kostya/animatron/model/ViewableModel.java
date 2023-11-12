package sexy.kostya.animatron.model;

import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sexy.kostya.animatron.animation.Animation;

public interface ViewableModel {

    @NotNull Model getModel();

    int getBoneID(@NotNull String boneName);

    void colorize(int r, int g, int b);

    void colorizeBone(@NotNull String boneName, int r, int g, int b);

    void moveBone(@NotNull String boneName, Vec pos);

    void rotateBone(@NotNull String boneName, Vec rot);

    @Nullable Animation getAnimation(@NotNull String animationName);

    default void playAnimation(@NotNull String animationName) {
        playAnimation(animationName, 1F);
    }

    default void playAnimation(@NotNull String animationName, float rate) {
        final Animation animation = getAnimation(animationName);
        if (animation != null) {
            playAnimation(animation, rate);
        }
    }

    default void playAnimation(@NotNull Animation animation) {
        playAnimation(animation, 1F);
    }

    void playAnimation(@NotNull Animation animation, float rate);

    default void playAnimationExactTime(@NotNull String animationName, float timeLimitInSeconds) {
        final Animation animation = getAnimation(animationName);
        if (animation != null) {
            playAnimationExactTime(animation, timeLimitInSeconds);
        }
    }

    default void playAnimationExactTime(@NotNull Animation animation, float timeLimitInSeconds) {
        final float actualLengthInSeconds = animation.getLength() / 20F;
        final float rate;
        if (Float.compare(actualLengthInSeconds, timeLimitInSeconds) == 0) {
            rate = 1F;
        } else {
            rate = actualLengthInSeconds / timeLimitInSeconds;
        }
        playAnimation(animation, rate);
    }

    default void switchAnimation(@NotNull String fromName, @NotNull String toName) {
        switchAnimation(fromName, toName, 1F);
    }

    default void switchAnimation(@NotNull String fromName, @NotNull String toName, float rate) {
        final Animation from = getAnimation(fromName);
        final Animation to = getAnimation(toName);
        switchAnimation(from, to, rate);
    }

    default void switchAnimation(@NotNull Animation from, @NotNull Animation to) {
        switchAnimation(from, to, 1F);
    }

    void switchAnimation(@NotNull Animation from, @NotNull Animation to, float rate);

    default void stopAnimation(@NotNull String animationName) {
        final Animation animation = getAnimation(animationName);
        if (animation != null) {
            stopAnimation(animation);
        }
    }

    void stopAnimation(@NotNull Animation animation);

    void stopAllAnimations();

    void tickAnimations();

}
