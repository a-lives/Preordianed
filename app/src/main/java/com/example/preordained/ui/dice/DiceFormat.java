package com.example.preordained.ui.dice;

import android.content.Context;

import com.example.preordained.R;
import com.example.preordained.dice.DiceBox;

import java.util.List;

/** Formats a list of dice entries into a readable summary such as "d6 × 2、d20 × 1". */
final class DiceFormat {

    private DiceFormat() {
    }

    static String summarize(Context context, List<DiceBox.Entry> entries) {
        if (entries.isEmpty()) {
            return context.getString(R.string.dice_box_contents_empty);
        }
        StringBuilder builder = new StringBuilder();
        for (DiceBox.Entry entry : entries) {
            if (builder.length() > 0) {
                builder.append(context.getString(R.string.dice_preset_separator));
            }
            builder.append(context.getString(R.string.dice_box_entry, entry.getSides(), entry.getCount()));
        }
        return builder.toString();
    }
}