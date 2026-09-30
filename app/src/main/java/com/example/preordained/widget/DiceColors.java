package com.example.preordained.widget;

/** Assigns a stable color per die type so dice of the same sides always look the same. */
public final class DiceColors {

    private static final int D4 = 0xFFE53935;
    private static final int D6 = 0xFF1E88E5;
    private static final int D8 = 0xFF00897B;
    private static final int D10 = 0xFFFB8C00;
    private static final int D12 = 0xFF8E24AA;
    private static final int D20 = 0xFF43A047;
    private static final int D100 = 0xFF3949AB;

    private static final int[] FALLBACK = {
            0xFF5C6BC0, 0xFFEC407A, 0xFF26A69A, 0xFFFFA726, 0xFF7CB342,
            0xFF8D6E63, 0xFF29B6F6, 0xFFAB47BC, 0xFF66BB6A, 0xFFF06292
    };

    private DiceColors() {
    }

    public static int colorFor(int sides) {
        switch (sides) {
            case 4:
                return D4;
            case 6:
                return D6;
            case 8:
                return D8;
            case 10:
                return D10;
            case 12:
                return D12;
            case 20:
                return D20;
            case 100:
                return D100;
            default:
                return FALLBACK[Math.floorMod(sides, FALLBACK.length)];
        }
    }
}