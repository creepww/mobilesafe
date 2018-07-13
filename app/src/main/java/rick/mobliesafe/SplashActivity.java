package rick.mobliesafe;

import android.Manifest;
import android.app.Activity;

import android.drm.DrmStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import rick.mobliesafe.HttpUtils.MyUtils;
import rick.mobliesafe.HttpUtils.VersionUpdateUtils;

public class SplashActivity extends AppCompatActivity{
    /*应用版本号*/
    private TextView mVersionTV;
    //本地版本号
    private String mVersion;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置该Activity没有标题栏，在加载布局之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initView();
    final VersionUpdateUtils updateUtils = new VersionUpdateUtils(mVersion, SplashActivity.this);
        new Thread() {
            public void run() {
                //获取服务器版本号
                updateUtils.getCloudVersion();
            }
        }.start();
    }

    //初始化控件
    private void initView() {
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mVersionTV.setText("版本号" + mVersion);
    }


}
