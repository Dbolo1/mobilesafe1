package com.bolo1.mobilesafe1.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by 菠萝 on 2017/8/12.
 */

public class Address {
    private static String path = "data/data/com.bolo1.mobilesafe1/files/address.db";
    private static String tag = "Address";

    private static String mAddress;
    private static SQLiteDatabase db;

    public static String getAddress( String phone) {
        mAddress = "未知号码";
        String regularExpression = "^1[3-8]\\d{9}";
        if (phone.matches(regularExpression)) {
            final String phone1 = phone.substring(0, 7);
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                    Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone1}, null, null, null);
                    //利用电话查询表一找到outkey
                    if (cursor.moveToNext()) {
                        String outkey = cursor.getString(0);
                        Cursor indexCursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                        if (indexCursor.moveToNext()) {
                            mAddress = indexCursor.getString(0);
                        }
                    } else {
                        mAddress = "未知号码";
                    }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    // 匪警电话 ,110,120等
                    mAddress = "报警电话";
                    break;
                case 4:
                    // 模拟器电话,5554,5556
                    mAddress = "模拟器";
                    break;
                case 5:
                    // 客服电话,95555
                    mAddress = "客服电话";
                    break;
                case 7:
                case 8:
                    // 本地电话
                    mAddress = "本地电话";
                    break;
                case 11:
                    String area=phone.substring(1,3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
                if(cursor.moveToNext()){
                    mAddress=cursor.getString(0);
                }else {
                    mAddress = "未知号码";
                }
                break;
                case 12:
                    String area1=phone.substring(1,4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
                    if(cursor1.moveToNext()){
                        mAddress=cursor1.getString(0);
                    }else {
                        mAddress = "未知号码";
                    }
                    break;
                default:
                    mAddress = "未知号码";
                    break;
            }
            return mAddress;
        }
            return mAddress;
           }
    }
