package com.example.preordained.dice;

/** A single result produced by one die in a roll. */
public final class DiceRoll {

    private final int sides;
    private final int value;

    public DiceRoll(int sides, int value) {
        this.sides = sides;
        this.value = value;
    }

    public int getSides() {
        return sides;
    }

    public int getValue() {
        return value;
    }
}