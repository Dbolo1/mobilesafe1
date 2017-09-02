package com.bolo1.mobilesafe1.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/8/18.
 */

public class ToastLocation extends AppCompatActivity {

    private ImageView im_toast_location;
    private Button bt_top;
    private Button bt_Bottom;
    private int mScreenWidth;
    private int mScreenHeigh;
    private long[] mHits= new long[2];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toastloaction_activity);
        initView();
        initToastLocation();
    }
    private void initView(){
        im_toast_location = (ImageView) findViewById(R.id.im_toast_location);
        bt_top = (Button) findViewById(R.id.bt_Top);
        bt_Bottom = (Button) findViewById(R.id.bt_Bottom);

    }
    private void initToastLocation() {
        int LocationX=Sputils.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X,0);
        int LocationY=Sputils.getInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin=LocationX;
        params.topMargin=LocationY;
        im_toast_location.setLayoutParams(params);
        WindowManager wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeigh = wm.getDefaultDisplay().getHeight();
        if(LocationY>mScreenHeigh/2){
            bt_Bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else {
            bt_Bottom.setVisibility(View.VISIBLE);
            bt_top.setVisibility(View.INVISIBLE);
        }
        im_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]=  SystemClock.currentThreadTimeMillis();
                if(mHits[mHits.length-1]-mHits[0]<500){
                   int left  =mScreenWidth/2-im_toast_location.getWidth()/2;
                    int top  =mScreenHeigh/2-im_toast_location.getHeight()/2;
                    int right  =mScreenWidth/2+im_toast_location.getWidth()/2;
                    int bottom  =mScreenHeigh/2+im_toast_location.getHeight()/2;
                    im_toast_location.layout(left, top,right,bottom);
                    Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X, left);
                    Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,top);
                }
            }
        });
        im_toast_location.setOnTouchListener(new View.OnTouchListener() {
            private int top;
            private int left;
            private int starty;
            private int startx;

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
                        left = im_toast_location.getLeft()+disX;
                        top = im_toast_location.getTop()+disY;
                        int right = im_toast_location.getRight()+disX;
                        int bottom = im_toast_location.getBottom()+disY;

                        if(left<0|| right>mScreenWidth||
                                top<0||bottom>mScreenHeigh-22){
                            return true;
                        }
                        im_toast_location.layout(left, top,right,bottom);
                        startx= (int) event.getRawX();
                        starty= (int) event.getRawY();
                        if(top>mScreenHeigh/2){
                            bt_Bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else {
                            bt_Bottom.setVisibility(View.VISIBLE);
                            bt_top.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_X, left);
                        Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_Y,top);
                        break;
                }
                return false;
            }
        });
    }


}
