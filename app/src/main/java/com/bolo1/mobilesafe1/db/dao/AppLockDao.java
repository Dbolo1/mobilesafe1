package com.bolo1.mobilesafe1.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.bolo1.mobilesafe1.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class AppLockDao {

    private final AppLockOpenHelper appLockOpenHelper;
    private final Context context;

    public AppLockDao(Context context) {
        this.context=context;
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    private static AppLockDao AppLockDao = null;

    public static AppLockDao getInstance(Context context) {
        if (AppLockDao == null) {
            AppLockDao = new AppLockDao(context);
        }
        return AppLockDao;
    }

    public void insert(String packageName) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packageName);
        db.insert("applock", null, contentValues);
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
    }
    public void delete(String packageName) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packageName);
        db.delete("applock", "packagename=?", new String[]{packageName});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);


    }
    public List<String> findAll() {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        List<String> packagelockList = new ArrayList<String>();
        Cursor cursor = db.query("applock", new String[]{"packageName"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String packagelock = cursor.getString(0);
            packagelockList.add(packagelock);
        }
        cursor.close();
        db.close();
        return packagelockList;
    }
}
