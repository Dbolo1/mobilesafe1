package com.bolo1.mobilesafe1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolo1.mobilesafe1.R;
import com.bolo1.mobilesafe1.db.domain.AppInfo;
import com.bolo1.mobilesafe1.db.domain.ProcessInfo;
import com.bolo1.mobilesafe1.engine.AppInfoProvider;
import com.bolo1.mobilesafe1.engine.ProcessInfoProvider;
import com.bolo1.mobilesafe1.utils.ConstantValue;
import com.bolo1.mobilesafe1.utils.Sputils;
import com.bolo1.mobilesafe1.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菠萝 on 2017/9/4.
 */

public class ProcessManage extends AppCompatActivity implements View.OnClickListener{


    private TextView tv_process_count;
    private int mProcessCount;
    private TextView tv_memory_space;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private List<ProcessInfo> processInfoList;
    private ProcessAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if ( adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new ProcessAdapter();
                lv_process.setAdapter(adapter);
                if(tv_process_des!=null && mCustomerList != null ){
                    tv_process_des.setText("手机应用个数(" + mCustomerList.size() + ")");
                }
            }
        }
    };
    private ListView lv_process;
    private TextView tv_process_des;
    private ProcessInfo processInfo;
    private long availSpace;
    private String strtotalSpace;


    private class ProcessAdapter extends BaseAdapter {


        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //展示纯文本
                return 0;
            } else {
                //展示文本+图片
                return 1;
            }
        }

        @Override
        public int getCount() {
            boolean show_system=Sputils.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM,false);
            if (show_system){
                return mCustomerList.size()+1;
            }else {
                return mCustomerList.size() + mSystemList.size() + 2;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 && position == mCustomerList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                //展示纯文本
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    //手机应用
                    holder.tv_title.setText("手机进程个数(" + mCustomerList.size() + ")");
                } else {
                    //系统应用
                    holder.tv_title.setText("系统进程个数(" + mSystemList.size() + ")");
                }
                return convertView;

            } else {
                //展示文本+图片
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_process_item, null);
                    holder = new ViewHolder();

                    holder.icon = (ImageView) convertView.findViewById(R.id.im_app_icon);
                    holder.packagename = (TextView) convertView.findViewById(R.id.tv_app_packager_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_process = (CheckBox) convertView.findViewById(R.id.cb_process);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.icon.setBackgroundDrawable(getItem(position).icon);
                holder.packagename.setText((getItem(position).name));
                String strSize=Formatter.formatFileSize(getApplicationContext(),getItem(position).memSize);
                holder.tv_memory_info.setText("占用内存:"+strSize);
                if(getItem(position).packageName.equals(getPackageName())){
                    holder.cb_process.setVisibility(View.GONE);
                }else{
                    holder.cb_process.setVisibility(View.VISIBLE);
                }
                holder.cb_process.setChecked(getItem(position).isCheck);
                return convertView;
            }

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity);
        initUI();
        initTitle();
        initListData();
    }

    private void initListData() {
        getData();
        lv_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //系统应用个数
                        tv_process_des.setText("系统应用个数(" + mSystemList.size() + ")");
                    } else {
                        //手机应用个数
                        tv_process_des.setText("手机应用个数(" + mCustomerList.size() + ")");
                    }
                }
            }
        });

        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size() + 1) {
                        processInfo = mCustomerList.get(position - 1);
                    } else {
                        processInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                     if(processInfo!=null){
                         if(!processInfo.packageName.equals(getPackageName())){
                             processInfo.isCheck=!processInfo.isCheck;
                               CheckBox  cb_process= (CheckBox) view.findViewById(R.id.cb_process);
                             cb_process.setChecked(processInfo.isCheck);
                         }
                     }
                }
            }
        });
    }

    private void getData() {
        new Thread() {

            @Override
            public void run() {
                processInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();
                for (ProcessInfo Info : processInfoList) {
                    if (Info.isSystem) {
                        //系统应用
                        mSystemList.add(Info);
                    } else {
                        //手机应用
                        mCustomerList.add(Info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    private void initTitle() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数:" + mProcessCount);

        //获取可用内存并转化格式
        availSpace = ProcessInfoProvider.getAvailSpace(this);
        String stravailSpace = Formatter.formatFileSize(this, availSpace);

        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        strtotalSpace = Formatter.formatFileSize(this, totalSpace);

        tv_memory_space.setText("剩余内存/总内存:" + stravailSpace + "/" + strtotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_space = (TextView) findViewById(R.id.tv_memory_space);
        lv_process = (ListView) findViewById(R.id.lv_process);
        tv_process_des = (TextView) findViewById(R.id.tv_process_des);
        Button bt_select_all = (Button) findViewById(R.id.bt_select_all);
        Button bt_invert_select = (Button) findViewById(R.id.bt_invert_select);
        Button bt_fly_memory_clear = (Button) findViewById(R.id.bt_fly_memory_clear);
        Button bt_process_setting = (Button) findViewById(R.id.bt_process_setting);

        bt_select_all.setOnClickListener(this);
        bt_invert_select.setOnClickListener(this);
        bt_fly_memory_clear.setOnClickListener(this);
        bt_process_setting.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_select_all :
                 SelectedAll();
                break;
            case R.id.bt_invert_select :
                SelectReverse();
                break;
            case R.id.bt_fly_memory_clear :
                clearAll();
                break;
            case R.id.bt_process_setting :
                setting();
                break;
        }
    }

    /**
     * 设置系统隐藏与锁屏清理
     */
    private void setting() {
       Intent intent= new Intent(this,ProcessSettingActivity.class);
       startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 清除内存
     */
    private void clearAll() {

       ArrayList<ProcessInfo> KillsProcessInfo = new ArrayList<ProcessInfo>();
        for (ProcessInfo processInfo : mCustomerList){
            if(processInfo.packageName.equals(getPackageName())){
                continue;
            }
            if(processInfo.isCheck){
                KillsProcessInfo.add(processInfo);
            }
        }

        for (ProcessInfo processInfo : mSystemList){
            if(processInfo.packageName.equals(getPackageName())){
                continue;
            }
            if(processInfo.isCheck){
                KillsProcessInfo.add(processInfo);
            }
        }
        long totlaReleaseSpace =0;
        for (ProcessInfo processInfo :KillsProcessInfo){
                //判断进程在哪个集合
            if(mCustomerList.contains(processInfo)){
                mCustomerList.remove(processInfo);
            }
            if (mSystemList.contains(processInfo)){
                mSystemList.remove(processInfo);
            }

            //更新进程数 与内存空间
            ProcessInfoProvider.KillsProcess(this,processInfo);
            totlaReleaseSpace+=processInfo.memSize;
        }

            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
            mProcessCount-=KillsProcessInfo.size();
            tv_process_count.setText("进程总数:"+Formatter.formatFileSize(this,mProcessCount));
            //剩余空间=释放空间+原有剩余空间
            availSpace+=totlaReleaseSpace;
            tv_memory_space.setText("剩余内存/总内存:" + Formatter.formatFileSize(this,availSpace) + "/" + strtotalSpace);



            ToastUtil.show(this,"杀死了"+mProcessCount+"个进程"+"释放了"+Formatter.formatFileSize(this,totlaReleaseSpace)+"内存");


    }

    /**
     * 反选
     */
    private void SelectReverse() {
        for (ProcessInfo processInfo : mCustomerList){
            if (processInfo.packageName.equals(getPackageName())){
                continue;
            }
            processInfo.isCheck= !processInfo.isCheck;
        }
        for (ProcessInfo processInfo : mSystemList){
            if (processInfo.packageName.equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=!processInfo.isCheck;
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 全选
     */
    private void SelectedAll() {
        for (ProcessInfo processInfo : mCustomerList){
            if (processInfo.packageName.equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=true;
        }
        for (ProcessInfo processInfo : mSystemList){
            if (processInfo.packageName.equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=true;
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView packagename;
        TextView tv_memory_info;
        CheckBox cb_process;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

}
