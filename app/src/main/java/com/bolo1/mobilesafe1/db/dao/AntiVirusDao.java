package com.bolo1.mobilesafe1.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bolo1.mobilesafe1.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/9.
 */

public class AntiVirusDao {
    final String path = "data/data/com.bolo1.mobilesafe1/files/antivirus.db";
    public List<String> getVirusList(){
        SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor=db.query("datable",new String[]{"md5"},null,null,null,null,null);
       List<String> VirusList= new ArrayList<String>();
        while (cursor.moveToNext()){
           VirusList.add(cursor.getString(0));
        }
        return VirusList;
    }

}
