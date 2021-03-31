/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import org.telegram.messenger.AndroidUtilities;

public abstract class BaseCell extends ViewGroup {

    private final class CheckForTap implements Runnable {
        public void run() {
            if (pendingCheckForLongPress == null) {
                pendingCheckForLongPress = new CheckForLongPress();
            }
            pendingCheckForLongPress.currentPressCount = ++pressCount;
            postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        public void run() {
            if (checkingForLongPress && getParent() != null && currentPressCount == pressCount) {
                checkingForLongPress = false;
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                onLongPress();
                MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                onTouchEvent(event);
                event.recycle();
            }
        }
    }

    private boolean checkingForLongPress = false;
    private CheckForLongPress pendingCheckForLongPress = null;
    private int pressCount = 0;
    private CheckForTap pendingCheckForTap = null;
    public final CellDrawingView cellDrawingView;

    public BaseCell(Context context) {
        super(context);
        setWillNotDraw(false);
        setFocusable(true);

        AndroidUtilities.disableClip(this);
        cellDrawingView = new CellDrawingView(context);
        cellDrawingView.cellContainer = this;
        this.addView(cellDrawingView);
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y) {
        setDrawableBounds(drawable, x, y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y) {
        setDrawableBounds(drawable, (int) x, (int) y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds(x, y, x + w, y + h);
        }
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds((int) x, (int) y, (int) x + w, (int) y + h);
        }
    }

    protected void startCheckLongPress() {
        if (checkingForLongPress) {
            return;
        }
        checkingForLongPress = true;
        if (pendingCheckForTap == null) {
            pendingCheckForTap = new CheckForTap();
        }
        postDelayed(pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }

    protected void cancelCheckLongPress() {
        checkingForLongPress = false;
        if (pendingCheckForLongPress != null) {
            removeCallbacks(pendingCheckForLongPress);
        }
        if (pendingCheckForTap != null) {
            removeCallbacks(pendingCheckForTap);
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    protected void onLongPress() {

    }

    //
    // CellDrawingView
    //

    private static final String TAG = "BaseCell";

    public void restoreContainer() {
        if (cellDrawingView.getParent() != this) {
            this.addView(cellDrawingView);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cellDrawingView.invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.d(TAG, "onLayout() " + left + " " + top + " " + right + " " + bottom);

        if (cellDrawingView.getParent() != this) return;

        cellDrawingView.setMatchCellContainer();
    }

    protected void onCellDraw(Canvas canvas) {
    }

    public static class CellDrawingView extends View {

        BaseCell cellContainer;
        public ValueAnimator animator;

        CellDrawingView(Context context) {
            super(context);
        }

        private void setMatchCellContainer() {
            setLeft(0);
            setTop(0);
            setRight(cellContainer.getMeasuredWidth());
            setBottom(cellContainer.getMeasuredHeight());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (cellContainer != null) {
                cellContainer.onCellDraw(canvas);
            }
        }
    }
}
