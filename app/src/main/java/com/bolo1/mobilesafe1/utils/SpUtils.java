package com.bolo1.mobilesafe1.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 菠萝 on 2017/7/5.
 */

public class SpUtils {


    private static SharedPreferences sp;

    public static void putBoolean(Context cxt, String key,boolean  value){
        if (sp==null){
            sp = cxt.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }
    public static boolean getBoolean(Context cxt, String key,boolean  value){
        if (sp==null){
            sp = cxt.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
       return sp.getBoolean(key,value);
    }
/**
 * 判断是否设置过密码
 */
public static void putString(Context cxt, String key,String  value){
    if (sp==null){
        sp = cxt.getSharedPreferences("config", Context.MODE_PRIVATE);
    }
    sp.edit().putString(key,value).commit();
}
    public static String getString(Context cxt, String key,String  value){
        if (sp==null){
            sp = cxt.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,value);
    }


}
