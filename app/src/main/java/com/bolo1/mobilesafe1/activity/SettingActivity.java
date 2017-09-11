package com.bolo1.mobilesafe1.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.service.AddressService;
import com.bolo1.mobilesafe1.service.BlackNumberService;
import com.bolo1.mobilesafe1.service.WatchDogService;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.ServiceUtils;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.view.SettingClickItem;
import com.bolo1.mobilesafe1.view.SettingItem;

/**
 * Created by 菠萝 on 2017/7/15.
 */

public class SettingActivity extends AppCompatActivity {

    private Boolean open_update;
    private SettingItem set_address;
    private boolean isRunning;
    private SettingClickItem sci_toast_style;
    private String[] mToastStyle;
    private final int toast_style = Sputils.getInt(this, ConstantValue.TOAST_STYLE, 0);
    private SettingItem set_blacknumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        }
        initUpData();
        initAddress();
        initToastStyle();
        initToastLocation();
        initBlackNumber();
        initAppLock();
    }

    private void initAppLock() {
        final SettingItem set_app_lock = (SettingItem) findViewById(R.id.set_app_lock);
        boolean running = ServiceUtils.isRunning(this, "com.bolo1.mobilesafe1.service.WatchDogService");
        set_app_lock.setCheck(running);
        set_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck= set_app_lock.isCheck();
                set_app_lock.setCheck(!isCheck);
                if(!isCheck){
                    startService(new Intent(getApplicationContext(),WatchDogService.class));
                }else {
                    stopService(new Intent(getApplicationContext(),WatchDogService.class));
                }
            }
        });

    }

    private void initBlackNumber() {
        final SettingItem set_blacknumber = (SettingItem) findViewById(R.id.set_blacknumber);
        boolean running = ServiceUtils.isRunning(this, "com.bolo1.mobilesafe1.service.BlacknumberService");
        set_blacknumber.setCheck(running);
        set_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck= set_blacknumber.isCheck();
                set_blacknumber.setCheck(!isCheck);
                if(!isCheck){
                    startService(new Intent(getApplicationContext(),BlackNumberService.class));
                }else {
                   stopService(new Intent(getApplicationContext(),BlackNumberService.class));
                }
            }
        });
    }

    private void initToastLocation() {
        SettingClickItem sci_toast_location = (SettingClickItem) findViewById(R.id.sci_toast_location);
        sci_toast_location.setTitle("归属地拦的位置设置");
        sci_toast_location.setDes("设置归属地提示框的位置");
        sci_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocation.class));
            }
        });
    }

    private void initToastStyle() {
        sci_toast_style = (SettingClickItem) findViewById(R.id.sci_toast_style);
        sci_toast_style.setTitle("归属地址窗口风格");
        mToastStyle = new String[]{"灰色", "黑色", "蓝色", "橙色", "绿色"};
        sci_toast_style.setDes(mToastStyle[toast_style]);
        sci_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });

    }

    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("请选择归属地样式");
        builder.setSingleChoiceItems(mToastStyle, toast_style, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int which) {

                Sputils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                sci_toast_style.setDes(mToastStyle[which]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void initAddress() {
        set_address = (SettingItem) findViewById(R.id.set_address);
        isRunning = ServiceUtils.isRunning(this, "com.bolo1.mobilesafe1.service.AddressService");
        set_address.setCheck(isRunning);
        set_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = set_address.isCheck();
                set_address.setCheck(!isCheck);
                if (!isCheck) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    private void initUpData() {
        final SettingItem set_update = (SettingItem) findViewById(R.id.set_update);
        open_update = Sputils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        set_update.setCheck(open_update);
        set_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = set_update.isCheck();
                set_update.setCheck(!isCheck);
                Sputils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });

    }
}
