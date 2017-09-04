package com.bolo1.mobilesafe1.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bolo1.mobilesafe1.R;

/**
 * Created by 菠萝 on 2017/7/14.
 */

public class ToastUtil {

    private  String mnessage;
    private  Toast mToast;


    /**
     *
     * @param cxt  上下文
     * @param msg   需要吐司的消息
     */


   public static void show(Context cxt, String msg){
       Toast.makeText(cxt,msg,Toast.LENGTH_SHORT).show();
   }
   public  void  show(){
       mToast.show();
       Log.i("ToastUtil", "Toast show...");
   }
}
