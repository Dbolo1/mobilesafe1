package com.bolo1.mobilesafe1.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.domain.AppInfo;
import com.bolo1.mobilesafe1.engine.AppInfoProvider;
import com.bolo1.mobilesafe1.utils.ToastUtil;

import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/2.
 */

public class AppManager extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_phone_memory;
    private TextView tv_sd_memory;
    private ListView lv_appmanager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mCustomerList != null&&adapter!=null) {
                adapter.notifyDataSetChanged();
            }else {
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
    private AppInfo mappInfo;
    private PopupWindow popupwindow;

    private class AppAdapter extends BaseAdapter {


        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //展示纯文本
                return 0;
            } else {
                //展示文本+图片
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mCustomerList.size() + mSystemList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 && position == mCustomerList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                //展示纯文本
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    //手机应用
                    holder.tv_title.setText("手机应用个数(" + mCustomerList.size() + ")");
                } else {
                    //系统应用
                    holder.tv_title.setText("系统应用个数(" + mSystemList.size() + ")");
                }
                return convertView;

            } else {
                //展示文本+图片
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item, null);
                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView.findViewById(R.id.im_app_icon);
                    holder.packagename = (TextView) convertView.findViewById(R.id.tv_app_packager_name);
                    holder.storage_location = (TextView) convertView.findViewById(R.id.tv_storage_location);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.icon.setBackgroundDrawable(getItem(position).Icon);
                holder.packagename.setText((getItem(position).name));
                if (getItem(position).isSdCard) {
                    holder.storage_location.setText("sd卡应用");
                } else {
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
        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerList != null && mSystemList != null) {
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
        lv_appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size() + 1) {
                        mappInfo = mCustomerList.get(position - 1);
                    } else {
                        mappInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow_view, null);
        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);
        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        //为popup_window设置透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        //设置缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);

        AnimationSet animationset = new AnimationSet(true);
        animationset.addAnimation(scaleAnimation);
        animationset.addAnimation(alphaAnimation);


        popupwindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupwindow.setBackgroundDrawable(new ColorDrawable());
        popupwindow.showAsDropDown(view, 100, -view.getHeight() - view.getHeight() / 2);
        popupView.startAnimation(animationset);

    }

    /**
     * popupWindow 点击事件
     *
     * @param v popupView
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                uninstall();
                break;
            case R.id.tv_start:
                launchApp();
                break;
            case R.id.tv_share:
                shareApp();
                break;
        }
        if (popupwindow!=null){
            popupwindow.dismiss();
        }
    }

    /**
     * 分享app
     */
    private void shareApp() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"这是一个非常棒的软件！我推荐给你~"+mappInfo.name);
        startActivity(intent);
    }

    /**
     * 启动app
     */
    private void launchApp() {
        try {
            PackageManager pm=this.getPackageManager();
            Intent intent=pm.getLaunchIntentForPackage(mappInfo.PackageName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this,"无法启动app");
        }
    }

    /**
     * 通过包名卸载应用
     */
    private void uninstall() {
        if (mappInfo.isSystem) {
            ToastUtil.show(getApplicationContext(), "系统应用无法卸载");
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mappInfo.PackageName));
            startActivityForResult(intent,0);
        }
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<AppInfo>();
                mCustomerList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
                        //系统应用
                        mSystemList.add(appInfo);
                    } else {
                        //手机应用
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
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
