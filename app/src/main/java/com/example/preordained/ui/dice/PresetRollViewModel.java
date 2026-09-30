package com.example.preordained.ui.dice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.preordained.dice.DiceBox;
import com.example.preordained.dice.RollResult;

import java.util.List;

/** Fragment-scoped state so a preset roll result survives configuration changes. */
public class PresetRollViewModel extends ViewModel {

    private final MutableLiveData<RollResult> result = new MutableLiveData<>();

    public LiveData<RollResult> getResult() {
        return result;
    }

    public void roll(List<DiceBox.Entry> entries) {
        DiceBox box = new DiceBox();
        for (DiceBox.Entry entry : entries) {
            box.add(entry.getSides(), entry.getCount());
        }
        result.setValue(box.roll());
    }
}