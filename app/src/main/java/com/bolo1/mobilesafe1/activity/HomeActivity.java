package com.bolo1.mobilesafe1.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Md5Util;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;

import org.w3c.dom.Text;

/**
 * Created by 菠萝 on 2017/7/14.
 */

public class HomeActivity extends AppCompatActivity {
    private static final String TAG ="HomeActivity.class" ;
    private String[] mTitleStrs;
    private GridView gv_name;
    private int[] mDrawableId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.home_activity);
        initUi();
        initData();

    }

    private void initUi() {
        gv_name = (GridView) findViewById(R.id.gv_name);
    }

    private void initData() {
        mTitleStrs = new String[]{
                "手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"
        };
        mDrawableId = new int[]{
                R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
                R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
        };
        gv_name.setAdapter(new MyAdapter());
        gv_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        showDialog();
                        break;
                    case 8:
                        Intent intent =new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(intent);
                        finish();
                }
            }
        });
    }

    private void showDialog() {
        String psd=Sputils.getString(this, ConstantValue.MOBILE_SAFE_PAS,"");
        if(TextUtils.isEmpty(psd)){
            //没有设置密码时
            SetPsdDialog();

        }else{
            //设置过密码时
            ConfirmDialog();
        }
    }

    private void ConfirmDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.confirmpsd_dialog,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
    Button  bt_confirm_submit = (Button) view.findViewById(R.id.bt_confirm_submit);
        Button bt_confirm_cancel = (Button) view.findViewById(R.id.bt_confirm_cancel);

        bt_confirm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText  et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String Confirmpsd = et_confirm_psd.getText().toString();
                if(!TextUtils.isEmpty(Confirmpsd)){
                    String psd=Sputils.getString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PAS,"");
                    //判断密码是否与本地密码一致
                    if(psd.equals(Md5Util.encoder(Confirmpsd))){
                        //密码一致则跳跃
                        Intent intent=new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }else{
                        ToastUtil.show(getApplicationContext(),"密码错误");
                    }
                }else{
                   ToastUtil.show(getApplicationContext(),"密码不能为空");
                }
            }
        });
        bt_confirm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void SetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog  dialog = builder.create();
        final View view = View.inflate(this,R.layout.setpasdialog_dialog,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button  bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText  et_set_psd= (EditText) view.findViewById(R.id.et_set_psd);
                EditText  et_set_confirm= (EditText) view.findViewById(R.id.et_set_confirm);
                String psd1= et_set_psd.getText().toString();
                String confirm1=et_set_confirm.getText().toString();
                if(!TextUtils.isEmpty(psd1) && !TextUtils.isEmpty(confirm1)){
                    //密码都不为空时候
                    if (psd1.equals(confirm1)){
                        //确认密码正确时
                        Intent intent=new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        Sputils.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PAS, Md5Util.encoder(psd1));
                    }else{
                        ToastUtil.show(getApplication(),"确认密码错误");
                    }
                }else{
                    ToastUtil.show(getApplicationContext(),"密码不能为空");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    class MyAdapter extends BaseAdapter{

        private TextView tv_title;
        private ImageView iv_icon;

        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          View view= View.inflate(getApplicationContext(),R.layout.gridview_item,null);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableId[position]);
            return view;
        }
    }
}
