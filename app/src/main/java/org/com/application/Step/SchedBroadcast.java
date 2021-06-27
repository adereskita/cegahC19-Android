package com.anaminase.shapeyou.Step;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class SchedBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove(StepActivity.EXTRA_STEP).apply();
    }
}
