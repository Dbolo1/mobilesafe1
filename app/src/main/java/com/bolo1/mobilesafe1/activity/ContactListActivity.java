package com.bolo1.mobilesafe1.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 菠萝 on 2017/7/28.
 */

public class ContactListActivity extends AppCompatActivity {

    private final static String tag = "ContactListActivity";
    List<HashMap<String, String>> contactsList = new ArrayList<>();
    private ListView lv_contact;
    private String number;
    private final static String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            madapter = new MyAdapter();
            lv_contact.setAdapter(madapter);
        }
    };
    private SimpleAdapter simpleAdapter;
    private MyAdapter madapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist_activity);
        initUI();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.
                READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        } else {
            //  initData();
            readContacts();
        }
    }

    /**
     * 自定义适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactsList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
            tv_contact_name.setText(getItem(position).get("name"));
            tv_contact_phone.setText(getItem(position).get("phone"));

            return view;
        }
    }

    private void readContacts() {
        Cursor cursor = null;
        try {
            new Thread() {
                @Override
                public void run() {
                    Cursor cursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String displayName = cursor.getString(0);
                            String number = cursor.getString(1);
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            if(!TextUtils.isEmpty(displayName)&&!TextUtils.isEmpty(number)){
                                hashMap.put("name", displayName);
                                hashMap.put("phone", number);
                                contactsList.add(hashMap);
                            }else {
                                continue;
                            }
                            /**  //获取手机联系人姓名
                             String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                             .Phone.DISPLAY_NAME));
                             hashMap.put("name", displayName);
                             //获取手机联系人机号
                             number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                             .Phone.NUMBER));
                             hashMap.put("phone", number);
                             contactsList.add(hashMap);
                             **/
                        }
                        Log.d(tag,contactsList.toString());
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    ToastUtil.show(this, "你拒绝了权限");
                }
                break;
            default:
        }
    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (madapter != null) {
                    HashMap<String, String> hashMap = madapter.getItem(position);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);
                    finish();
                }
            }
        });
    }
}
