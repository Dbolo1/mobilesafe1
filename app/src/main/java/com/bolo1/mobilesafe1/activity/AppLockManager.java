package com.bolo1.mobilesafe1.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.dao.AppLockDao;
import com.bolo1.mobilesafe1.db.dao.CommonNumberDao;
import com.bolo1.mobilesafe1.db.domain.AppInfo;
import com.bolo1.mobilesafe1.engine.AppInfoProvider;
import com.bolo1.mobilesafe1.engine.ProcessInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class AppLockManager extends AppCompatActivity {

    private Button bt_lock;
    private Button bt_unlock;
    private TextView tv_lock;
    private TextView tv_unlock;
    private ListView lv_lock;
    private ListView lv_unlock;
    private LinearLayout ll_lock;
    private LinearLayout ll_unlock;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //设置适配器
            mLockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            mUnLockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(mUnLockAdapter);

        }
    };
    private List<AppInfo> mLockList;
    private List<AppInfo> mUnLockList;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnLockAdapter;
    private AppLockDao mDao;
    private TranslateAnimation translateAnimation;

    public class MyAdapter extends BaseAdapter {

        private boolean isLock;//用于判断该listview是否加锁

        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                tv_lock.setText("加锁应用:" + mLockList.size());
                return mLockList.size();
            } else {
                tv_unlock.setText("未加锁应用:" + mUnLockList.size());
                return mUnLockList.size();
            }

        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockList.get(position);
            } else {
                return mUnLockList.get(position);
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.applock_item_lock, null);
                holder.im_app_lock_icon = (ImageView) convertView.findViewById(R.id.im_app_lock_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.im_islock = (ImageView) convertView.findViewById(R.id.im_islock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo=getItem(position);
            final View animationView=convertView;
            holder.im_app_lock_icon.setBackgroundDrawable(getItem(position).Icon);
            holder.tv_app_name.setText(getItem(position).name);
            if (isLock) {
                holder.im_islock.setBackgroundResource(R.drawable.lock);
            } else {
                holder.im_islock.setBackgroundResource(R.drawable.unlock);
            }
            holder.im_islock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //开启动画
                    animationView.startAnimation(translateAnimation);
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock) {
                                //从锁数组里移除,向未锁数组里面添加
                                mLockList.remove(appInfo);
                                mUnLockList.add(appInfo);
                                //向数据库中删除一条数据
                                mDao.delete(appInfo.PackageName);
                                mLockAdapter.notifyDataSetChanged();
                            }else {
                                mUnLockList.remove(appInfo);
                                mLockList.add(appInfo);
                                mDao.insert(appInfo.PackageName);
                                mUnLockAdapter.notifyDataSetChanged();
                            }

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                }
            });
            return convertView;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applock_activity);
        initUI();
        initData();
        initAnimation();
    }

    private void initAnimation() {
        translateAnimation = new TranslateAnimation(
                 Animation.RELATIVE_TO_SELF, 0,
                 Animation.RELATIVE_TO_SELF, 1,
                 Animation.RELATIVE_TO_SELF, 0,
                 Animation.RELATIVE_TO_SELF, 0
         );
        translateAnimation.setDuration(500);
    }

    private void initData() {

        new Thread() {
            @Override
            public void run() {
                List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mLockList = new ArrayList<AppInfo>();
                mUnLockList = new ArrayList<AppInfo>();

                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appInfo : appInfoList) {
                    if (lockPackageList.contains(appInfo.PackageName)) {
                        //上锁应用
                        mLockList.add(appInfo);
                    } else {
                        //未上锁应用
                        mUnLockList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initUI() {
        bt_lock = (Button) findViewById(R.id.bt_lock);
        bt_unlock = (Button) findViewById(R.id.bt_unlock);

        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);

        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);

        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                mLockAdapter.notifyDataSetChanged();
            }
        });
        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_lock.setBackgroundResource(R.drawable.tab_right_default);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
                mUnLockAdapter.notifyDataSetChanged();
            }
        });


    }

    static class ViewHolder {
        ImageView im_app_lock_icon;
        TextView tv_app_name;
        ImageView im_islock;
    }
}
