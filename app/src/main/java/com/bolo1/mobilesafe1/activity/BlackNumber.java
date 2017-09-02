package com.bolo1.mobilesafe1.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.dao.BlackNumberDao;
import com.bolo1.mobilesafe1.db.domain.BlackNumberInfo;
import com.bolo1.mobilesafe1.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 菠萝 on 2017/8/21.
 */

public class BlackNumber extends AppCompatActivity {

    private ListView lv_blacknumber;

    private BlackNumberAdapter numberAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (numberAdapter == null) {
                numberAdapter = new BlackNumberAdapter();
                lv_blacknumber.setAdapter(numberAdapter);
            } else {
                numberAdapter.notifyDataSetChanged();
            }
        }
    };
    private BlackNumberDao dao;
    private RadioButton rb_all;
    private RadioButton rb_phone;
    private RadioButton rb_sms;
    private RadioGroup rd_mode_grop;
    private int mode;
    private List<BlackNumberInfo> blackNumberInfoList;
    private boolean isLoad = false;
    private int mCount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        initView();
        initData();

    }

    private void initData() {
        blackNumberInfoList = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                dao = BlackNumberDao.getInstance(getApplicationContext());
                mCount = dao.getCount();
                blackNumberInfoList = dao.Allfind();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
    private void initView() {
        Toolbar tb_blacknumber = (Toolbar) findViewById(R.id.tb_blacknumber);
        setSupportActionBar(tb_blacknumber);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (blackNumberInfoList != null) {
                    if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE &&
                            lv_blacknumber.getLastVisiblePosition() >= blackNumberInfoList.size() - 1
                            && !isLoad) {
                       if (mCount>blackNumberInfoList.size()){
                           new Thread(){
                               @Override
                               public void run() {
                                   dao = BlackNumberDao.getInstance(getApplicationContext());
                                   List<BlackNumberInfo> moreList =  dao.find(blackNumberInfoList.size());
                                   blackNumberInfoList.addAll(moreList);
                                   mHandler.sendEmptyMessage(0);
                               }
                           }.start();
                       }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blacknumber, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_blacknumber:
                showAddDialog();
                break;
        }
        return true;
    }

    /**
     * 添加黑名单设置按钮
     */
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_addblcaknumber, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        rb_all = (RadioButton) view.findViewById(R.id.rb_all);
        rb_phone = (RadioButton) view.findViewById(R.id.rb_phone);
        rb_sms = (RadioButton) view.findViewById(R.id.rb_sms);
        rd_mode_grop = (RadioGroup) view.findViewById(R.id.rd_mode_grop);
        Button bt_blacknumber_confirm = (Button) view.findViewById(R.id.bt_blacknumber_confirm);
        final Button bt_blacknumber_cancel = (Button) view.findViewById(R.id.bt_blacknumber_cancel);
        rd_mode_grop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        final EditText et_blacknumber_phone = (EditText) view.findViewById(R.id.et_blacknumber_phone);
        bt_blacknumber_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_blacknumber_phone.getText())) {
                    String phone = et_blacknumber_phone.getText().toString();
                    //添加号码到数据库
                    dao.insert(phone, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setPhone(phone);
                    blackNumberInfo.setMode(mode + "");
                    blackNumberInfoList.add(0, blackNumberInfo);
                    if (numberAdapter != null) {
                        numberAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT);
                }
            }
        });
        bt_blacknumber_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private class BlackNumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return blackNumberInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.blacknumberitem_list, null);
                viewHolder = new ViewHolder();
                viewHolder.phone = (TextView) convertView.findViewById(R.id.tv_blacknumber);
                viewHolder.mode = (TextView) convertView.findViewById(R.id.tv_blackmode_mode);
                viewHolder.im_blacknumber_delete = (ImageView) convertView.findViewById(R.id.im_blacknumber_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.phone.setText(blackNumberInfoList.get(position).phone);
            int mode = Integer.parseInt(blackNumberInfoList.get(position).mode);
            switch (mode) {
                case 1:
                    viewHolder.mode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.mode.setText("拦截手机");
                    break;
                case 3:
                    viewHolder.mode.setText("拦截所有");
                    break;
            }
            viewHolder.im_blacknumber_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dao.delete(blackNumberInfoList.get(position).phone);
                    blackNumberInfoList.remove(position);
                    if (numberAdapter != null) {
                        numberAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView phone;
        TextView mode;
        ImageView im_blacknumber_delete;
    }
}
