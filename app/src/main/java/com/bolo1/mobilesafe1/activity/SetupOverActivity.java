package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/7/26.
 */

public class SetupOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean set_over = Sputils.getBoolean(this, ConstantValue.SET_OVER, false);
        if (set_over) {
            //如果完成设置则跳转到设置显示
            setContentView(R.layout.setupover_activity);
        } else {
            //如果没有完成则跳转到导航界面1
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }

    }
}
