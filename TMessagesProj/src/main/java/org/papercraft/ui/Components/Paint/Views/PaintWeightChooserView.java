package org.papercraft.ui.Components.Paint.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;

import org.papercraft.messenger.AndroidUtilities;
import org.papercraft.ui.Components.AnimatedFloat;
import org.papercraft.ui.Components.CubicBezierInterpolator;
import org.papercraft.ui.Components.Paint.RenderView;
import org.papercraft.ui.Components.Paint.Swatch;

public class PaintWeightChooserView extends View {
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    private GestureDetectorCompat gestureDetector;

    private RectF touchRect = new RectF();

    private boolean isTouchInProgress;
    private boolean isViewHidden;
    private float hideProgress;
    private float showProgress;
    private long lastUpdate;

    private boolean showPreview = true;

    private AnimatedFloat animatedWeight = new AnimatedFloat(this);
    private AnimatedFloat animatedMin = new AnimatedFloat(this);
    private AnimatedFloat animatedMax = new AnimatedFloat(this);

    private RenderView renderView;
    private float min, max;
    private Swatch colorSwatch = new Swatch(Color.WHITE, 1f, 0.016773745f);
    private Runnable onUpdate;
    private boolean drawCenter = true;

    private boolean isPanTransitionInProgress;
    private int fromContentHeight;
    private int newContentHeight;
    private float panProgress;

    private ValueOverride valueOverride;

