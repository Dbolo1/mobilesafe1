package com.bolo1.mobilesafe1.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.activity.Address;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

public class AddressService extends Service {

    private TelephonyManager tm;
    private final String tag = "AddressService";
    private MyPhoneStateListener listener;
    private WindowManager wm;
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View view;
    private String mAddress;
    private TextView tv_toast_view;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast_view.setText(mAddress);
        }
    };
    private int mScreenWidth;
    private int mScreenHeight;
    private InnerOutCallReceiver mOutCallReceiver;
    private IntentFilter intentFilter;


    public void onCreate() {
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        mOutCallReceiver = new InnerOutCallReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();


        Log.d(tag,"进入如斯====");
        super.onCreate();
    }

    private class InnerOutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(tag,"进入拨打号码界面====");
                String phone=getResultData();
            showToastAddress(phone);
        }
    }
    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(tag, "挂断电话-------");
                    //空闲状态下的电话状态
                    if (wm != null && view != null) {
                        wm.removeView(view);
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃时的电话状态
                    Log.d(tag, "响铃状态-----");
                    //这里要做非空判断，否则获取不到incomingNumber
                    if (incomingNumber != null || incomingNumber.isEmpty()) {
                        Log.d(tag,"incomingNumber======"+incomingNumber);
                        showToastAddress(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    Log.d(tag, "摘机下状态---");
                    //摘机状态下的电话状态
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }


    private void showToastAddress(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.gravity = Gravity.LEFT+Gravity.TOP;
//        params.setTitle("Toast");
//        LayoutInflater inflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view=inflater.inflate(R.layout.show_toast,null);
        view = View.inflate(this, R.layout.show_toast, null);
        tv_toast_view = (TextView) view.findViewById(R.id.tv_toast_view);
       int[] ToastStyleIndex= new int[]{
                R.drawable.call_locate_gray,R.drawable.call_locate_white,
               R.drawable.call_locate_blue,R.drawable.call_locate_orange,
               R.drawable.call_locate_green};
        int toastcolorstyle=Sputils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE,0);
        tv_toast_view.setBackgroundResource(ToastStyleIndex[toastcolorstyle]);

        params.x=Sputils.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X, 0);
        params.y=Sputils.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,0);

        wm.addView(view, params);
        tv_toast_view.setOnTouchListener(new View.OnTouchListener() {
            public int starty;
            public int startx;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startx = (int) event.getRawX();
                        starty = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int disX=moveX- startx;
                        int disY=moveY-starty;
                        params.x=params.x+disX;
                        params.y=params.y+disY;
                        if(params.x<0){
                            params.x=0;
                        }
                        if(params.x>mScreenWidth-tv_toast_view.getWidth()){
                            params.x=mScreenWidth-tv_toast_view.getWidth();
                        }
                        if(params.y<0){
                            params.y=0;
                        }
                        if(params.y>mScreenHeight-tv_toast_view.getHeight()-22){
                            params.y=mScreenHeight-tv_toast_view.getHeight()-22;
                        }
                        wm.updateViewLayout(view,params);
                        startx= (int) event.getRawX();
                        starty= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X, params.x);
                        Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,params.y);
                        break;
                }
                return true;
            }
        });

        query1(incomingNumber);
    }

    private void query1(final String incomingNumber) {
        //此处查询应开启线程
        new Thread(){
            @Override
            public void run() {
                mAddress = Address.getAddress(incomingNumber);
                Log.d(tag, "电话属于----------------------" + mAddress);
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(tag,"boda haoma huishsudi ");
            registerReceiver(mOutCallReceiver,intentFilter);
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (tm != null && listener != null) {
            tm.listen(listener, PhoneStateListener.LISTEN_NONE);
            listener = null;
        }
        if(mOutCallReceiver!=null){
            Log.d(tag, "拨打号码接收者关闭 ");
            unregisterReceiver(mOutCallReceiver);
        }
        super.onDestroy();
    }
}
