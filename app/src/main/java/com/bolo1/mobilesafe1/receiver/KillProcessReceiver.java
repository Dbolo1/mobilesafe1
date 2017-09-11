package com.bolo1.mobilesafe1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bolo1.mobilesafe1.engine.ProcessInfoProvider;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.KillAll(context);
    }
}
