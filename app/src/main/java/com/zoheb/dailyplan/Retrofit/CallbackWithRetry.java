package com.zoheb.dailyplan.Retrofit;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class CallbackWithRetry<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 2;
    private static final String TAG = CallbackWithRetry.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public CallbackWithRetry(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
//        Log.e(TAG, t.getLocalizedMessage());
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry();
        }else {
           onFailedAfterRetry(call,t);
        }
    }

    public abstract void onFailedAfterRetry(Call<T> call, Throwable t);

    private void retry() {
        call.clone().enqueue(this);
    }
}