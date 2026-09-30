package com.example.preordained.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A coin with a sun face on the front and a crescent-moon face on the back.
 * The {@code back} flag is set explicitly by the animator (rather than derived
 * from the 3D rotation), so the correct face always shows; the back face is
 * pre-mirrored so its artwork never appears flipped.
 */
public class CoinView extends View {

    private static final int FRONT_FILL = 0xFF00897B;
    private static final int FRONT_RING = 0xFF00695C;
    private static final int FRONT_MARK = 0xFFFFFFFF;
    private static final int BACK_FILL = 0xFF5E35B1;
    private static final int BACK_RING = 0xFF4527A0;
    private static final int BACK_MARK = 0xFFEDE7F6;

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;

    private boolean back;

    public CoinView(Context context) {
        this(context, null);
    }

    public CoinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        setCameraDistance(12000f * density);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(2f * density);
        markStroke.setStyle(Paint.Style.STROKE);
        markStroke.setStrokeCap(Paint.Cap.ROUND);
        markStroke.setStrokeJoin(Paint.Join.ROUND);
    }

    /** Instantly presents the heads (front) or tails (back) face without spinning. */
    public void setFace(boolean heads) {
        setBack(!heads);
        setRotationY(heads ? 0f : 180f);
    }

    /** Sets which physical side faces the viewer. */
    public void setBack(boolean back) {
        if (this.back != back) {
            this.back = back;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float cx = width / 2f;
        float cy = height / 2f;
        float radius = Math.min(width, height) / 2f - ringPaint.getStrokeWidth();
        if (radius <= 0) {
            return;
        }

        canvas.save();
        if (back) {
            canvas.scale(-1f, 1f, cx, cy);
        }

        fillPaint.setColor(back ? BACK_FILL : FRONT_FILL);
        canvas.drawCircle(cx, cy, radius, fillPaint);
        ringPaint.setColor(back ? BACK_RING : FRONT_RING);
        canvas.drawCircle(cx, cy, radius, ringPaint);

        if (back) {
            drawMoon(canvas, cx, cy, radius);
        } else {
            drawSun(canvas, cx, cy, radius);
        }
        canvas.restore();
    }

    private void drawSun(Canvas canvas, float cx, float cy, float radius) {
        markFill.setColor(FRONT_MARK);
        canvas.drawCircle(cx, cy, radius * 0.50f, markFill);

        markStroke.setColor(FRONT_MARK);
        markStroke.setStrokeWidth(radius * 0.10f);
        float inner = radius * 0.68f;
        float outer = radius * 0.84f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(45 * i - 90);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            canvas.drawLine(cx + inner * cos, cy + inner * sin,
                    cx + outer * cos, cy + outer * sin, markStroke);
        }
    }

    private void drawMoon(Canvas canvas, float cx, float cy, float radius) {
        float moon = radius * 0.72f;
        float cut = moon * 0.88f;
        float offset_x = moon * 0.16f;
        float offset_y = - moon * 0.03f;
        float mx = cx + radius * 0.10f;

        markFill.setColor(BACK_MARK);
        canvas.drawCircle(mx, cy, moon, markFill);

        markFill.setColor(BACK_FILL);
        canvas.drawCircle(mx + offset_x, cy + offset_y, cut, markFill);
    }
}