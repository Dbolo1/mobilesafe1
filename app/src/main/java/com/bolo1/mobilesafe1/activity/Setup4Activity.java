package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;

/**
 * Created by 菠萝 on 2017/7/26.
 */

 public    class Setup4Activity  extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup4_activity);
        initUI();
    }

    private void initUI() {
        final CheckBox  cb_cbx= (CheckBox) findViewById(R.id.cb_cbx);
       boolean open_security= Sputils.getBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,false);
        cb_cbx.setChecked(open_security);
        if(open_security){
            cb_cbx.setText("安全防护已开启");
        }else{
            cb_cbx.setText("安全防护已关闭");
        }
        cb_cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_cbx.setText("安全防护已开启");
                }else{
                    cb_cbx.setText("安全防护已关闭");
                }
                Sputils.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,isChecked);
            }
        });
    }
    public void nextPage(View view) {
            boolean open_security=Sputils.getBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,false);
        if(open_security){
            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);
            finish();
            Sputils.putBoolean(this, ConstantValue.SET_OVER,true);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(getApplicationContext(),"请开启防盗保护");
        }

    }

    public void perPage(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
