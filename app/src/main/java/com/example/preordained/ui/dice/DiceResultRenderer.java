package com.example.preordained.ui.dice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.preordained.dice.DiceRoll;
import com.example.preordained.dice.RollResult;
import com.example.preordained.widget.DiceView;
import com.example.preordained.widget.WrapLayout;

import java.util.List;
import java.util.Random;

/** Fills a wrapping container with dice for a roll and plays the tumble animation. */
final class DiceResultRenderer {

    private static final int DICE_SIZE_DP = 56;
    private static final int DICE_MARGIN_DP = 6;
    private static final long DURATION_MS = 450;
    private static final long STAGGER_MS = 60;

    private final float density;
    private final Random random = new Random();

    DiceResultRenderer(Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
    }

    void renderAndAnimate(WrapLayout container, RollResult result) {
        container.removeAllViews();
        List<DiceRoll> rolls = result.getRolls();
        for (DiceRoll roll : rolls) {
            DiceView dice = new DiceView(container.getContext());
            dice.setSides(roll.getSides());
            container.addView(dice, params());
        }
        animate(container, rolls);
    }

    private void animate(WrapLayout container, List<DiceRoll> rolls) {
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DiceView dice = (DiceView) container.getChildAt(i);
            DiceRoll roll = rolls.get(i);
            long delay = STAGGER_MS * i;

            dice.setValue(0);
            dice.setScaleX(0.3f);
            dice.setScaleY(0.3f);
            dice.setRotation(-360f);
            dice.setAlpha(0f);
            dice.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(0f)
                    .alpha(1f)
                    .setDuration(DURATION_MS)
                    .setStartDelay(delay)
                    .setInterpolator(new OvershootInterpolator(2f))
                    .start();

            ValueAnimator tumble = ValueAnimator.ofFloat(0f, 1f);
            tumble.setDuration(DURATION_MS);
            tumble.setStartDelay(delay);
            tumble.setInterpolator(new AccelerateDecelerateInterpolator());
            tumble.addUpdateListener(animation -> {
                if (animation.getAnimatedFraction() < 1f) {
                    dice.setValue(random.nextInt(roll.getSides()) + 1);
                } else {
                    dice.setValue(roll.getValue());
                }
            });
            tumble.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dice.setValue(roll.getValue());
                }
            });
            tumble.start();
        }
    }

    private ViewGroup.MarginLayoutParams params() {
        int size = Math.round(DICE_SIZE_DP * density);
        int margin = Math.round(DICE_MARGIN_DP * density);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }
}