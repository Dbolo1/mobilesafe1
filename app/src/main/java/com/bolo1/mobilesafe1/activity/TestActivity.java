package com.bolo1.mobilesafe1.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.dao.BlackNumberDao;
import com.bolo1.mobilesafe1.service.AddressService;
import com.bolo1.mobilesafe1.service.LocationService;
import com.bolo1.mobilesafe1.service.RocketService;

/**
 * Created by 菠萝 on 2017/7/25.
 */

public  class TestActivity extends AppCompatActivity {
    private final String tag="TestActivity";
    private TextView tv_test_text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity);
        Button bt_quedin= (Button) findViewById(R.id.bt_quedin);
        Button bt_quxiao= (Button) findViewById(R.id.bt_quxiao);
        bt_quedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().startService(new Intent(getApplicationContext(),RocketService.class));
                finish();
    }
});
      bt_quxiao.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              stopService(new Intent(getApplicationContext(), RocketService.class));
          }
      });
       BlackNumberDao dao= BlackNumberDao.getInstance(getApplicationContext());

    }
}
