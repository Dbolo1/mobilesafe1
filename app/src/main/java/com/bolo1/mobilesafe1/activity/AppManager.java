package com.bolo1.mobilesafe1.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.domain.AppInfo;
import com.bolo1.mobilesafe1.engine.AppInfoProvider;

import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/2.
 */

public class AppManager extends AppCompatActivity {

    private TextView tv_phone_memory;
    private TextView tv_sd_memory;
    private ListView lv_appmanager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mCustomerList!=null) {
                adapter = new AppAdapter();
                lv_appmanager.setAdapter(adapter);
                tv_des.setText("手机应用个数(" + mCustomerList.size() + ")");
            }
        }
    };
    private AppAdapter adapter;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mSystemList;
    private List<AppInfo> mCustomerList;
    private TextView tv_des;


    private class AppAdapter extends BaseAdapter {
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0|| position == mCustomerList.size()+1){
                //展示纯文本
                return 0;
            }else {
                //展示文本+图片
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mCustomerList.size()+mSystemList.size()+2;
        }

        @Override
        public AppInfo getItem(int position) {
            if(position==0 && position ==mCustomerList.size()+1){
                return null;
            }else {
                if (position<mCustomerList.size()+1){
                    return mCustomerList.get(position-1);
                }else {
                    return mSystemList.get(position-mCustomerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                int type=getItemViewType(position);
            if (type==0){
                //展示纯文本
                ViewTitleHolder holder=null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item_title, null);
                    holder=new ViewTitleHolder();
                  holder.tv_title= (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if(position==0){
                    //手机应用
                    holder.tv_title.setText("手机应用个数("+mCustomerList.size()+")");
                }else {
                    //系统应用
                    holder.tv_title.setText("系统应用个数("+mSystemList.size()+")");
                }
                return convertView;

            }else {
                //展示文本+图片
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item, null);
                    holder=new ViewHolder();
                    holder.icon = (ImageView) convertView.findViewById(R.id.im_app_icon);
                    holder.packagename = (TextView) convertView.findViewById(R.id.tv_app_packager_name);
                    holder.storage_location = (TextView) convertView.findViewById(R.id.tv_storage_location);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.icon.setBackgroundDrawable(getItem(position).Icon);
                holder.packagename.setText((getItem(position).name));
                if(getItem(position).isSdCard){
                    holder.storage_location.setText("sd卡应用");
                }else{
                    holder.storage_location.setText("手机应用");
                }
                return convertView;
            }

        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmanager_activity);
        initUI();
        initTitle();
        initList();
    }

    private void initList() {
        lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
        tv_des = (TextView) findViewById(R.id.tv_des);
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<AppInfo>();
                mCustomerList = new ArrayList<AppInfo>();
                for(AppInfo appInfo : mAppInfoList){
                    if(appInfo.isSystem){
                        //系统应用
                        mSystemList.add(appInfo);
                    }else {
                        //手机应用
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                      if(mCustomerList!=null&&mSystemList!=null) {
                          if (firstVisibleItem >= mCustomerList.size() + 1) {
                              //系统应用个数
                              tv_des.setText("系统应用个数(" + mSystemList.size() + ")");
                          } else {
                              //手机应用个数
                              tv_des.setText("手机应用个数(" + mCustomerList.size() + ")");
                          }
                      }
            }
        });
    }
    private void initUI() {
        tv_phone_memory = (TextView) findViewById(R.id.tv_phone_memory);
        tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
    }
    private void initTitle() {
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String memory = android.text.format.Formatter.formatFileSize(this, getAvailSpace(path));
        String sd_memory = android.text.format.Formatter.formatFileSize(this, getAvailSpace(sdPath));
        tv_phone_memory.setText("内存可用空间:" + memory);
        tv_sd_memory.setText("sd卡可用空间:" + sd_memory);
    }

    private long getAvailSpace(String path) {
        StatFs statfs = new StatFs(path);
        long count = statfs.getAvailableBlocks();
        long size = statfs.getBlockSize();
        return count * size;
    }
    static class ViewHolder {
        ImageView icon;
        TextView packagename;
        TextView storage_location;
    }
    static class ViewTitleHolder {
       TextView tv_title;
    }
}
