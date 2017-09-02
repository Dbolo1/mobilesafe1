package com.bolo1.mobilesafe1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;


/**
 * Created by 菠萝 on 2017/7/15.
 */

public class SettingClickItem extends RelativeLayout {

    private static final String TAG = "SettingItem";
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.bolo1.mobilesafe1";
    private TextView tv_top_title1;
    private TextView tv_below_des1;


    public SettingClickItem(Context context) {
        this(context, null);
    }

    public SettingClickItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.setting_click_item, this);
        tv_top_title1 = (TextView) findViewById(R.id.tv_top_title1);
        tv_below_des1 = (TextView) findViewById(R.id.tv_below_des1);
    }
    public void  setTitle(String title){
        tv_top_title1.setText(title);
    }
    public void  setDes(String des){
        tv_below_des1.setText(des);
    }
}
