package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;

/**
 * Created by 菠萝 on 2017/7/26.
 */

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_sim_number;
    private Button bt_select_number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup3_activity);
        initUI();


    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        String phone=  et_sim_number.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);
            finish();
            Sputils.putString(getApplicationContext(),ConstantValue.CONTACT_PHONE,phone);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请输入电话号码");
        }
    }

    private void initUI() {
        et_sim_number = (EditText) findViewById(R.id.et_sim_number);
        String phone=Sputils.getString(getApplicationContext(),ConstantValue.CONTACT_PHONE,"");
        et_sim_number.setText(phone);
        bt_select_number = (Button) findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_sim_number.setText(phone);
            Sputils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE,phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

