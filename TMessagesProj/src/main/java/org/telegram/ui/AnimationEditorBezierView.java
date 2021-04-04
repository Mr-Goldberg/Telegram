package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Point;

public class AnimationEditorBezierView extends View {

    private static final String TAG = "AnimationBezierView";
    private AnimationSettingBezier params = new AnimationSettingBezier();
    private Listener listener;

    private final Point controlPoint = new Point();
    private Point controlMoveShift;
    private int controlMovingIndex = 0;

    private final Path path = new Path();
    private final Paint curvePaint = new Paint();
    private final Paint controlPaint = new Paint();
    private static final float controlCircleRadius = AndroidUtilities.dp(12);
    private static final float controlCircleTouchRadius = AndroidUtilities.dp(30);

    public AnimationEditorBezierView(Context context) {
        super(context);
        init();
    }

    public AnimationEditorBezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimationEditorBezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnimationEditorBezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setColor(Color.LTGRAY);
        curvePaint.setStrokeCap(Paint.Cap.ROUND);
        curvePaint.setAntiAlias(true);
        curvePaint.setStrokeWidth(AndroidUtilities.dp(3));

        controlPaint.setStyle(Paint.Style.STROKE);
        controlPaint.setColor(Color.BLUE);
        controlPaint.setAntiAlias(true);
        controlPaint.setStrokeWidth(AndroidUtilities.dp(2));
    }

    public AnimationSettingBezier getParams() {
        return new AnimationSettingBezier(params);
    }

    public void setParams(AnimationSettingBezier params) {
        this.params = new AnimationSettingBezier(params);
        invalidate();
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 4; ++i) {
                    setPoint(i);
                    if (isOverPoint(event)) {
                        controlMoveShift = controlPoint.subtract(new Point(event.getX(), event.getY()));
                        controlMovingIndex = i;
                        if (listener != null) {
                            listener.onInterceptTouch(true);
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (controlMoveShift != null) {
                    float x = event.getX();
                    float y = event.getY();
                    float controlX = x + controlMoveShift.x;
                    float controlY = y + controlMoveShift.y;
                    float w = getWidth();
                    float h = getHeight();

                    if (controlX < 0) {
                        controlX = 0;
                    } else if (controlX > w) {
                        controlX = w;
                    }

                    if (controlY < 0) {
                        controlY = 0;
                    } else if (controlY > h) {
                        controlY = h;
                    }

                    float realX = controlX / w;
                    float realY = controlY / h;

                    switch (controlMovingIndex) {
                        case 0:
                            params.startX = realX;
                            break;
                        case 1:
                            params.x1 = realX;
                            params.y1 = realY;
                            break;
                        case 2:
                            params.x2 = realX;
                            params.y2 = realY;
                            break;
                        case 3:
                            params.endX = realX;
                            break;
                    }

                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                controlMoveShift = null;
                Log.d(TAG, "ACTION_UP: " + params);
                if (listener != null) {
                    listener.onParamsChanged(new AnimationSettingBezier(params));
                    listener.onInterceptTouch(false);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (listener != null) {
                    listener.onInterceptTouch(false);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float w = getWidth();
        final float h = getHeight();
        final AnimationSettingBezier params = this.params;

        path.reset();
        path.moveTo(params.startX * w, h);
        path.cubicTo(params.x1 * w, params.y1 * h, params.x2 * w, params.y2 * h, params.endX * w, 0);

        canvas.drawPath(path, curvePaint);

        canvas.drawCircle(params.startX * w, h - controlCircleRadius / 2, controlCircleRadius, controlPaint);
        canvas.drawCircle(params.x1 * w, params.y1 * h, controlCircleRadius, controlPaint);
        canvas.drawCircle(params.x2 * w, params.y2 * h, controlCircleRadius, controlPaint);
        canvas.drawCircle(params.endX * w, 0 + controlCircleRadius / 2, controlCircleRadius, controlPaint);
    }

    // 0-3
    private void setPoint(int index) {
        final float w = getWidth();
        final float h = getHeight();
        switch (index) {
            case 0:
                controlPoint.set(params.startX * w, h);
                break;
            case 1:
                controlPoint.set(params.x1 * w, params.y1 * h);
                break;
            case 2:
                controlPoint.set(params.x2 * w, params.y2 * h);
                break;
            case 3:
                controlPoint.set(params.endX * w, 0);
                break;
        }
    }

    private boolean isOverPoint(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return (controlPoint.x - controlCircleTouchRadius <= x &&
                x <= controlPoint.x + controlCircleTouchRadius &&
                controlPoint.y - controlCircleTouchRadius <= y &&
                y <= controlPoint.y + controlCircleTouchRadius);
    }

    public interface Listener {
        void onInterceptTouch(boolean intercept);

        void onParamsChanged(AnimationSettingBezier params);
    }
}
