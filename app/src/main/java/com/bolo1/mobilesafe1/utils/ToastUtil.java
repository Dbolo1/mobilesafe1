package com.bolo1.mobilesafe1.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 菠萝 on 2017/7/14.
 */

public class ToastUtil {
    /**
     *
     * @param cxt  上下文
     * @param msg   需要吐司的消息
     */
   public static void show(Context cxt, String msg){
       Toast.makeText(cxt,msg,Toast.LENGTH_SHORT).show();
   }
}
