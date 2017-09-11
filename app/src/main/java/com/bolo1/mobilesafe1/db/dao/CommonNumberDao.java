package com.bolo1.mobilesafe1.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/6.
 */

public class CommonNumberDao {
    final String path = "data/data/com.bolo1.mobilesafe1/files/commonnum.db";

    /**
     * 查询组的表信息
     *
     * @return
     */
    public List<Group> getGroup() {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        List<Group> groupList = new ArrayList<Group>();
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            groupList.add(group);
        }
        db.close();
        cursor.close();
        return groupList;
    }

    public List<Child> getChild(String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx, null);
        List<Child> childList = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childList.add(child);
        }
        db.close();
        cursor.close();
        return childList;
    }

    public class Group {
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child {
        public String _id;
        public String number;
        public String name;
    }
}
