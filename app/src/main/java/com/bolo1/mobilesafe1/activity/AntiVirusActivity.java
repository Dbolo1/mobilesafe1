package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.dao.AntiVirusDao;
import com.bolo1.mobilesafe1.db.domain.AppInfo;
import com.bolo1.mobilesafe1.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 菠萝 on 2017/9/9.
 */

public class AntiVirusActivity extends AppCompatActivity {

    private static final int SCANNING = 100;
    private static final int SCAN_FINISH = 101;
    private ImageView im_scan;
    private TextView tv_scan_name;
    private ProgressBar pb_scan;
    private LinearLayout ll_add_text;
    private int index = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    //扫描中更新UI
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scan_name.setText(scanInfo.name);
                    TextView textview = new TextView(getApplicationContext());
                    if (scanInfo.isVirusa) {
                        textview.setTextColor(Color.RED);
                        textview.setText("发现病毒:" + scanInfo.name);
                    } else {
                        textview.setTextColor(Color.BLACK);
                        textview.setText("扫描安全:" + scanInfo.name);
                    }
                    ll_add_text.addView(textview,0);
                    break;
                case SCAN_FINISH:
                    //结束时更新UI
                    tv_scan_name.setText("扫描完成");
                    im_scan.clearAnimation();
                    //通知用户发现的病毒
                    UninstallVirus();

                    break;
            }
        }
    };
    private List<ScanInfo> mVirusInfoList;

    private void UninstallVirus() {
        for (ScanInfo scanInfo : mVirusInfoList) {
            String packagename = scanInfo.packagename;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package" + packagename));
            startActivity(intent);

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.antivirus_activity);
        initUI();
        initAnimation();
        checkScanVirus();
    }

    private void checkScanVirus() {
        new Thread() {
            @Override
            public void run() {
                PackageManager pm = getPackageManager();
                //获得所有应用的签名
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES
                        + PackageManager.GET_UNINSTALLED_PACKAGES);
                //获得病毒库的签名MD5
                List<String> VirusList = new AntiVirusDao().getVirusList();
                //循环遍历所有应用
                //获得病毒集合
                mVirusInfoList = new ArrayList<ScanInfo>();
                //获得所有应用集合
                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
                pb_scan.setMax(packageInfoList.size());
                for (PackageInfo packageInfo : packageInfoList) {
                    //每次循环创建scaninfo
                    ScanInfo scanInfo = new ScanInfo();
                    //获得应用的签名
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    //把签名转换为字符
                    String string = signature.toCharsString();
                    String encode = Md5Util.encoder(string);
                    //将应用的签名md5与病毒库匹配
                    if (VirusList.contains(encode)) {
                        //记录病毒
                        scanInfo.isVirusa = true;
                        mVirusInfoList.add(scanInfo);
                    } else {
                        scanInfo.isVirusa = false;
                    }
                    scanInfo.packagename = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);
                    index++;
                    pb_scan.setProgress(index);
                    //发送消息去更新UI
                    try {
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    class ScanInfo {
        boolean isVirusa;
        String packagename;
        String name;
    }

    private void initAnimation() {
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        rotate.setRepeatCount(RotateAnimation.INFINITE);
        rotate.setFillAfter(true);
        im_scan.startAnimation(rotate);
    }

    private void initUI() {
        im_scan = (ImageView) findViewById(R.id.im_scan);
        tv_scan_name = (TextView) findViewById(R.id.tv_scan_name);
        pb_scan = (ProgressBar) findViewById(R.id.pb_scan);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

    }

}
