package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ToastUtil;

/**
 * Created by 菠萝 on 2017/9/9.
 */

public class EnterPasActivity extends AppCompatActivity {


    private String name;
    private String packagename;
    private Drawable icon;
    private TextView tv_app_entername;
    private ImageView im_app_enetericon;
    private EditText et_app_pas;
    private Button bt_app_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packagename = getIntent().getStringExtra("packagename");
        setContentView(R.layout.enterpas_activity);
        initUI();
        initData();
    }

    private void initData() {

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            name = applicationInfo.loadLabel(pm).toString();
            icon = applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tv_app_entername.setText(name);
        im_app_enetericon.setBackgroundDrawable(icon);
        bt_app_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pas = et_app_pas.getText().toString();
                if (!TextUtils.isEmpty(pas)) {
                    if (pas.equals("123")) {

                        Intent intent = new Intent("android.intent.action.skip");
                        intent.putExtra("packagename", packagename);
                        sendBroadcast(intent);
                        finish();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                }
            }
        });

    }

    private void initUI() {
        tv_app_entername = (TextView) findViewById(R.id.tv_app_entername);
        im_app_enetericon = (ImageView) findViewById(R.id.im_app_enetericon);
        et_app_pas = (EditText) findViewById(R.id.et_app_pas);
        bt_app_submit = (Button) findViewById(R.id.bt_app_submit);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
