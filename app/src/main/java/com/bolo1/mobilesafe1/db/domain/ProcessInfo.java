package com.bolo1.mobilesafe1.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by 菠萝 on 2017/9/4.
 */

/**
 * 获取名称     包名 图标 使用空间  是否为系统软件 是否选中
 * public String name;
 * public Drawable icon;
 * public String packageName;
 * public long memSize;
 * public boolean isSystem;
 * public boolean isCheck;
 */
public class ProcessInfo {

    public String name;
    public Drawable icon;
    public String packageName;
    public long memSize;
    public boolean isSystem;
    public boolean isCheck;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
