package com.bolo1.mobilesafe1.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.StreamTools;
import com.bolo1.mobilesafe1.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity.class";
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_EX = 102;
    private static final int IO_EX = 103;
    private static final int JSON_EX = 104;
    private TextView tv_version_name;
    private String versionName;
    private String versionDos;
    private String versionCode;
    private String downloadUrl;
    private int mLocalVersionCode;
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case IO_EX:
                    ToastUtil.show(SplashActivity.this, "IO异常");
                    enterHome();
                    break;
                case JSON_EX:
                    ToastUtil.show(SplashActivity.this, "JSON异常");
                    enterHome();
                    break;
                case URL_EX:
                    ToastUtil.show(SplashActivity.this, "URL异常");
                    enterHome();
                    break;
            }
        }
    };

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("亲~有新版本哦!");
        builder.setIcon(R.drawable.e_163);
        builder.setMessage(versionDos);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApp();
            }
        });
        builder.setNegativeButton("我不要", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void downloadApp() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡挂载上时
            HttpUtils hu = new HttpUtils();
            String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe1_2.0.apk";
            hu.download(downloadUrl, localPath, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.d(TAG, "下载成功：。。。");
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.d(TAG, "下载中：。。。" + total);
                    Log.d(TAG, "下载进度+" + current);
                    super.onLoading(total, current, isUploading);
                    //正在下载


                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.d(TAG, "下载失败：。。。");
                }
            });
        }
    }

    /**
     * 安装apk
     *
     * @param file 获取下载apk的路径
     */
    private void installApk(File file) {
        //系统安装源码
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        initUi();
        initData();
    }

    private void initUi() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }

    private void initData() {
        tv_version_name.setText("版本号:" + getVersionName());
        //判断是否要更新应用
        mLocalVersionCode = getVersionCode();
        //判断是否小于服务器的版本号
        checkVersion();
    }

    /**
     * 请求网络
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                long start = System.currentTimeMillis();
                try {
                    URL url = new URL("http://192.168.56.1:8080/update2.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        //连接请求成功
                        InputStream in = connection.getInputStream();
                        String result = StreamTools.readFromStream(in);
                        JSONObject json = new JSONObject(result);
                        versionName = json.getString("versionName");
                        versionDos = json.getString("versionDos");
                        versionCode = json.getString("versionCode");
                        downloadUrl = json.getString("downloadUrl");
                        Log.d(TAG, "versionDos：" + versionDos);
                        //判断是否需要更新应用
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_EX;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = IO_EX;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_EX;
                    e.printStackTrace();
                } finally {
                    long end = System.currentTimeMillis();
                    if (end - start < 4000) {
                        try {
                            Thread.sleep(4000 - (end - start));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }


    /**
     * 获取版本名称
     *
     * @return null代表异常
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取版本号
     *
     * @return 0代表异常
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
