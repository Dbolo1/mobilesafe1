package com.bolo1.mobilesafe1.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 菠萝 on 2017/7/15.
 */

public class Sputils {

    private static SharedPreferences sp;

    public static void putBoolean(Context ctx, String key, Boolean value){
        if(sp==null){
            sp = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }
    public static boolean getBoolean(Context ctx, String key, Boolean defValue){
        if(sp==null){
            sp = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }
    public static void putString(Context ctx, String key, String value){
        if(sp==null){
            sp = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).commit();
    }
    public static String getString(Context ctx, String key, String defValue){
        if(sp==null){
            sp = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }
}
