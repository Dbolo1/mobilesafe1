package com.bolo1.mobilesafe1.engine;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.bolo1.mobilesafe1.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/2.
 */

public class AppInfoProvider {
    /**
     *  获取包管理者 应用名 包名 图标 是否为系统应用 是否为sdcard应用
     * @param ctx 上下文环境
     * @return 返回appInfoList
     */
    public static List<AppInfo> getAppInfoList(Context ctx){
        //获取包管理者 应用名 包名 图标 是否为系统应用 是否为sdcard应用
        PackageManager pm =ctx.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();
        for(PackageInfo packageInfo : packageInfoList){
                AppInfo appInfo=  new AppInfo();
            appInfo.PackageName=packageInfo.packageName;
           ApplicationInfo applicationInfo =packageInfo.applicationInfo;
             appInfo.name=applicationInfo.loadLabel(pm).toString();
            appInfo.Icon=applicationInfo.loadIcon(pm);
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.isSystem=true;
            }else {
                appInfo.isSystem=false;
                //非系统应用
            }
            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)== ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                //sd卡应用
                appInfo.isSdCard=true;
            }else {
                appInfo.isSdCard=false;
                //手机应用
            }
            //将索引加入AppInfolist
            appInfoList.add(appInfo);
        }
        return appInfoList ;
    }
}
