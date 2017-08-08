package com.bolo1.mobilesafe1.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 菠萝 on 2017/8/7.
 */

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //监听手势
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势
                if (e1.getRawX()-e2.getRawX()<100 ){
                    //上一页手势
                    showPrePage();
                }
                if (e1.getRawX()-e2.getRawX()>100 ) {
                    //下一页手势
                    showNextPage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    protected abstract void showPrePage();

    protected abstract void showNextPage();

    public void nextPage(View view) {
        showNextPage();
    }

    public void perPage(View view) {
        showPrePage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
