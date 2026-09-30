package com.example.preordained.ui.coin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.preordained.coin.CoinResult;
import com.example.preordained.widget.CoinView;
import com.example.preordained.widget.WrapLayout;

/** Lays out coins for a flip and plays the spin animation when the batch is small. */
final class CoinResultRenderer {

    static final int MAX_ANIMATED_COINS = 10;

    private static final int SIZE_DP = 48;
    private static final int MARGIN_DP = 6;
    private static final long MULTI_DURATION_MS = 800;
    private static final long SINGLE_DURATION_MS = 1100;
    private static final long STAGGER_MS = 80;
    private static final int MULTI_TURNS = 2;
    private static final int SINGLE_TURNS = 4;

    private final float density;
    private final DecelerateInterpolator decelerate = new DecelerateInterpolator();
    private final OvershootInterpolator overshoot = new OvershootInterpolator(1.5f);

    CoinResultRenderer(Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
    }

    void renderSequence(WrapLayout container, CoinResult result, boolean animate) {
        container.removeAllViews();
        for (int i = 0; i < result.size(); i++) {
            container.addView(new CoinView(container.getContext()), params());
        }
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CoinView coin = (CoinView) container.getChildAt(i);
            if (animate) {
                flip(coin, result.isHeads(i), STAGGER_MS * i, MULTI_TURNS, MULTI_DURATION_MS);
            } else {
                coin.setFace(result.isHeads(i));
            }
        }
    }

    void animateSingle(CoinView coin, boolean heads) {
        flip(coin, heads, 0, SINGLE_TURNS, SINGLE_DURATION_MS);
    }

    private void flip(CoinView coin, boolean heads, long delay, int turns, long duration) {
        coin.setScaleX(0.35f);
        coin.setScaleY(0.35f);
        coin.setAlpha(0f);

        float start = coin.getRotationY();
        float currentMod = ((start % 360f) + 360f) % 360f;
        float targetMod = heads ? 0f : 180f;
        float delta = ((targetMod - currentMod) % 360f + 360f) % 360f;
        float end = start + turns * 360f + delta;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float t = animation.getAnimatedFraction();
            float easedRotation = decelerate.getInterpolation(t);
            float rotation = start + (end - start) * easedRotation;
            coin.setRotationY(rotation);

            float rotationMod = ((rotation % 360f) + 360f) % 360f;
            coin.setBack(rotationMod > 90f && rotationMod < 270f);

            float scale = 0.35f + 0.65f * overshoot.getInterpolation(t);
            coin.setScaleX(scale);
            coin.setScaleY(scale);
            coin.setAlpha(Math.min(1f, t * 4f));
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                coin.setRotationY(end);
                coin.setBack(!heads);
                coin.setScaleX(1f);
                coin.setScaleY(1f);
                coin.setAlpha(1f);
            }
        });
        animator.start();
    }

    private ViewGroup.MarginLayoutParams params() {
        int size = Math.round(SIZE_DP * density);
        int margin = Math.round(MARGIN_DP * density);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }
}