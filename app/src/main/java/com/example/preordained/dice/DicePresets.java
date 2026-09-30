package com.example.preordained.dice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Factory methods for useful sets of dice. */
public final class DicePresets {

    public static final int D4 = 4;
    public static final int D6 = 6;
    public static final int D8 = 8;
    public static final int D10 = 10;
    public static final int D12 = 12;
    public static final int D20 = 20;
    public static final int D100 = 100;

    /** Common polyhedral dice in ascending order of sides. */
    public static final List<Integer> COMMON_SIDES = Collections.unmodifiableList(
            Arrays.asList(D4, D6, D8, D10, D12, D20, D100));

    public static final int CUSTOM_INDEX = COMMON_SIDES.size();

    private DicePresets() {
    }
}