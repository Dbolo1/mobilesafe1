package com.bolo1.mobilesafe1.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.view.SettingItem;

/**
 * Created by 菠萝 on 2017/7/15.
 */

public class SettingActivity extends AppCompatActivity {

    private Boolean open_update;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        initUpData();
    }
    private void initUpData() {
       final SettingItem  set_update= (SettingItem) findViewById(R.id.set_update);
        open_update = Sputils.getBoolean(this, ConstantValue.OPEN_UPDATE,false);
        set_update.setCheck(open_update);
       set_update.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               boolean isCheck = set_update.isCheck();
               set_update.setCheck(!isCheck);
               Sputils.putBoolean(getApplicationContext(),ConstantValue.OPEN_UPDATE,!isCheck);
           }
       });
    }
}
