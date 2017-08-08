package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bolo1.mobilesafe1.R;

/**
 * Created by 菠萝 on 2017/7/26.
 */

public  class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup1over_activity);
    }

    @Override
    protected void showPrePage() {
        //不做处理
    }

    @Override
    protected void showNextPage() {
        Intent intent=new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
    }
}
