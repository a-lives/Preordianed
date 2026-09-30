package com.example.preordained.ui.dice;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.preordained.R;
import com.example.preordained.dice.DiceBox;
import com.example.preordained.dice.DiceBoxPreset;
import com.example.preordained.dice.DicePresets;
import com.example.preordained.dice.Die;
import com.example.preordained.dice.RollResult;
import com.example.preordained.databinding.DialogPresetNameBinding;
import com.example.preordained.databinding.FragmentFreeBoxBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** The editable working box: pick/add dice, save it as a preset, roll and see results. */
public class FreeBoxFragment extends Fragment {

    private static final int MAX_COUNT = 100;
    private static final int CUSTOM_SIDES = -1;

    private final Map<Integer, Integer> chipSides = new HashMap<>();

    private FragmentFreeBoxBinding binding;
    private DiceViewModel viewModel;
    private DiceResultRenderer resultRenderer;
    private int quantity = 1;
    private int customChipId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFreeBoxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DiceViewModel.class);
        resultRenderer = new DiceResultRenderer(requireContext());

        setupTypeChips();
        setupQuantityStepper();
        setupButtons();

        viewModel.getEntries().observe(getViewLifecycleOwner(), this::renderBox);
        viewModel.getLastRoll().observe(getViewLifecycleOwner(), this::showRollResult);
    }

    private void setupTypeChips() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int defaultChipId = View.NO_ID;
        for (int sides : DicePresets.COMMON_SIDES) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_dice_type, binding.chipGroupDiceType, false);
            chip.setText(String.format(Locale.getDefault(), "d%d", sides));
            chip.setId(View.generateViewId());
            chipSides.put(chip.getId(), sides);
            if (sides == DicePresets.D6) {
                defaultChipId = chip.getId();
            }
            binding.chipGroupDiceType.addView(chip);
        }

        Chip customChip = (Chip) inflater.inflate(R.layout.chip_dice_type, binding.chipGroupDiceType, false);
        customChip.setText(R.string.dice_custom);
        customChip.setId(View.generateViewId());
        chipSides.put(customChip.getId(), CUSTOM_SIDES);
        customChipId = customChip.getId();
        binding.chipGroupDiceType.addView(customChip);

        binding.chipGroupDiceType.check(defaultChipId);
        binding.chipGroupDiceType.setOnCheckedStateChangeListener((group, checkedIds) ->
                binding.layoutCustomSides.setVisibility(
                        checkedIds.contains(customChipId) ? View.VISIBLE : View.GONE));
    }

    private void setupQuantityStepper() {
        binding.btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityText();
            }
        });
        binding.btnIncrease.setOnClickListener(v -> {
            if (quantity < MAX_COUNT) {
                quantity++;
                updateQuantityText();
            }
        });
    }

    private void updateQuantityText() {
        binding.textQuantity.setText(String.valueOf(quantity));
    }

    private void setupButtons() {
        binding.btnAddDice.setOnClickListener(v -> addSelectedDice());
        binding.btnClearBox.setOnClickListener(v -> viewModel.clearDice());
        binding.btnRoll.setOnClickListener(v -> rollDice());
        binding.btnSavePreset.setOnClickListener(v -> saveCurrentAsPreset());
    }

    private void addSelectedDice() {
        int selectedChipId = binding.chipGroupDiceType.getCheckedChipId();
        int sides;
        if (selectedChipId == customChipId) {
            Integer custom = parseIntIfValid(binding.inputCustomSides, value -> value >= Die.MIN_SIDES);
            if (custom == null) {
                binding.layoutCustomSides.setError(getString(R.string.dice_error_invalid_sides));
                return;
            }
            binding.layoutCustomSides.setError(null);
            sides = custom;
        } else {
            Integer preset = chipSides.get(selectedChipId);
            if (preset == null || preset == CUSTOM_SIDES) {
                return;
            }
            sides = preset;
        }
        viewModel.addDice(sides, quantity);
        quantity = 1;
        updateQuantityText();
    }

    private void rollDice() {
        if (viewModel.isBoxEmpty()) {
            showMessage(R.string.dice_error_box_empty);
            return;
        }
        binding.btnRoll.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        viewModel.roll();
    }

    private void renderBox(List<DiceBox.Entry> entries) {
        binding.chipGroupBox.removeAllViews();
        boolean empty = entries.isEmpty();
        binding.textBoxEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.chipGroupBox.setVisibility(empty ? View.GONE : View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < entries.size(); i++) {
            DiceBox.Entry entry = entries.get(i);
            Chip chip = (Chip) inflater.inflate(R.layout.chip_dice_box, binding.chipGroupBox, false);
            chip.setText(getString(R.string.dice_box_entry, entry.getSides(), entry.getCount()));
            chip.setCloseIconContentDescription(getString(R.string.dice_remove));
            final int index = i;
            chip.setOnCloseIconClickListener(v -> viewModel.removeDice(index));
            binding.chipGroupBox.addView(chip);
        }
    }

    private void saveCurrentAsPreset() {
        if (viewModel.isBoxEmpty()) {
            showMessage(R.string.dice_error_box_empty);
            return;
        }
        int nextNumber = presetCount() + 1;
        showNameDialog(getString(R.string.dice_new_preset_name, nextNumber),
                name -> {
                    viewModel.saveCurrentAsPreset(name);
                    showMessage(getString(R.string.dice_preset_saved, name));
                });
    }

    private void showNameDialog(String initialName, NameCallback callback) {
        DialogPresetNameBinding dialogBinding = DialogPresetNameBinding.inflate(getLayoutInflater());
        dialogBinding.inputPresetName.setText(initialName);
        dialogBinding.inputPresetName.setSelection(initialName.length());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dice_preset_save_title)
                .setView(dialogBinding.getRoot())
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = dialogBinding.inputPresetName.getText() == null
                    ? "" : dialogBinding.inputPresetName.getText().toString().trim();
            if (name.isEmpty()) {
                dialogBinding.layoutPresetName.setError(getString(R.string.dice_preset_name_required));
                return;
            }
            callback.onName(name);
            dialog.dismiss();
        });
    }

    private int presetCount() {
        List<DiceBoxPreset> presets = viewModel.getPresets().getValue();
        return presets == null ? 0 : presets.size();
    }

    private void showRollResult(RollResult result) {
        if (result == null) {
            return;
        }
        resultRenderer.renderAndAnimate(binding.resultDiceContainer, result);
        binding.textRollHint.setVisibility(View.GONE);
        binding.scrollRollResult.setVisibility(View.VISIBLE);

        String text = getString(R.string.dice_result_total) + ": " + result.getTotal();
        SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textRollTotal.setText(span);
        binding.textRollTotal.setVisibility(View.VISIBLE);
    }

    private static Integer parseIntIfValid(EditText input, Validator validator) {
        int value;
        try {
            value = Integer.parseInt(input.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
        return validator.isValid(value) ? value : null;
    }

    private interface Validator {
        boolean isValid(int value);
    }

    private interface NameCallback {
        void onName(String name);
    }

    private void showMessage(@StringRes int messageRes) {
        Snackbar.make(binding.getRoot(), messageRes, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.btnRoll)
                .show();
    }

    private void showMessage(CharSequence message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.btnRoll)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}