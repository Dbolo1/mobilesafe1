package com.bolo1.mobilesafe1.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/7/26.
 */

public class SetupOverActivity extends AppCompatActivity {
    private TextView tv_phone_number;


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
        initUi();
        initData();
    }

    private void initData() {
        //获取绑定的安全手机号码
        String phone=Sputils.getString(this,ConstantValue.CONTACT_PHONE,"");
        tv_phone_number.setText(phone);


    }

    private void initUi() {
        TextView tv_return_nav= (TextView) findViewById(R.id.tv_return_nav);
        tv_return_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetupOverActivity.this,Setup1Activity.class));
                finish();
            }
        });
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
    }
}
