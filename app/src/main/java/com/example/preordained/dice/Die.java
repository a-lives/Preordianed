package com.example.preordained.dice;

import java.util.Random;

/** A single die with a fixed number of sides. Values range from 1 to sides. */
public final class Die {

    public static final int MIN_SIDES = 2;

    private final int sides;
    private final Random random;

    public Die(int sides) {
        if (sides < MIN_SIDES) {
            throw new IllegalArgumentException("sides must be at least " + MIN_SIDES);
        }
        this.sides = sides;
        this.random = new Random();
    }

    public int getSides() {
        return sides;
    }

    public int roll() {
        return random.nextInt(sides) + 1;
    }
}