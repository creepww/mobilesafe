package rick.mobliesafe.appManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import android.text.format.Formatter;
import java.util.List;
import android.os.Handler;

import rick.mobliesafe.R;


public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView mPhoneMemoryTV;
    private TextView mSDMemoryTV;
    private ListView mListView;
    private List<Appinfo> appinfos;
    private List<Appinfo> userAppinfos=new ArrayList<Appinfo>();
    private List<Appinfo> systemAppInfos=new ArrayList<Appinfo>();
    private AppManagerAdapter adapter;
    private UninstallReceiver receiver;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 10:
                    if (adapter==null){
                        adapter=new AppManagerAdapter(userAppinfos,systemAppInfos,AppManagerActivity.this);
                    }
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case 15:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private TextView mAppNumTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_manager);
        //注册广播
        receiver=new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver,intentFilter);
        initView();
    }
    private void initView(){
        ImageView mLeftImgv=(ImageView)findViewById(R.id.imgv_leftbtn);
        ((TextView)findViewById(R.id.tv_title)).setText("软件管家");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        mPhoneMemoryTV=(TextView)findViewById(R.id.tv_phonememory_appmanager);
        mSDMemoryTV=(TextView)findViewById(R.id.tv_sdmemory_appmanager);
        mAppNumTV=(TextView)findViewById(R.id.tv_appnumber);
        mListView=(ListView)findViewById(R.id.lv_appmanager);
        //取得手机剩余内存和sd卡剩余内存
        getMemoryFromPhone();
        initData();
        initListener();
    }
    private void initListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                if (adapter!=null){
                    new Thread(){
                        public void run(){
                            Appinfo mappInfo=(Appinfo) adapter.getItem(position);
                            //记住当前条目的状态
                            boolean flag=mappInfo.isSelected;
                            //先将集合中所有条目的AppInfo变为未选中状态
                            for (Appinfo appinfo:userAppinfos){
                                appinfo.isSelected=false;
                            }
                            for (Appinfo appinfo:systemAppInfos){
                                appinfo.isSelected=false;
                            }
                            if (mappInfo!=null){
                                //如果已经选中则变为未选中
                                if (flag){
                                    mappInfo.isSelected=false;
                                }else {
                                    mappInfo.isSelected=true;
                                }
                                mHandler.sendEmptyMessage(15);
                            }
                        };
                    }.start();
                }

            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem>=userAppinfos.size()+1){
                    mAppNumTV.setText("系统程序"+systemAppInfos.size()+"个");
                }else {
                    mAppNumTV.setText("用户程序"+userAppinfos.size()+"个");
                }
            }
        });
    }
    private void initData(){
        appinfos=new ArrayList<Appinfo>();
        new Thread(){
            public void run(){
                appinfos.clear();
                userAppinfos.clear();
                systemAppInfos.clear();
                appinfos.addAll(AppInfoParser.getAppInfos(AppManagerActivity.this));
                for (Appinfo appinfo:appinfos){
                    //如果是用户app
                    if (appinfo.isUserApp){
                        userAppinfos.add(appinfo);
                    }else {
                        systemAppInfos.add(appinfo);
                    }
                }
                mHandler.sendEmptyMessage(10);
            };
        }.start();
    }
    private void getMemoryFromPhone(){
        long avail_sd= Environment.getExternalStorageDirectory().getFreeSpace();
        long avail_rom=Environment.getDataDirectory().getFreeSpace();
        String str_avail_sd= Formatter.formatFileSize(this,avail_sd);
        String str_avail_rom=Formatter.formatFileSize(this,avail_rom);
        mPhoneMemoryTV.setText("剩余手机内存"+str_avail_rom);
        mSDMemoryTV.setText("剩余SD卡内存"+str_avail_sd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgv_leftbtn:
                finish();
                break;
        }
    }
    class UninstallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        receiver=null;
        super.onDestroy();
    }
}
