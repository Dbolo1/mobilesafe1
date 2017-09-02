package com.bolo1.mobilesafe1.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.activity.SmokeActivity;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/8/18.
 */

public class RocketService extends Service {
    private int mScreenWidth;
    private int mScreenHeight;
    private View rocketView;
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager wm;
    private ImageView im_rocket;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            params.y = (int) msg.obj;
            wm.updateViewLayout(rocketView, params);
        }
    };
    private WindowManager.LayoutParams params;
    private String tag = "RocketService";


    @Override
    public void onCreate() {

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        Log.d(tag, "onCreate======");
        initRocketView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void initRocketView() {
        Log.d(tag, "init RocketView ======");
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        rocketView = View.inflate(getApplicationContext(), R.layout.rocket_send_activity, null);
        im_rocket = (ImageView) rocketView.findViewById(R.id.im_rocket);
        AnimationDrawable drawable = (AnimationDrawable) im_rocket.getBackground();

        drawable.start();
        wm.addView(rocketView, params);
        im_rocket.setOnTouchListener(new View.OnTouchListener() {
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
                        int disX = moveX - startx;
                        int disY = moveY - starty;
                        params.x = params.x + disX;
                        params.y = params.y + disY;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.x > mScreenWidth - im_rocket.getWidth()) {
                            params.x = mScreenWidth - im_rocket.getWidth();
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.y > mScreenHeight - im_rocket.getHeight() - 22) {
                            params.y = mScreenHeight - im_rocket.getHeight() - 22;
                        }
                        wm.updateViewLayout(rocketView, params);
                        startx = (int) event.getRawX();
                        starty = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.y > mScreenHeight * 2 / 3) {
                            params.x = mScreenWidth / 2 - im_rocket.getWidth() / 2;
                            wm.updateViewLayout(rocketView, params);
                            sendRocket();
                            Intent intent=new Intent(getApplicationContext(),SmokeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                        }
                        break;
                }
                return true;
            }
        });
    }

    private void sendRocket() {
        new Thread() {
            public void run() {
                Log.d(tag, "发射火箭");
                for (int i = 0; i < 12; i++) {
                    int height = mScreenHeight * 2 / 3 - (mScreenHeight * 1 / 18) * i;
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = height;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (wm != null && rocketView != null) {
            Log.d(tag, "移除");
            wm.removeView(rocketView);
        }
        super.onDestroy();
    }
}
