package com.bolo1.mobilesafe1.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 菠萝 on 2017/7/15.
 */

public class Sputils {

    private static SharedPreferences sp;

    /**
     *   存储boolean值
     * @param ctx  上下文环境
     * @param key  键对
     * @param value 值
     */
    public static void putBoolean(Context ctx, String key, Boolean value){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }

    /**
     *   获取boolean值
     * @param ctx  上下文环境
     * @param key   键对
     * @param defValue 默认值
     * @return    返回boolean值
     */
    public static boolean getBoolean(Context ctx, String key, Boolean defValue){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }

    /**
     *  存储String字符串
     * @param ctx  上下文环境
     * @param key     键对
     * @param value 值
     */
    public static void putString(Context ctx, String key, String value){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).commit();
    }

    /**
     *  获取String字符串
     * @param ctx  上下文环境
     * @param key   键对
     * @param defValue  默认值
     * @return  返回string值
     */
    public static String getString(Context ctx, String key, String defValue){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }

    /**
     *    移除key
     * @param ctx  上下文环境
     * @param key  键对
     */
    public static void remove(Context ctx, String key) {
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    /**
     *
     * @param ctx 上下文环境
     * @param key  存储int的key
     * @param value 存储的值
     */
    public static void putInt(Context ctx, String key, int value){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).commit();
    }

    /**
     *
     * @param ctx  上下文环境
     * @param key   获取Int类型的key
     * @param defValue  默认为0
     * @return 返回int的值
     */
    public static int getInt(Context ctx, String key, int defValue){
        if(sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key,0);
    }

}
