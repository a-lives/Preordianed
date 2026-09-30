package com.example.preordained.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * A single die drawn as a hollow, rounded-corner shape with its roll value inside.
 * The outline shape hints at the die type: d4 is a triangle, d6 a rounded square,
 * and other dice use a regular polygon with as many corners as fits their sides.
 */
public class DiceView extends View {

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path shapePath = new Path();

    private int sides;
    private int value;

    public DiceView(Context context) {
        this(context, null);
    }

    public DiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3f * density);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    public void setSides(int sides) {
        this.sides = sides;
        int color = DiceColors.colorFor(sides);
        strokePaint.setColor(color);
        textPaint.setColor(color);
        invalidate();
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sides <= 0) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        float half = Math.min(width, height) / 2f - strokePaint.getStrokeWidth() / 2f;
        if (half <= 0) {
            return;
        }
        float cx = width / 2f;
        float cy = height / 2f;

        int corners = cornerCountFor(sides);
        double step = 2 * Math.PI / corners;
        double start = Math.toRadians(startAngleFor(corners));
        float radius = half;

        shapePath.reset();
        for (int i = 0; i < corners; i++) {
            double angle = start + i * step;
            float x = cx + radius * (float) Math.cos(angle);
            float y = cy + radius * (float) Math.sin(angle);
            if (i == 0) {
                shapePath.moveTo(x, y);
            } else {
                shapePath.lineTo(x, y);
            }
        }
        shapePath.close();
        canvas.drawPath(shapePath, strokePaint);

        if (value <= 0) {
            return;
        }
        float apothem = radius * (float) Math.cos(Math.PI / corners);
        textPaint.setTextSize(Math.min(half * textScaleFor(value), apothem * 1.25f));
        float baseline = cy + half * BASELINE_FRACTION;
        canvas.drawText(String.valueOf(value), cx, baseline, textPaint);
    }

    private static final float BASELINE_FRACTION = 0.28f;

    private static float textScaleFor(int value) {
        if (value >= 100) {
            return 0.58f;
        }
        if (value >= 10) {
            return 0.75f;
        }
        return 0.9f;
    }

    private static int cornerCountFor(int sides) {
        switch (sides) {
            case 4:
                return 3;
            case 6:
                return 4;
            case 8:
                return 6;
            case 10:
                return 5;
            case 12:
                return 6;
            case 20:
                return 5;
            case 100:
                return 8;
            default:
                return 6;
        }
    }

    /** Orients each polygon with a flat, horizontal bottom edge so the value sits upright above it. */
    private static float startAngleFor(int corners) {
        if (corners % 2 == 1) {
            return -90f;
        }
        if (corners % 4 == 0) {
            return 180f / corners;
        }
        return 0f;
    }
}