package org.telegram.ui;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * All fields are in [0;1] range.
 */
class AnimationSettingBezier {

    float startX = 0;
    float endX = 1;

    float x1 = 0.33f;
    float y1 = 1;
    float x2 = 0;
    float y2 = 0;

    public AnimationSettingBezier() {
    }

    public AnimationSettingBezier(float startX, float endX, float x1, float y1, float x2, float y2) {
        this.startX = startX;
        this.endX = endX;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public AnimationSettingBezier(AnimationSettingBezier other) {
        this.startX = other.startX;
        this.endX = other.endX;
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
    }

    public AnimationSettingBezier clone2() {
        return new AnimationSettingBezier(this);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US, "AnimationSettingBezier: start-end(%f, %f) p1(%f, %f) p2(%f, %f)", startX, endX, x1, y1, x2, y2);
    }
}
