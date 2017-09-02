package com.bolo1.mobilesafe1.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.bolo1.mobilesafe1.service.AddressService;

import java.util.List;

/**
 * Created by 菠萝 on 2017/8/16.
 */

public class ServiceUtils {
    private static List<ActivityManager.RunningServiceInfo> runningServices;
    public static boolean isRunning(Context cxt, String ServiceName) {
        ActivityManager mAM = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        runningServices = mAM.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo rsi : runningServices) {
            if (ServiceName.equals(rsi.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
