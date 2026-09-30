package com.example.preordained.dice;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Persists named dice-box presets to SharedPreferences as JSON. */
public final class PresetStore {

    private static final String PREFS_NAME = "preordained_dice_presets";
    private static final String KEY_PRESETS = "presets";

    private final SharedPreferences preferences;

    public PresetStore(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<DiceBoxPreset> load() {
        List<DiceBoxPreset> presets = new ArrayList<>();
        String raw = preferences.getString(KEY_PRESETS, null);
        if (raw == null) {
            return presets;
        }
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject preset = array.getJSONObject(i);
                JSONArray dice = preset.getJSONArray("dice");
                List<DiceBox.Entry> entries = new ArrayList<>();
                for (int j = 0; j < dice.length(); j++) {
                    JSONObject die = dice.getJSONObject(j);
                    entries.add(new DiceBox.Entry(die.getInt("sides"), die.getInt("count")));
                }
                presets.add(new DiceBoxPreset(preset.getString("name"), entries));
            }
        } catch (JSONException e) {
            return new ArrayList<>();
        }
        return presets;
    }

    public void save(List<DiceBoxPreset> presets) {
        JSONArray array = new JSONArray();
        for (DiceBoxPreset preset : presets) {
            JSONObject obj = new JSONObject();
            JSONArray dice = new JSONArray();
            for (DiceBox.Entry entry : preset.getEntries()) {
                JSONObject die = new JSONObject();
                try {
                    die.put("sides", entry.getSides());
                    die.put("count", entry.getCount());
                } catch (JSONException ignored) {
                    continue;
                }
                dice.put(die);
            }
            try {
                obj.put("name", preset.getName());
                obj.put("dice", dice);
            } catch (JSONException ignored) {
                continue;
            }
            array.put(obj);
        }
        preferences.edit().putString(KEY_PRESETS, array.toString()).apply();
    }
}