package com.example.preordained.dice;

import java.util.ArrayList;
import java.util.List;

/** A box that holds dice to be rolled together. */
public final class DiceBox {

    /** An immutable view of one dice entry in the box. */
    public static final class Entry {
        private final int sides;
        private final int count;

        Entry(int sides, int count) {
            this.sides = sides;
            this.count = count;
        }

        public int getSides() {
            return sides;
        }

        public int getCount() {
            return count;
        }
    }

    private static final class StoredDie {
        private final Die die;
        private int count;

        StoredDie(Die die, int count) {
            this.die = die;
            this.count = count;
        }
    }

    private final List<StoredDie> stored = new ArrayList<>();

    public void add(int sides, int count) {
        if (sides < Die.MIN_SIDES) {
            throw new IllegalArgumentException("sides must be at least " + Die.MIN_SIDES);
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        for (StoredDie storedDie : stored) {
            if (storedDie.die.getSides() == sides) {
                storedDie.count += count;
                return;
            }
        }
        stored.add(new StoredDie(new Die(sides), count));
    }

    public void remove(int index) {
        stored.remove(index);
    }

    public void clear() {
        stored.clear();
    }

    public boolean isEmpty() {
        return stored.isEmpty();
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>(stored.size());
        for (StoredDie storedDie : stored) {
            entries.add(new Entry(storedDie.die.getSides(), storedDie.count));
        }
        return entries;
    }

    public RollResult roll() {
        List<DiceRoll> rolls = new ArrayList<>();
        int total = 0;
        for (StoredDie storedDie : stored) {
            for (int i = 0; i < storedDie.count; i++) {
                int value = storedDie.die.roll();
                rolls.add(new DiceRoll(storedDie.die.getSides(), value));
                total += value;
            }
        }
        return new RollResult(rolls, total);
    }
}