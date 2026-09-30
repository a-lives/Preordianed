package com.example.preordained.ui.dice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.preordained.R;
import com.example.preordained.dice.DiceBox;
import com.example.preordained.dice.DiceBoxPreset;
import com.example.preordained.databinding.FragmentDiceBoxListBinding;
import com.example.preordained.databinding.ItemBoxRowBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/** Start screen: shows the free box on top and the saved preset boxes below. */
public class DiceBoxListFragment extends Fragment {

    private FragmentDiceBoxListBinding binding;
    private DiceViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiceBoxListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DiceViewModel.class);

        binding.cardFreeBox.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.nav_free_box));

        viewModel.getEntries().observe(getViewLifecycleOwner(), entries ->
                binding.textFreeBoxContents.setText(DiceFormat.summarize(requireContext(), entries)));
        viewModel.getPresets().observe(getViewLifecycleOwner(), this::renderPresets);
    }

    private void renderPresets(List<DiceBoxPreset> presets) {
        binding.listPresets.removeAllViews();
        boolean empty = presets.isEmpty();
        binding.textPresetsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < presets.size(); i++) {
            DiceBoxPreset preset = presets.get(i);
            ItemBoxRowBinding row = ItemBoxRowBinding.inflate(inflater, binding.listPresets, false);
            row.textBoxName.setText(preset.getName());
            row.textBoxContents.setText(DiceFormat.summarize(requireContext(), preset.getEntries()));

            final int index = i;
            row.getRoot().setOnClickListener(v -> openPreset(index));
            row.getRoot().setOnLongClickListener(v -> {
                confirmDelete(index, preset.getName());
                return true;
            });
            binding.listPresets.addView(row.getRoot());
        }
    }

    private void openPreset(int index) {
        Bundle args = new Bundle();
        args.putInt(PresetRollFragment.ARG_PRESET_INDEX, index);
        NavHostFragment.findNavController(this).navigate(R.id.nav_preset_box, args);
    }

    private void confirmDelete(int index, String name) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dice_preset_delete_title)
                .setMessage(getString(R.string.dice_preset_delete_message, name))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.dice_preset_delete, (dialog, which) -> viewModel.deletePreset(index))
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}