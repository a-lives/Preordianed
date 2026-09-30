package com.example.preordained.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/** Lays its children out in rows, wrapping to the next row when the current one is full. */
public class WrapLayout extends ViewGroup {

    public WrapLayout(Context context) {
        super(context);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int availableWidth = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingRight();

        int wrappedWidth = 0;
        int wrappedHeight = getPaddingTop() + getPaddingBottom();
        int rowWidth = 0;
        int rowHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int childWidth = child.getMeasuredWidth() + marginH(child);
            int childHeight = child.getMeasuredHeight() + marginV(child);

            if (rowWidth + childWidth > availableWidth && rowWidth > 0) {
                wrappedWidth = Math.max(wrappedWidth, rowWidth);
                wrappedHeight += rowHeight;
                rowWidth = childWidth;
                rowHeight = childHeight;
            } else {
                rowWidth += childWidth;
                rowHeight = Math.max(rowHeight, childHeight);
            }
        }
        wrappedWidth = Math.max(wrappedWidth, rowWidth);
        wrappedHeight += rowHeight;

        setMeasuredDimension(
                resolveSize(wrappedWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(wrappedHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int rowBottom = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth() + marginH(child);
            int childHeight = child.getMeasuredHeight() + marginV(child);

            if (x + childWidth > getPaddingLeft() + availableWidth && x > getPaddingLeft()) {
                x = getPaddingLeft();
                y += rowBottom;
                rowBottom = 0;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childLeft = x + lp.leftMargin;
            int childTop = y + lp.topMargin;
            child.layout(childLeft, childTop,
                    childLeft + child.getMeasuredWidth(),
                    childTop + child.getMeasuredHeight());

            x += childWidth;
            rowBottom = Math.max(rowBottom, childHeight);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    private int marginH(View child) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return lp.leftMargin + lp.rightMargin;
    }

    private int marginV(View child) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return lp.topMargin + lp.bottomMargin;
    }
}