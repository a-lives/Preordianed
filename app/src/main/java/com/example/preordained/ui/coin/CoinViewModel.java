package com.example.preordained.ui.coin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.preordained.coin.CoinResult;

import java.util.Random;

public class CoinViewModel extends ViewModel {

    private final MutableLiveData<CoinResult> result = new MutableLiveData<>();
    private final Random random = new Random();
    private int coinCount = 1;

    public LiveData<CoinResult> getResult() {
        return result;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public void flip() {
        boolean[] outcomes = new boolean[coinCount];
        for (int i = 0; i < coinCount; i++) {
            outcomes[i] = random.nextBoolean();
        }
        result.setValue(new CoinResult(outcomes));
    }
}