package com.bolo1.mobilesafe1.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bolo1.mobilesafe1.engine.ProcessInfoProvider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class LockCleanService extends Service {
    private String tag="LockCleanService";
    private InnerReceiver receiver;
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        //监听锁屏广播
        Log.d(tag,"开启广播监听锁屏");
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(tag,"5秒清理一次");
            }
        },0, 5000);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new InnerReceiver();
        registerReceiver(receiver, intentFilter);

    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.KillAll(context);
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        mTimer.cancel();
        mTimer=null;
    }
}
