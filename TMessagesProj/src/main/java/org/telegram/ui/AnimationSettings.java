package org.telegram.ui;

public class AnimationSettings {

    private static final int DEFAULT_DURATION = 200;

    // Text

    public int textDuration = DEFAULT_DURATION;
    public AnimationSettingBezier textInterpolationX = new AnimationSettingBezier();
    public AnimationSettingBezier textInterpolationY = new AnimationSettingBezier();

    // Sticker from panel

    public int stickerDuration = DEFAULT_DURATION;

    // Sticker from single text emoji

    public int emojiDuration = DEFAULT_DURATION;

    AnimationSettings() {}

    AnimationSettings(AnimationSettings other) {
        this.textDuration = other.textDuration;
        this.textInterpolationX = other.textInterpolationX.clone2();
        this.textInterpolationY = other.textInterpolationY.clone2();

        stickerDuration = other.stickerDuration;

        emojiDuration = other.emojiDuration;
    }

    AnimationSettings clone2() {
        return new AnimationSettings(this);
    }
}
