package com.bolo1.mobilesafe1.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.engine.SmsBack;

import java.io.File;
import java.util.jar.Manifest;

public class AToolActivity extends AppCompatActivity {


    private ProgressBar pb_backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atool_activity);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 1);
        }
        initUI();
    }

    private void initUI() {
        pb_backup = (ProgressBar) findViewById(R.id.pb_backup);
        TextView tv_query_address = (TextView) findViewById(R.id.tv_query_address);
        tv_query_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QueryPhoneAddress.class));

            }
        });
        TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
            }
        });
        TextView ttv_common_number = (TextView) findViewById(R.id.ttv_common_number);
        ttv_common_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
        TextView tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockManager.class));
            }
        });
    }

    private void showProgress() {
        final ProgressDialog  pd = new ProgressDialog(this);
        pd.setTitle("短信备份");
//        pd.setIcon(R.mipmap.smsbackup);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "74sms.xml";
                SmsBack.backup(getApplicationContext(), path, new SmsBack.CallBack() {
                    @Override
                    public void setMax(int max) {
                        pb_backup.setMax(max);
                        pd.setMax(max);
                    }
                    @Override
                    public void setProgress(int index) {
                        pb_backup.setProgress(index);
                        pd.setProgress(index);
                    }
                });
                pd.dismiss();
            }
        }.start();
    }
}
