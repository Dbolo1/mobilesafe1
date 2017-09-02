package com.bolo1.mobilesafe1.activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;


/**
 * Created by 菠萝 on 2017/8/11.
 */

public class QueryPhoneAddress extends AppCompatActivity {

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_query_result.setText(mAddress);
        }
    };
    private String mAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_phone_address_activity);
        initAddress();

    }

    private void initAddress() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if(!TextUtils.isEmpty(phone)){
                    query(phone);
                }else {
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    et_phone.setAnimation(shake);
                    Vibrator vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(2000);
                    vibrator.vibrate(new long[]{2000,5000,2000,5000},-1);
                }
            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                   String phone1=et_phone.getText().toString();
                  query(phone1);
            }
        });
    }
    private void query(final String phone) {
        new Thread() {
            public void run() {
                mAddress = Address.getAddress(phone);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
