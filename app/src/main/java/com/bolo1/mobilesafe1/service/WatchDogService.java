package com.bolo1.mobilesafe1.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bolo1.mobilesafe1.activity.EnterPasActivity;
import com.bolo1.mobilesafe1.activity.TestActivity;
import com.bolo1.mobilesafe1.db.dao.AppLockDao;
import com.bolo1.mobilesafe1.db.domain.AppInfo;

import java.util.List;

/**
 * Created by 菠萝 on 2017/9/9.
 */

public class WatchDogService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private List<String> mPackageList;
    private InnerReceiver mInnerReceiver;
    private String skippackagename;
    private MyContentObserver myContentObserver;


    @Override
    public void onCreate() {
        mDao = AppLockDao.getInstance(getApplicationContext());
        isWatch = true;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.skip");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);
        myContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),true, myContentObserver);

        watch();
        super.onCreate();

    }
    private class MyContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            mPackageList = mDao.findAll();
            super.onChange(selfChange);
        }
    }

    public class  InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            skippackagename = intent.getStringExtra("packagename");
        }
    }

    private void watch() {
        //开启看门狗 可控无限循环匹配packageName；
        new Thread() {
            @Override
            public void run() {
                mPackageList = mDao.findAll();
                while (isWatch) {
                    //获取活动管理者
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTaskInfos=am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo=runningTaskInfos.get(0);
                    //获取栈顶的activity的包名
                    String packageName=runningTaskInfo.topActivity.getPackageName();
                    if(mPackageList.contains(packageName)){
                        //加锁应用
                        if (!packageName.equals(skippackagename)){
                            Intent intent=new Intent(getApplicationContext(),EnterPasActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename",packageName);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isWatch=false;
        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        if(myContentObserver!=null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }

        super.onDestroy();
    }
}
