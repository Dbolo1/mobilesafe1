package com.bolo1.mobilesafe1.db.domain;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by 菠萝 on 2017/9/2.
 */

public class AppInfo {
    public   String PackageName;
    public  String name;
    public Drawable Icon;
    public  boolean isSystem;
    public  boolean isSdCard;

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return Icon;
    }

    public void setIcon(Drawable icon) {
        Icon = icon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSdCard() {
        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }
}
