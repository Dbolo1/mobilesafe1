package com.bolo1.mobilesafe1.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.bolo1.mobilesafe1.db.BlackNumberOpenHelper;
import com.bolo1.mobilesafe1.db.domain.BlackNumberInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/8/21.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper numberOpenHelper;

    private BlackNumberDao(Context context) {
        numberOpenHelper = new BlackNumberOpenHelper(context);
    }

    private static BlackNumberDao blackNumberDao = null;
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 向黑名单表内插入号码及类型
     * @param phone    传入的手机号码
     * @param mode      拦截类型   1为短信 2为电话 3为所有
     */
    public   void insert( String phone, String mode) {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * 删除黑名单表内号码及类型
     * @param phone  需要删除的手机号码
     */
    public void delete( String phone) {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        db.delete("blacknumber", "phone=?", new String[]{phone});
        db.close();
    }

    /**
     *  更新的手机号的拦截类型
     * @param phone 更新的手机号
     * @param mode 更新拦截类型   1为短信 2为电话 3为所有
     */
    public void update( String phone, String mode) {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber", values, "phone=?", new String[]{phone});
        db.close();
    }

    /**
     *  查询数据库中对应的手机号 及其拦截类型
     * @return  手机号与拦截类型的list数组
     */
    public List<BlackNumberInfo> Allfind() {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        Cursor cursor =  db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
         List<BlackNumberInfo> blacknumberList= new ArrayList<BlackNumberInfo>();

        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blacknumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blacknumberList;
    }
    public List<BlackNumberInfo>  find(int index) {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        Cursor cursor =  db.rawQuery("select phone,mode from blacknumber order by _id desc  limit ?,20",new String[]{index+""});
        List<BlackNumberInfo> blacknumberList= new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blacknumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blacknumberList;
    }
    public int  getCount() {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        int count=0;
        Cursor cursor =  db.rawQuery("select count(*) from blacknumber ",null);
        while (cursor.moveToNext()){
          count= cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    public int  getMode(String phone) {
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        int  mode=0;
        Cursor cursor =  db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null,null,null);
        while (cursor.moveToNext()){
            mode= cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
