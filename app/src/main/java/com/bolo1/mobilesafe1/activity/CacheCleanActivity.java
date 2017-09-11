package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;

import java.lang.reflect.Method;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

/**
 * Created by 菠萝 on 2017/9/11.
 */

public class CacheCleanActivity extends AppCompatActivity {

    private static final int UP_DATA_VIEW = 100;
    private static final int UP_SCAN_NAME = 101;
    private static final int SCAN_FINISH = 102;
    private static final int CACHE_ALL = 103;
    private ProgressBar pb_cache;
    private TextView tv_cache_name;
    private LinearLayout ll_add_cache;
    private PackageManager mPm;
    private int index=0;
    private static final String tag="CacheCleanActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UP_DATA_VIEW:
                    //在这里更新UI
                    AddCacheView(msg);
                    break;
                case UP_SCAN_NAME:
                    //显示扫描的名称
                    String strName= (String) msg.obj;
                    tv_cache_name.setText("正在扫描:"+strName);
                    break;
                case SCAN_FINISH:
                    tv_cache_name.setText("扫描完成");
                    break;
                case CACHE_ALL:
                    ll_add_cache.removeAllViews();
                    break;
            }
        }
    };
    private ImageView im_delete;
    private CacheInfo cacheInfo;


    /**
     *  将有缓存的应用添加至布局中
     * @param msg
     */
    private void AddCacheView(Message msg) {
        View view = View.inflate(getApplicationContext(), R.layout.lv_cache_item, null);
        ImageView im_app_icon= (ImageView) view.findViewById(R.id.im_app_icon);
        TextView tv_app_packager_name= (TextView) view.findViewById(R.id.tv_app_packager_name);
        TextView  tv_memory_info= (TextView)view.findViewById(R.id.tv_memory_info);
        im_delete = (ImageView) view.findViewById(R.id.im_delete);
        cacheInfo = (CacheInfo) msg.obj;
        im_app_icon.setBackgroundDrawable(cacheInfo.icon);
        tv_app_packager_name.setText(cacheInfo.name);
        tv_memory_info.setText("缓存大小:" + android.text.format.Formatter.
                formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
        ll_add_cache.addView(view,0);
        im_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //"android.settings.APPLICATION_DETAILS_SETTINGS"
                Intent intent=new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:"+ cacheInfo.packageName));
                startActivityForResult(intent,0);
            }
        });

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cacheclean_activity);
        initUI();
        initData();

    }



    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mPm = getPackageManager();
                List<PackageInfo> installedPackageInfo = mPm.getInstalledPackages(0);
                pb_cache.setMax(installedPackageInfo.size());
                for (PackageInfo packageInfo : installedPackageInfo) {
                    String packagename = packageInfo.packageName;
                    getPackageCache(packagename);
                    try {
                        String appname= mPm.getApplicationInfo(packagename,0).loadLabel(mPm).toString();
                        Message msg= Message.obtain();
                        msg.what=UP_SCAN_NAME;
                        msg.obj=appname;
                        mHandler.sendMessage(msg);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    index++;
                    pb_cache.setProgress(index);
                    try {
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg= Message.obtain();
                msg.what=SCAN_FINISH;
                mHandler.sendMessage(msg);


            }
        }.start();

    }

    private void initUI() {
        Toolbar tb_cache = (Toolbar) findViewById(R.id.tb_cache);
        setSupportActionBar(tb_cache);
        pb_cache = (ProgressBar) findViewById(R.id.pb_cache);
        tv_cache_name = (TextView) findViewById(R.id.tv_cache_name);
        ll_add_cache = (LinearLayout) findViewById(R.id.ll_add_cache);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cacheclean, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cache_clean:
                    CleanAll();

                break;
        }
        return true;
    }

    /**
     *  清理所有应用
     */
    private void CleanAll() {

        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    //需要更新UI
                    Message msg= Message.obtain();
                    msg.what=CACHE_ALL;
                    mHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public void getPackageCache(final String PackageName) {


                IPackageStatsObserver statsObserver = new IPackageStatsObserver.Stub() {
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        long cacheSize = pStats.cacheSize;
                        if (cacheSize > 0) {
                            //通知主线程更新UI
                            Message msg = Message.obtain();
                            msg.what = UP_DATA_VIEW;
                             CacheInfo  cacheInfo = new CacheInfo();
                            cacheInfo.packageName = pStats.packageName;
                            cacheInfo.cacheSize = cacheSize;
                            try {
                                cacheInfo.name = mPm.getApplicationInfo(pStats.packageName, 0).loadLabel(mPm).toString();
                                cacheInfo.icon = mPm.getApplicationInfo(pStats.packageName, 0).loadIcon(mPm);

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            msg.obj = cacheInfo;
                            mHandler.sendMessage(msg);
                            Log.d(tag,"-----------消息发送成功"+ cacheInfo.packageName);
                        }
                    }
                };
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPm, PackageName, statsObserver);
        } catch (Exception e) {
            e.printStackTrace();

        }



    }

    class CacheInfo {
        public String name;
        public Drawable icon;
        public String packageName;
        public long cacheSize;
    }
}
