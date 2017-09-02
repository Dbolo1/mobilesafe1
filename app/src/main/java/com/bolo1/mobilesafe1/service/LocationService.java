package com.bolo1.mobilesafe1.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.bolo1.mobilesafe1.activity.HomeActivity;
import com.bolo1.mobilesafe1.activity.TestActivity;

public class LocationService extends Service {
    private Activity mTestActivity;
    private LocationManager lm;
    private MyLocationListener listener;
    private static final String tag ="LocationService";

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        listener = new MyLocationListener();
        Log.d("LocationService","开启服务==============");


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED
//                ) {
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
//        }
//        ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
                Log.d("LocationService","没有获取权限===========================");
            // TODO: Consider calling

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, listener) ;


    }
    class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            //位置发生改变
                double longitude=location.getLongitude();//获取当前经度
                double latitude=location.getLatitude();//获取当前纬度
//            String accuracy = "精确度:" + location.getAccuracy();
//            String altitude = "海拔:" + location.getAltitude();
                Log.d("LocationService","获取到了经度==="+longitude+"获取到了纬度"+latitude);
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage("5554",null,"经度=="+longitude+"纬度=="+latitude,null,null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(tag,"位置提供者的状态发生改变");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(tag,"位置提供者可用");
        }

        @Override
        public void onProviderDisabled(String provider) {
                Log.d(tag,"位置提供者不可用");
        }
    }
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);
        listener=null;
    }
}
