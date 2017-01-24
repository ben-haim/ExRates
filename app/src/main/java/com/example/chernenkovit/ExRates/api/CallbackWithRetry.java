package com.example.chernenkovit.ExRates.api;

import android.os.Handler;

import retrofit2.Call;
import retrofit2.Callback;

//unused
/** Callback with retrying logic. */
public abstract class CallbackWithRetry<T> implements Callback<T> {
    private final Call<T> call;
    private static final int RETRY_COUNT = 10;
    private static final long RETRY_DELAY = 600000;
    private int retryCount = 0;


    public CallbackWithRetry(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onFailure(final Call<T> call,Throwable t) {
        if (retryCount <= RETRY_COUNT) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    retry();
                }
            }, RETRY_DELAY);
            retry();
        }
    }

    private void retry() {
        call.clone().enqueue(this);
    }
}