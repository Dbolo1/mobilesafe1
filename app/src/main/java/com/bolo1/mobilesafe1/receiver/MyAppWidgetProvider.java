package com.bolo1.mobilesafe1.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bolo1.mobilesafe1.service.UpdateWidgetService;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class MyAppWidgetProvider extends AppWidgetProvider {
    private String tag="MyAppWidgetProvider";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(tag,"onReceive........");

        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(tag,"创建第一个窗口小控件........");
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(tag,"创建多个窗口小控件........");
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(tag,"创建多个窗口小控件........");
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(tag,"删除一个窗口小控件........");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(tag,"删除全部窗口小控件........");
        //关闭服务
        context.stopService(new Intent(context,UpdateWidgetService.class));
        super.onDisabled(context);
    }
}
