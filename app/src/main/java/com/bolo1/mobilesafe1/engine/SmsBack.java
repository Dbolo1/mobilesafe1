package com.bolo1.mobilesafe1.engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 菠萝 on 2017/9/2.
 */

public class SmsBack {


    private static int index=0;



    public static void backup(Context ctx,String path,CallBack callback) {
        FileOutputStream fos=null;
       Cursor cursor=null;
        try {

            File file = new File(path);
//            Telephony.Sms.CONTENT_URI
            cursor = ctx.getContentResolver().
                    query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"}, null, null, null);
            fos= new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //进度条可变
//            pd.setMax(cursor.getCount());
            callback.setMax(cursor.getCount());
            xmlSerializer.setOutput(fos,"utf-8");
            xmlSerializer.startDocument("utf-8",true);
            xmlSerializer.startTag(null, "smss");
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null,"sms");

                xmlSerializer.startTag(null,"address");
                xmlSerializer.text( cursor.getString(0));
                xmlSerializer.endTag(null,"address");

                xmlSerializer.startTag(null,"date");
                xmlSerializer.text( cursor.getString(1));
                xmlSerializer.endTag(null,"date");

                xmlSerializer.startTag(null,"type");
                xmlSerializer.text( cursor.getString(2));
                xmlSerializer.endTag(null,"type");

                xmlSerializer.startTag(null,"body");
                xmlSerializer.text( cursor.getString(3));
                xmlSerializer.endTag(null,"body");

                xmlSerializer.endTag(null,"sms");

                index++;
                Thread.sleep(500);
                //进度条可变
//                pd.setProgress(index);
                callback.setProgress(index);
            }
            xmlSerializer.endTag(null,"smss");
            xmlSerializer.endDocument();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null&&fos!=null){
                cursor.close();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 设置观察者模式,在此自定义进度条样式
     */
    public interface CallBack{
        /**
         *  设置进度条最大进度
         * @param max
         */
        public void setMax(int max);

        /**
         * 设置进度条进度
         * @param index
         */
        public void setProgress(int index);
    }
}
