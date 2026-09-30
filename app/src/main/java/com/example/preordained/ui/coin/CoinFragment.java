package com.example.preordained.ui.coin;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.preordained.R;
import com.example.preordained.coin.CoinResult;
import com.example.preordained.databinding.DialogCountBinding;
import com.example.preordained.databinding.FragmentCoinBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Flip a chosen number of coins and show the heads / tails result and sequence. */
public class CoinFragment extends Fragment {

    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 100;

    private FragmentCoinBinding binding;
    private CoinViewModel viewModel;
    private CoinResultRenderer resultRenderer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCoinBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CoinViewModel.class);
        resultRenderer = new CoinResultRenderer(requireContext());

        updateCountText();
        binding.btnDecrease.setOnClickListener(v -> changeCount(-1));
        binding.btnIncrease.setOnClickListener(v -> changeCount(1));
        binding.textQuantity.setOnClickListener(v -> showCountDialog());
        binding.btnFlip.setOnClickListener(v -> flip());

        viewModel.getResult().observe(getViewLifecycleOwner(), this::showResult);
    }

    private void changeCount(int delta) {
        int next = viewModel.getCoinCount() + delta;
        if (next < MIN_COUNT || next > MAX_COUNT) {
            return;
        }
        viewModel.setCoinCount(next);
        updateCountText();
    }

    private void updateCountText() {
        binding.textQuantity.setText(String.valueOf(viewModel.getCoinCount()));
    }

    private void showCountDialog() {
        DialogCountBinding dialogBinding = DialogCountBinding.inflate(getLayoutInflater());
        String current = String.valueOf(viewModel.getCoinCount());
        dialogBinding.inputCount.setText(current);
        dialogBinding.inputCount.setSelection(current.length());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.coin_pick_title)
                .setView(dialogBinding.getRoot())
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int value = parseCount(dialogBinding);
            if (value < MIN_COUNT || value > MAX_COUNT) {
                dialogBinding.layoutCount.setError(getString(R.string.coin_error_invalid_count));
                return;
            }
            viewModel.setCoinCount(value);
            updateCountText();
            dialog.dismiss();
        });
    }

    private static int parseCount(DialogCountBinding dialogBinding) {
        try {
            return Integer.parseInt(dialogBinding.inputCount.getText().toString().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void flip() {
        binding.btnFlip.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        viewModel.flip();
    }

    private void showResult(CoinResult result) {
        if (result == null) {
            return;
        }
        binding.textCoinHint.setVisibility(View.GONE);
        binding.blockResult.setVisibility(View.VISIBLE);
        binding.textHeadsCount.setText(String.valueOf(result.getHeads()));
        binding.textTailsCount.setText(String.valueOf(result.getTails()));

        if (result.size() == 1) {
            binding.resultCoinContainer.setVisibility(View.GONE);
            binding.singleCoinFrame.setVisibility(View.VISIBLE);
            resultRenderer.animateSingle(binding.singleCoin, result.isHeads(0));
        } else {
            binding.singleCoinFrame.setVisibility(View.GONE);
            binding.resultCoinContainer.setVisibility(View.VISIBLE);
            resultRenderer.renderSequence(binding.resultCoinContainer, result,
                    result.size() <= CoinResultRenderer.MAX_ANIMATED_COINS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}