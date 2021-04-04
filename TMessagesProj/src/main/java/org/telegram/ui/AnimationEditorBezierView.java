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

    private static final String TAG = "AnimationEditorBezierVi";
    private AnimationSettingBezier params = new AnimationSettingBezier();
    private Listener listener;

    private final Point[] controlPoints = new Point[4];
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

        for (int i = 0; i < 4; ++i) {
            controlPoints[i] = new Point();
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int pointIndex = getPointUnderTouch(event);
                if (pointIndex >= 0) {
                    controlMoveShift = controlPoints[pointIndex].subtract(new Point(event.getX(), event.getY()));
                    controlMovingIndex = pointIndex;
                    if (listener != null) {
                        listener.onInterceptTouch(true);
                    }
                    return true;
                }
                break;
            }
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

        setPoints();
        Point[] pts = controlPoints;

        path.reset();
        path.moveTo(pts[0].x, pts[0].y);
        path.cubicTo(pts[1].x, pts[1].y, pts[2].x, pts[2].y, pts[3].x, pts[3].y);
        canvas.drawPath(path, curvePaint);

        canvas.drawCircle(pts[0].x, pts[0].y + controlCircleRadius / 2, controlCircleRadius, controlPaint);
        canvas.drawCircle(pts[1].x, pts[1].y, controlCircleRadius, controlPaint);
        canvas.drawCircle(pts[2].x, pts[2].y, controlCircleRadius, controlPaint);
        canvas.drawCircle(pts[3].x, pts[3].y - controlCircleRadius / 2, controlCircleRadius, controlPaint);
    }

    private void setPoints() {
        final float w = getWidth();
        final float h = getHeight();
        controlPoints[0].set(params.startX * w, 0);
        controlPoints[1].set(params.x1 * w, params.y1 * h);
        controlPoints[2].set(params.x2 * w, params.y2 * h);
        controlPoints[3].set(params.endX * w, h);
    }

    private int getPointUnderTouch(MotionEvent event) {
        for (int i = 0; i < 4; ++i) {
            if (isOverPoint(controlPoints[i], event)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isOverPoint(Point p, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return (p.x - controlCircleTouchRadius <= x &&
                x <= p.x + controlCircleTouchRadius &&
                p.y - controlCircleTouchRadius <= y &&
                y <= p.y + controlCircleTouchRadius);
    }

    public interface Listener {
        void onInterceptTouch(boolean intercept);

        void onParamsChanged(AnimationSettingBezier params);
    }
}
