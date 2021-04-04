package org.telegram.ui;

class AnimationSettings {

    private static final int DEFAULT_DURATION = 200;

    // Text

    int textDuration = DEFAULT_DURATION;
    AnimationSettingBezier textInterpolationX = new AnimationSettingBezier();
    AnimationSettingBezier textInterpolationY = new AnimationSettingBezier();

    // Sticker from panel

    int stickerDuration = DEFAULT_DURATION;

    // Sticker from single text emoji

    int emojiDuration = DEFAULT_DURATION;

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
