package com.example.preordained.dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A named, saved set of dice that can be loaded into the working box. */
public final class DiceBoxPreset {

    private final String name;
    private final List<DiceBox.Entry> entries;

    public DiceBoxPreset(String name, List<DiceBox.Entry> entries) {
        this.name = name;
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public String getName() {
        return name;
    }

    public List<DiceBox.Entry> getEntries() {
        return entries;
    }
}