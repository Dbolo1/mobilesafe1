package com.bolo1.mobilesafe1.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;
import com.bolo1.mobilesafe1.view.SettingItem;

/**
 * Created by 菠萝 on 2017/7/26.
 */

public  class Setup2Activity  extends BaseSetupActivity{

    private SettingItem siv_sim_bound;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup2_activity);
        initUi();
    }

    @Override
    protected void showPrePage() {
        Intent intent=new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        String serialNumber=Sputils.getString(this,ConstantValue.SIM_NUMBER,"");
        if(!TextUtils.isEmpty(serialNumber)){
            Intent intent=new Intent(this,Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请绑定sim卡");
        }
    }


    private void initUi() {
        siv_sim_bound = (SettingItem) findViewById(R.id.siv_sim_bound);
        String  sim_number= Sputils.getString(this,ConstantValue.SIM_NUMBER,"");
        if(TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);
        }else{
            siv_sim_bound.setCheck(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isCheck=siv_sim_bound.isCheck();
                siv_sim_bound.setCheck(!isCheck);
                if(!isCheck){
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String  simSerialNumber=telephonyManager.getSimSerialNumber();
                    Sputils.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                }else{
                    Sputils.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }


}
