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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.preordained.R;
import com.example.preordained.dice.DiceBoxPreset;
import com.example.preordained.dice.RollResult;
import com.example.preordained.databinding.FragmentPresetRollBinding;

/** Roll-only view for a saved preset box: shows its dice, a roll button and the results. */
public class PresetRollFragment extends Fragment {

    public static final String ARG_PRESET_INDEX = "presetIndex";

    private FragmentPresetRollBinding binding;
    private PresetRollViewModel rollViewModel;
    private DiceResultRenderer resultRenderer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPresetRollBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiceViewModel diceViewModel = new ViewModelProvider(requireActivity()).get(DiceViewModel.class);
        rollViewModel = new ViewModelProvider(this).get(PresetRollViewModel.class);
        resultRenderer = new DiceResultRenderer(requireContext());

        int index = getArguments() == null
                ? -1 : getArguments().getInt(ARG_PRESET_INDEX, -1);
        DiceBoxPreset preset = diceViewModel.getPreset(index);
        if (preset == null) {
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        requireActivity().setTitle(preset.getName());
        binding.textPresetName.setText(preset.getName());
        binding.textPresetContents.setText(DiceFormat.summarize(requireContext(), preset.getEntries()));
        binding.btnRoll.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            rollViewModel.roll(preset.getEntries());
        });
        rollViewModel.getResult().observe(getViewLifecycleOwner(), this::showRollResult);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}