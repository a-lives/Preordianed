package com.example.preordained.dice;

import java.util.Collections;
import java.util.List;

/** The outcome of rolling every die currently in the dice box. */
public final class RollResult {

    private final List<DiceRoll> rolls;
    private final int total;

    public RollResult(List<DiceRoll> rolls, int total) {
        this.rolls = Collections.unmodifiableList(rolls);
        this.total = total;
    }

    public List<DiceRoll> getRolls() {
        return rolls;
    }

    public int getTotal() {
        return total;
    }
}