    public PaintWeightChooserView(Context context) {
        super(context);

        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            float startWeight;
            boolean startedY;
            float startDeltaY;

            @Override
            public boolean onDown(MotionEvent e) {
                boolean touch = touchRect.contains(e.getX(), e.getY());
                if (isTouchInProgress != touch) {
                    isTouchInProgress = touch;
                    invalidate();
                    if (touch) {
                        startWeight = valueOverride != null ? valueOverride.get() : colorSwatch.brushWeight;
                        startedY = false;
                    }
                }
                return isTouchInProgress;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isTouchInProgress) {
                    if (!startedY) {
                        startDeltaY = e1.getY() - e2.getY();
                        startedY = true;
                    }
                    float weight = MathUtils.clamp(startWeight + (e1.getY() - e2.getY() - startDeltaY) / touchRect.height() * (max - min), min, max);
                    if (valueOverride != null) {
                        valueOverride.set(weight);
                    } else {
                        colorSwatch.brushWeight = weight;
                    }
                    animatedWeight.set(weight, true);
                    onUpdate.run();
                    invalidate();
                }
                return isTouchInProgress;
            }
        });
        colorPaint.setColor(Color.WHITE);
        colorPaint.setShadowLayer(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(2), 0x50000000);
        backgroundPaint.setColor(0x40ffffff);
        backgroundPaint.setShadowLayer(AndroidUtilities.dp(3), 0, AndroidUtilities.dp(1), 0x26000000);
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
        invalidate();
    }

    public void setValueOverride(ValueOverride valueOverride) {
        this.valueOverride = valueOverride;
        invalidate();
    }

    public void startPanTransition(int fromContentHeight, int newContentHeight) {
        isPanTransitionInProgress = true;
        this.fromContentHeight = fromContentHeight;
        this.newContentHeight = newContentHeight;
        invalidate();
    }

    public void stopPanTransition() {
        isPanTransitionInProgress = false;
        invalidate();
    }

    public void updatePanTransition(float y, float progress) {
        if (!isPanTransitionInProgress) {
            return;
        }
        if (fromContentHeight < newContentHeight) {
            progress = 1f - progress;
        }

        this.panProgress = progress;

        setTranslationY(y);

        int contentHeight = AndroidUtilities.lerp(fromContentHeight, newContentHeight, panProgress);
        int height = (int) (contentHeight * 0.3f);
        touchRect.set(0, (contentHeight - height) / 2f, AndroidUtilities.dp(32), (contentHeight + height) / 2f);

        invalidate();
    }

    public void setRenderView(RenderView renderView) {
        this.renderView = renderView;
    }

    public void setColorSwatch(Swatch colorSwatch) {
        this.colorSwatch = colorSwatch;
        invalidate();
    }

    public void setDrawCenter(boolean draw) {
        drawCenter = draw;
        invalidate();
    }

    public void setMinMax(float min, float max) {
        this.min = min;
        this.max = max;
        invalidate();
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean detector = gestureDetector.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            isTouchInProgress = false;
            invalidate();
        }
        return detector;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (!isPanTransitionInProgress) {
            int height = (int) (getHeight() * 0.3f);
            touchRect.set(0, (getHeight() - height) / 2f, AndroidUtilities.dp(32), (getHeight() + height) / 2f);
        }
    }

    public void setViewHidden(boolean isViewHidden) {
        this.isViewHidden = isViewHidden;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long dt = Math.min(16, System.currentTimeMillis() - lastUpdate);
        lastUpdate = System.currentTimeMillis();

        float weight = animatedWeight.set(valueOverride != null ? valueOverride.get() : colorSwatch.brushWeight), min = animatedMin.set(this.min), max = animatedMax.set(this.max);

        if (isViewHidden && hideProgress != 1f) {
            hideProgress = Math.min(1f, hideProgress + dt / 200f);
            invalidate();
        } else if (!isViewHidden && hideProgress != 0f) {
            hideProgress = Math.max(0f, hideProgress - dt / 200f);
            invalidate();
        }

        if (isTouchInProgress && showProgress != 1f) {
            showProgress = Math.min(1f, showProgress + dt / 200f);
            invalidate();
        } else if (!isTouchInProgress && showProgress != 0f) {
            showProgress = Math.max(0f, showProgress - dt / 200f);
            invalidate();
        }

        float height = touchRect.height();
        int bigRadius = AndroidUtilities.dp(16);
        int smallRadius = AndroidUtilities.dp(3);
        int smallLine = AndroidUtilities.dp(3);

        path.rewind();
        path.moveTo(0, 0);
        AndroidUtilities.rectTmp.set(AndroidUtilities.lerp(-smallLine, -bigRadius, showProgress), 0, AndroidUtilities.lerp(smallLine, bigRadius, showProgress), bigRadius * 2);
        path.arcTo(AndroidUtilities.rectTmp, -90, 90);
        path.lineTo(AndroidUtilities.lerp(smallLine, smallRadius, showProgress), height);
        AndroidUtilities.rectTmp.set(AndroidUtilities.lerp(-smallLine, -smallRadius, showProgress), height - smallRadius * 2, AndroidUtilities.lerp(smallLine, smallRadius, showProgress), height);
        path.arcTo(AndroidUtilities.rectTmp, 0, 180);

        path.lineTo(AndroidUtilities.lerp(-smallLine, -bigRadius, showProgress), bigRadius);
        AndroidUtilities.rectTmp.set(AndroidUtilities.lerp(-smallLine, -bigRadius, showProgress), 0, AndroidUtilities.lerp(smallLine, bigRadius, showProgress), bigRadius * 2);
        path.arcTo(AndroidUtilities.rectTmp, -180, 90);

        path.close();

        if (hideProgress != 0f) {
            AndroidUtilities.rectTmp.set(0, 0, getWidth(), getHeight());
            canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (0xFF * (1f - hideProgress)), Canvas.ALL_SAVE_FLAG);
        }

        canvas.save();
        canvas.translate(AndroidUtilities.dp(32) * CubicBezierInterpolator.DEFAULT.getInterpolation(showProgress), touchRect.top);
        canvas.drawPath(path, backgroundPaint);
        canvas.restore();

        drawCircleWithShadow(
            canvas,
            AndroidUtilities.dp(32) * CubicBezierInterpolator.DEFAULT.getInterpolation(showProgress),
            MathUtils.clamp(
                touchRect.top + touchRect.height() * (1f - (weight - min) / (max - min)),
                touchRect.top + bigRadius,
                touchRect.bottom - Math.min(smallRadius * 1.5f, bigRadius)
            ),
            AndroidUtilities.lerp(AndroidUtilities.dp(12), AndroidUtilities.lerp(Math.min(smallRadius * 1.5f, bigRadius), bigRadius, (weight - min) / (max - min)), showProgress),
            false
        );

        if (drawCenter && showProgress != 0f && showPreview) {
            float cx = getWidth() / 2f, cy = getHeight() / 2f, rad = renderView.brushWeightForSize(weight) * renderView.getCurrentBrush().getScale() * renderView.getCurrentBrush().getPreviewScale();
            drawCircleWithShadow(canvas, cx, cy, rad, true);
        }

        if (hideProgress != 0f) {
            canvas.restore();
        }
    }

    private void drawCircleWithShadow(Canvas canvas, float cx, float cy, float rad, boolean useAlpha) {
        if (useAlpha) {
            AndroidUtilities.rectTmp.set(cx - rad - AndroidUtilities.dp(6), cy - rad - AndroidUtilities.dp(6), cx + rad + AndroidUtilities.dp(6), cy + rad + AndroidUtilities.dp(6));
            canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (0xFF * showProgress), Canvas.ALL_SAVE_FLAG);
        }
        canvas.drawCircle(cx, cy, rad, colorPaint);
        if (useAlpha) {
            canvas.restore();
        }
    }

    public interface ValueOverride {
        float get();
        void set(float val);
    }
}
