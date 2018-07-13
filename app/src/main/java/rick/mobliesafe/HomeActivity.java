package rick.mobliesafe;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.InputStream;
import java.security.Key;

import io.reactivex.functions.Consumer;
import rick.mobliesafe.antitheft.InterPasswordDialog;
import rick.mobliesafe.antitheft.LostFindActivity;
import rick.mobliesafe.antitheft.MD5Utils;
import rick.mobliesafe.antitheft.MyDeviceAdminReciever;
import rick.mobliesafe.antitheft.SetUpPasswordDialog;
import rick.mobliesafe.appManager.AppManagerActivity;
import rick.mobliesafe.blackcontact.SecurityPhoneActivity;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    //存储手机防盗密码的sp
    private SharedPreferences msharedPreferences;
    //设备管理员
    private DevicePolicyManager policyManager;
    //申请权限
    private ComponentName componentName;
    private long mExitTime;
    private static final String TAG = "RxPermissionTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        requestPermissions();
        msharedPreferences =getSharedPreferences("config",MODE_PRIVATE);
        //初始化GridView
        gv_home=(GridView)findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://手机防盗
                        if (isSetUpPassword()){
                            //弹出输入密码对话框
                           showInterPswdDialog();
                       }else {
                           showSetUpPswdDialog();
                       }
                       break;
                    case 1://通讯卫士
                        startActivity(SecurityPhoneActivity.class);
                        break;
                    case 2://软件管家
                        startActivity(AppManagerActivity.class);
                        break;
                    case 3://病毒查杀
                        //startActivity(VirusScanActivity.class);
                        break;
                    case 4://缓存清理
                        //startActivity(CacheClearListActivity.class);
                        break;
                    case 5://进程管理
                        //startActivity(ProcessManagerActivity.class);
                        break;
                    case 6://流量统计
                        //startActivity(TrafficMonitoringActivity.class);
                        break;
                    case 7://高级工具
                        //startActivity(AppManagerActivity.class);
                        break;
                    case 8://设置中心
                        //startActivity(SettingActivity.class);
                        break;
                }
            }
        });

        //1.获取设备管理员
        policyManager=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        //2.申请权限，MyDeviceAdminReceiver继承自DeviceAdminReceiver
        componentName =new ComponentName(this, MyDeviceAdminReciever.class);
        //3.判断，如果没有权限申请权限
        boolean active = policyManager.isAdminActive(componentName);
        if (!active){
            //如果没有权限则获取
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"获取超级管理员权限");
            startActivity(intent);
        }
        final String myPackageName = getPackageName();
        if
                (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName))
        {
            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
            Log.d("liueg", "11111111111");
        }
    }
    //弹出设置密码对话框
    private void showSetUpPswdDialog(){
        final SetUpPasswordDialog setUpPasswordDialog= new SetUpPasswordDialog(HomeActivity.this);
        setUpPasswordDialog.setCallBack(new rick.mobliesafe.antitheft.SetUpPasswordDialog.MyCallBack(){
            @Override
            public void ok() {
                String firstPwsd =setUpPasswordDialog.mFirstPWDET.getText().toString().trim();
                String affirmPwsd = setUpPasswordDialog.mAffirmET.getText().toString().trim();
                if (!TextUtils.isEmpty(firstPwsd)&&!TextUtils.isEmpty(affirmPwsd)){
                    if (firstPwsd.equals(affirmPwsd)) {
                        //两次密码一致,存储
                        savePswd(affirmPwsd);
                        setUpPasswordDialog.dismiss();
                        //显示输入密码对话框
                        showInterPswdDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次输入不一致", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                setUpPasswordDialog.dismiss();
            }
        });
        setUpPasswordDialog.setCancelable(true);
        setUpPasswordDialog.show();
    }
    //弹出输入密码对话框
    private void showInterPswdDialog(){
        final String password = getPassword();
        final InterPasswordDialog mInPswdDialog= new InterPasswordDialog(HomeActivity.this);
        mInPswdDialog.setCallBack(new InterPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                if (TextUtils.isEmpty(mInPswdDialog.getPassword())){
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(password.equals(MD5Utils.encode(mInPswdDialog.getPassword()))){
                    //进入防盗主页面
                    mInPswdDialog.dismiss();
                    startActivity(LostFindActivity.class);
                }else {
                    //对话框消失,弹出toast
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void cancel() {
            mInPswdDialog.dismiss();
            }
        });
        mInPswdDialog.setCancelable(true);
        //显示对话框
        mInPswdDialog.show();
    }
    //保存密码
    private void savePswd(String affirmPswd){
        SharedPreferences.Editor edit = msharedPreferences.edit();
        edit.putString("PhoneAntiTheftPWD",MD5Utils.encode(affirmPswd));
        edit.commit();
    }
    /*
    * 获取密码
    * @return sp存储的密码
    */
    private String getPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return "";
        }
        return password;
    }
    //判断用户是否设置过手机防盗密码
    private boolean isSetUpPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }
    /*开启新的Activity不关闭自己*/
    public void startActivity(Class<?> cls){
        Intent intent = new Intent(HomeActivity.this,cls);
        startActivity(intent);
    }
    /*按两次返回键退出程序*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis()-mExitTime)<2000){
                System.exit(0);
            }else{
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime=System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(HomeActivity.this);
        rxPermission
                .requestEach(
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                        )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.d(TAG, permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(TAG, permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d(TAG, permission.name + " is denied.");
                        }
                    }
                });


    }
}
