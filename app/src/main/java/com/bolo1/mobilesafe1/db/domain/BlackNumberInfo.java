package com.bolo1.mobilesafe1.db.domain;

/**
 * Created by 菠萝 on 2017/8/21.
 */

public class BlackNumberInfo {
    public String phone;
    public String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" + "phone='" + phone + '\'' + ", mode='" + mode + '\'' + '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
