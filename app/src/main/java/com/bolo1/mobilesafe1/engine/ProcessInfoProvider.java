package com.bolo1.mobilesafe1.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.domain.ProcessInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/4.
 */

public class ProcessInfoProvider {

    private static FileReader fileReader;
    private static BufferedReader bufferedReader;

    /**
     * @param cxt 上下文环境
     * @return 返回进程数
     */
    public static int getProcessCount(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }
    //获得可用内存

    /**
     * 获取可用内存
     *
     * @param cxt 上下文
     * @return 0为空或者异常
     */
    public static long getAvailSpace(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * @param ctx 上下文
     * @return 返回可用的内存为0则异常 单位bytes
     */
    public static long getTotalSpace(Context ctx) {
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            //将字符串转化为字符数组
            char[] charArray = line.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : charArray) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            //将字符数组转化为long类型
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     *
     * @param cxt 上下文
     * @return     当前手机运行的进程信息
     */
    public static List<ProcessInfo> getProcessInfo(Context cxt) {
         List<ProcessInfo> processInfoList=new ArrayList<ProcessInfo>();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = cxt.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //获得运行的进程并且循环遍历 获得每一个进程的包,name,icon
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.processName;
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取已经使用的内存
            processInfo.memSize = memoryInfo.getTotalPrivateDirty();
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                processInfo.icon = applicationInfo.loadIcon(pm);
                //判断该程序是否为系统程序
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.name =info.processName;
                processInfo.icon= cxt.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
            return  processInfoList;
    }

    /**
     *      杀死索引进程
     * @param cxt 上下文
     * @param processInfo  进程索引
     */
    public static void KillsProcess(Context cxt,ProcessInfo processInfo) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processInfo.packageName);
    }
}
