package com.bolo1.mobilesafe1.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.engine.ProcessInfoProvider;
import com.bolo1.mobilesafe1.receiver.MyAppWidgetProvider;

import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class UpdateWidgetService extends Service {

    private Timer mTimer;
    private InnerReceiver receiver;

    @Override
    public void onCreate() {
        startTimer();


        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentfilter.addAction(Intent.ACTION_SCREEN_ON);
        receiver = new InnerReceiver();

        registerReceiver(receiver,intentfilter);

        super.onCreate();
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAppWidget();
            }
        }, 0, 5000);

    }

    private void updateAppWidget() {
        //更新窗口显示
        AppWidgetManager Awm = AppWidgetManager.getInstance(this);
        RemoteViews remoteviews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteviews.setTextViewText(R.id.tv_process_count, "进程总数:" + ProcessInfoProvider.getProcessCount(this));
        String availSpace = android.text.format.Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteviews.setTextViewText(R.id.tv_process_memory, "剩余内存:" + availSpace);

        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteviews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        //延迟点击一键清理
        Intent intent2 = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = pendingIntent.getBroadcast(this, 1, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteviews.setOnClickPendingIntent(R.id.btn_clear, broadcast);

        ComponentName componentname = new ComponentName(this, MyAppWidgetProvider.class);
        Awm.updateAppWidget(componentname, remoteviews);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(intent.ACTION_SCREEN_ON)){
                    //开启定时器
                    startTimer();
                }else {
                    //关闭定时器
                    cancelTimerTask();
                }
        }
    }
    private void cancelTimerTask() {
            if(mTimer!=null){
                mTimer.cancel();
                mTimer=null;
            }
    }

    @Override
    public void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        cancelTimerTask();
        super.onDestroy();

    }
}
