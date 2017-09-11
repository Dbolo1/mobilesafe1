package com.bolo1.mobilesafe1.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

/**
 * Created by 菠萝 on 2017/7/14.
 */

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity.class";
    private String[] mTitleStrs;
    private GridView gv_name;
    private int[] mDrawableId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initUi();
        initData();
        requestPer1();

    }

    private void requestPer1() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    /**
     * 申请权限的方法
     */

    private void initUi() {
        TextView tv_marquee = (TextView) findViewById(R.id.tv_marquee);
        tv_marquee.setSelected(true);
        tv_marquee.setSingleLine(true);
        tv_marquee.setHorizontallyScrolling(true);
        gv_name = (GridView) findViewById(R.id.gv_name);
    }

    private void initData() {
        mTitleStrs = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };
        mDrawableId = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };
        gv_name.setAdapter(new MyAdapter());
        gv_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),BlackNumber.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AppManager.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),ProcessManage.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(),CacheCleanActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String psd = Sputils.getString(this, ConstantValue.MOBILE_SAFE_PAS, "");
        if (TextUtils.isEmpty(psd)) {
            //没有设置密码时
            SetPsdDialog();

        } else {
            //设置过密码时
            ConfirmDialog();
        }
    }

    private void ConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.confirmpsd_dialog, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        Button bt_confirm_submit = (Button) view.findViewById(R.id.bt_confirm_submit);
        Button bt_confirm_cancel = (Button) view.findViewById(R.id.bt_confirm_cancel);

        bt_confirm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String Confirmpsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(Confirmpsd)) {
                    String psd = Sputils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PAS, "");
                    //判断密码是否与本地密码一致
                    if (psd.equals(Md5Util.encoder(Confirmpsd))) {
                        //密码一致则跳跃
                        boolean set_over = Sputils.getBoolean(getApplicationContext(), ConstantValue.SET_OVER, false);
                        if (set_over) {
                            //如果完成设置则跳转到设置显示
                            startActivity(new Intent(getApplicationContext(), SetupOverActivity.class));
                            finish();
                        } else {
                            //如果没有完成则跳转到导航界面1
                            Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                            startActivity(intent);
                            finish();
                        }

                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
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
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.setpasdialog_dialog, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_set_confirm = (EditText) view.findViewById(R.id.et_set_confirm);
                String psd1 = et_set_psd.getText().toString();
                String confirm1 = et_set_confirm.getText().toString();
                if (!TextUtils.isEmpty(psd1) && !TextUtils.isEmpty(confirm1)) {
                    //密码都不为空时候
                    if (psd1.equals(confirm1)) {
                        //确认密码正确时
//                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
//                        startActivity(intent);
                        boolean set_over = Sputils.getBoolean(getApplicationContext(), ConstantValue.SET_OVER, false);
                        if (set_over) {
                            //如果完成设置则跳转到设置显示
                            startActivity(new Intent(getApplicationContext(), SetupOverActivity.class));
                        } else {
                            //如果没有完成则跳转到导航界面1
                            Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                        Sputils.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PAS, Md5Util.encoder(psd1));
                    } else {
                        ToastUtil.show(getApplication(), "确认密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
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

    class MyAdapter extends BaseAdapter {

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
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[position]);
            tv_title.setTextColor(Color.parseColor("#000000"));
            iv_icon.setBackgroundResource(mDrawableId[position]);
            return view;
        }
    }
}
