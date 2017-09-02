package com.bolo1.mobilesafe1.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bolo1.mobilesafe1.R;

/**
 * Created by 菠萝 on 2017/8/19.
 */

public class SmokeActivity extends AppCompatActivity {
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
                finish();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smoke_activity);
        ImageView  im_smoke_top= (ImageView) findViewById(R.id.im_smoke_top);
        ImageView im_smoke_bottom= (ImageView) findViewById(R.id.im_smoke_bottom);
         AlphaAnimation animation= new  AlphaAnimation(0,1);
        animation.setDuration(800);
        im_smoke_top.setAnimation(animation);
        im_smoke_bottom.setAnimation(animation);
        mHandler.sendEmptyMessageDelayed(0,1000);
    }


}
