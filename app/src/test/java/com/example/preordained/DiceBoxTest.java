package com.example.preordained;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.preordained.dice.DiceBox;
import com.example.preordained.dice.DicePresets;
import com.example.preordained.dice.Die;
import com.example.preordained.dice.DiceRoll;
import com.example.preordained.dice.RollResult;

import org.junit.Test;

import java.util.List;

public class DiceBoxTest {

    @Test(expected = IllegalArgumentException.class)
    public void die_rejectsTooFewSides() {
        new Die(Die.MIN_SIDES - 1);
    }

    @Test
    public void rollValuesStayWithinRange() {
        DiceBox box = new DiceBox();
        box.add(6, 100);
        RollResult result = box.roll();
        for (DiceRoll roll : result.getRolls()) {
            assertTrue(roll.getValue() >= 1 && roll.getValue() <= roll.getSides());
        }
    }

    @Test
    public void totalEqualsSumOfIndividualRolls() {
        DiceBox box = new DiceBox();
        box.add(4, 2);
        box.add(20, 1);
        RollResult result = box.roll();
        int sum = 0;
        for (DiceRoll roll : result.getRolls()) {
            sum += roll.getValue();
        }
        assertEquals(sum, result.getTotal());
        assertEquals(3, result.getRolls().size());
    }

    @Test
    public void addingSameDieMergesEntries() {
        DiceBox box = new DiceBox();
        box.add(6, 2);
        box.add(6, 3);
        List<DiceBox.Entry> entries = box.getEntries();
        assertEquals(1, entries.size());
        assertEquals(5, entries.get(0).getCount());
        assertEquals(6, entries.get(0).getSides());
    }

    @Test
    public void removeAndClearAffectBox() {
        DiceBox box = new DiceBox();
        box.add(DicePresets.D20, 1);
        box.add(DicePresets.D6, 1);
        assertEquals(2, box.getEntries().size());
        box.remove(0);
        assertEquals(1, box.getEntries().size());
        box.clear();
        assertTrue(box.isEmpty());
    }
}