package com.bolo1.mobilesafe1.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.bolo1.mobilesafe1.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 菠萝 on 2017/8/29.
 */

public class BlackNumberService extends Service {


    private InnerSmsReceiver innerSmsReceiver;
    private TelephonyManager tm;
    private BlackNumberDao dao;
    private String tag = "BlackNumberService";
    private MyPhoneStateListener myPhoneStateListener;
    private MyObserver observer;

    @Override
    public void onCreate() {
        dao = BlackNumberDao.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        innerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(innerSmsReceiver, filter);
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃时的电话状态
                    Log.d(tag, "来电人--------" + incomingNumber);
                    if (incomingNumber != null || incomingNumber.isEmpty()) {
                        endCall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }

    private void endCall(String phone) {
        int mode = dao.getMode(phone);
        Log.d(tag, "拦截短信手机联系人的mode" + mode + "以及联系人:phone--" + phone);
        if (mode == 2 || mode == 3) {
            try {
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                Method method = clazz.getMethod("getService", String.class);
                IBinder ibinder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
                ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            observer = new MyObserver(new Handler(), phone);
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, observer);
        }
    }

    private class MyObserver extends ContentObserver {
        private String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            delCallLog(phone);
            super.onChange(selfChange);

        }
    }

    private void delCallLog(String number) {
        ContentResolver resolver = this.getContentResolver();
        //通话记录的uri，可以通过CallLog.Calls.CONTENT_URI得到，"number"记录通话记录的号码
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = resolver.
                query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=?", new String[]{number}, null);
        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id});
        }

    }


    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object obs : objects) {
                SmsMessage smg = SmsMessage.createFromPdu((byte[]) obs);
                String address = smg.getOriginatingAddress();
                String body = smg.getMessageBody();

                int mode = dao.getMode(address);
                Log.d(tag, "拦截短信手机联系人的mode" + mode + "以及联系人--" + address);
                if (mode == 1 || mode == 3) {
                    Log.d(tag, "onReceive:== 拦截了短信，--------------------------------");
                    this.abortBroadcast();
                }
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (innerSmsReceiver != null) {
            unregisterReceiver(innerSmsReceiver);
        }
        if (myPhoneStateListener != null) {
            tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }
        super.onDestroy();
    }
}
