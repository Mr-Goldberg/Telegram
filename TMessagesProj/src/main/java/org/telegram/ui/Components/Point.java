/*
 * This is the source code of Telegram for Android v. 5.x.x
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Components;

import androidx.annotation.NonNull;

public class Point {
    public float x;
    public float y;

    public Point() {

    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point add(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    public void addToSelf(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public Point subtract(Point p2) {
        return new Point(x - p2.x, y - p2.y);
    }

    public Point subtract(int x, int y) {
        return new Point(this.x - x, this.y - y);
    }

    @NonNull
    @Override
    public String toString() {
        return "TGPoint(" + x + "," + y + ")";
    }
}
