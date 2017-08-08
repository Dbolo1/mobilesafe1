package com.bolo1.mobilesafe1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;

/**
 * Created by 菠萝 on 2017/8/7.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String phone=Sputils.getString(context, ConstantValue.CONTACT_PHONE,"");
        TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELECOM_SERVICE);
       String simNumber= tm.getSimSerialNumber();
        if(!phone.equals(simNumber)){
            SmsManager sms=SmsManager.getDefault();
            String phone1=Sputils.getString(context, ConstantValue.CONTACT_PHONE,"");
            sms.sendTextMessage(phone1,null,"sms change!!!",null,null);
        }
    }
}
