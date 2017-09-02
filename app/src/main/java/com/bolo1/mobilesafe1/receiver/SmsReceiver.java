package com.bolo1.mobilesafe1.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.activity.TestActivity;
import com.bolo1.mobilesafe1.service.LocationService;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;

public class SmsReceiver extends BroadcastReceiver {
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(context, DeviceAdminSampleReceiver.class);
        boolean open_security = Sputils.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object obs : objects) {
                SmsMessage smg = SmsMessage.createFromPdu((byte[]) obs);
                String address = smg.getOriginatingAddress();
                String body = smg.getMessageBody();
                System.out.print("收到短信内容" + body + "发件人" + address);
                if (body.contains("#*location*#")) {
                    //发送当前的手机经纬度
//                context.startActivity(new Intent(context, TestActivity.class));

                    context.startService(new Intent(context, LocationService.class));
                    abortBroadcast();
                } else if (body.contains("#*alarm*#")) {
                    //打开报警音乐
                    ToastUtil.show(context, "点击播放音乐");
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.dd);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else if (body.contains("#*wipedata*#")) {
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        mDPM.wipeData(0);
                    } else {
                        ToastUtil.show(context, "请先激活管理者");
                    }
                    //数据销毁
                } else if (body.contains("#*lockscreen*#")) {
                    //远程锁屏
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        mDPM.lockNow();
                        mDPM.resetPassword("", 0);
                    } else {
                        ToastUtil.show(context, "请先激活管理者");
                    }
                }
            }
        }
    }
}
