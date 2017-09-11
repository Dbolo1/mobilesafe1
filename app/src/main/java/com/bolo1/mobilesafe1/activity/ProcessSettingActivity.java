package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.service.LockCleanService;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class ProcessSettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_setting_activity);
        initShowSystem();
        initLockClean();
    }

    /**
     * 锁屏清理进程
     */
    private void initLockClean() {
        final CheckBox cb_lock_clean = (CheckBox) findViewById(R.id.cb_lock_clean);
        boolean lock_clean = Sputils.getBoolean(getApplicationContext(), ConstantValue.LOCK_CLEAN, false);
        cb_lock_clean.setChecked(lock_clean);
        if (lock_clean){
            cb_lock_clean.setText("开启锁屏清理");
        }else{
            cb_lock_clean.setText("关闭锁屏清理");
        }

        cb_lock_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cb_lock_clean.setChecked(isChecked);
                if (isChecked) {
                    cb_lock_clean.setText("开启锁屏清理");
                    startService(new Intent(getApplicationContext(),LockCleanService.class));
                } else {
                    cb_lock_clean.setText("关闭锁屏清理");
                    stopService(new Intent(getApplicationContext(),LockCleanService.class));

                }
                Sputils.putBoolean(getApplicationContext(), ConstantValue.LOCK_CLEAN, isChecked);
            }
        });

    }

    /**
     * 设置是否显示系统进程
     */
    private void initShowSystem() {

        final CheckBox cb_show_SystemProcess = (CheckBox) findViewById(R.id.cb_show_SystemProcess);
        boolean show_system = Sputils.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
        cb_show_SystemProcess.setChecked(show_system);
        if (show_system){
            cb_show_SystemProcess.setText("显示系统进程");
        }else{
            cb_show_SystemProcess.setText("隐藏系统进程");
        }

        cb_show_SystemProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cb_show_SystemProcess.setChecked(isChecked);
                if (isChecked) {
                    cb_show_SystemProcess.setText("显示系统进程");
                } else {
                    cb_show_SystemProcess.setText("隐藏系统进程");
                }
                Sputils.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
            }
        });
    }


}
