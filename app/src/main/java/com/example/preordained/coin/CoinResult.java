package com.example.preordained.coin;

/** Outcome of flipping a batch of coins, in flip order. */
public final class CoinResult {

    private final boolean[] outcomes;
    private final int heads;

    public CoinResult(boolean[] outcomes) {
        this.outcomes = outcomes.clone();
        int count = 0;
        for (boolean outcome : this.outcomes) {
            if (outcome) {
                count++;
            }
        }
        this.heads = count;
    }

    /** Number of coins flipped. */
    public int size() {
        return outcomes.length;
    }

    /** Whether the coin at {@code index} landed heads. */
    public boolean isHeads(int index) {
        return outcomes[index];
    }

    public int getHeads() {
        return heads;
    }

    public int getTails() {
        return outcomes.length - heads;
    }
}