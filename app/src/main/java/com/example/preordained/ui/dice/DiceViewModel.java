package com.example.preordained.ui.dice;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.preordained.dice.DiceBox;
import com.example.preordained.dice.DiceBoxPreset;
import com.example.preordained.dice.PresetStore;
import com.example.preordained.dice.RollResult;

import java.util.ArrayList;
import java.util.List;

/** Shared, activity-scoped state for the dice feature: the working box and saved presets. */
public class DiceViewModel extends AndroidViewModel {

    private final DiceBox diceBox = new DiceBox();
    private final PresetStore presetStore;
    private final MutableLiveData<List<DiceBox.Entry>> entries = new MutableLiveData<>();
    private final MutableLiveData<RollResult> lastRoll = new MutableLiveData<>();
    private final MutableLiveData<List<DiceBoxPreset>> presets = new MutableLiveData<>();

    public DiceViewModel(@NonNull Application application) {
        super(application);
        presetStore = new PresetStore(application);
        entries.setValue(diceBox.getEntries());
        presets.setValue(presetStore.load());
    }

    public LiveData<List<DiceBox.Entry>> getEntries() {
        return entries;
    }

    public LiveData<RollResult> getLastRoll() {
        return lastRoll;
    }

    public LiveData<List<DiceBoxPreset>> getPresets() {
        return presets;
    }

    public boolean isBoxEmpty() {
        return diceBox.isEmpty();
    }

    public void addDice(int sides, int count) {
        diceBox.add(sides, count);
        refreshEntries();
    }

    public void removeDice(int index) {
        diceBox.remove(index);
        refreshEntries();
    }

    public void clearDice() {
        diceBox.clear();
        refreshEntries();
    }

    public void roll() {
        lastRoll.setValue(diceBox.roll());
    }

    public void saveCurrentAsPreset(String name) {
        List<DiceBoxPreset> updated = new ArrayList<>(currentPresets());
        updated.add(new DiceBoxPreset(name, diceBox.getEntries()));
        updatePresets(updated);
    }

    public void deletePreset(int index) {
        List<DiceBoxPreset> updated = new ArrayList<>(currentPresets());
        if (index < 0 || index >= updated.size()) {
            return;
        }
        updated.remove(index);
        updatePresets(updated);
    }

    public DiceBoxPreset getPreset(int index) {
        List<DiceBoxPreset> current = currentPresets();
        if (index < 0 || index >= current.size()) {
            return null;
        }
        return current.get(index);
    }

    private List<DiceBoxPreset> currentPresets() {
        List<DiceBoxPreset> value = presets.getValue();
        return value == null ? new ArrayList<>() : value;
    }

    private void updatePresets(List<DiceBoxPreset> list) {
        presets.setValue(list);
        presetStore.save(list);
    }

    private void refreshEntries() {
        entries.setValue(diceBox.getEntries());
    }
}