package com.bolo1.mobilesafe1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bolo1.mobilesafe1.R;



/**
 * Created by 菠萝 on 2017/7/15.
 */

public class SettingItem extends RelativeLayout {

    private static final String TAG ="SettingItem";
    private TextView tv_top_title;
    private TextView tv_below_des;
    private CheckBox cb_set_box;
    private static final String NAMESPACE="http://schemas.android.com/apk/res/com.bolo1.mobilesafe1";
    private String mDestitle;
    private String mDeson;
    private String mDesoff;

    public SettingItem(Context context) {
        this(context,null);
    }

    public SettingItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.setting_item,this);
        tv_top_title = (TextView)findViewById(R.id.tv_top_title);
        tv_below_des = (TextView) findViewById(R.id.tv_below_des);
        cb_set_box = (CheckBox) findViewById(R.id.cb_set_box);
        initAttrs(attrs);
        tv_top_title.setText(mDestitle);
    }
    private void initAttrs(AttributeSet attrs) {
        Log.d(TAG, "attrs.getAttributeCount()==="+attrs.getAttributeCount() );
        
        mDestitle = attrs.getAttributeValue(NAMESPACE,"des_title");
        mDeson = attrs.getAttributeValue(NAMESPACE,"des_on");
        mDesoff = attrs.getAttributeValue(NAMESPACE,"des_off");
    }
    public boolean isCheck() {
        return cb_set_box.isChecked();
    }
    public void setCheck(boolean isCheck){
        cb_set_box.setChecked(isCheck);
        if(isCheck){
            tv_below_des.setText(mDeson);
        }else{
            tv_below_des.setText(mDesoff);
        }

    }

}
