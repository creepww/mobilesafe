package rick.mobliesafe.antitheft;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import rick.mobliesafe.R;

public class SetUp2Activity extends  BaseSetUpActivity implements View.OnClickListener{
    private TelephonyManager mTelephoneManager;
    private Button mBindSIMBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
            mTelephoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            initView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SetUp2Activity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SetUp2Activity.this, new String[]{
                        Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                bindSIM();
            }
        }
    }
    private void initView(){
        //设置第二个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_second)).setChecked(true);
        mBindSIMBtn = (Button)findViewById(R.id.btn_bind_sim);
        mBindSIMBtn.setOnClickListener(this);
        if (isBind()){
            mBindSIMBtn.setEnabled(true);
        }else {
            mBindSIMBtn.setEnabled(false);
        }
    }
    private boolean isBind(){
        String simString=sp.getString("sim",null);
        if (TextUtils.isEmpty(simString)){
            return false;
        }
        return true;
    }

    @Override
    public void showNext() {
        if (!isBind()){
            Toast.makeText(this,"您还没有绑定SIM卡",0).show();
            return;
        }
        startActivityAndFinishSelf(SetUp3Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(SetUp1Activity.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R. id.btn_bind_sim:
                //绑定SIM卡
                bindSIM();
                break;
        }
    }
    private void bindSIM(){
        if (!isBind()){
            try {
                //需要权限的操作
                String simSerialNumber = mTelephoneManager.getSimSerialNumber();
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("sim",simSerialNumber);
                edit.commit();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "SIM卡绑定成功", Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);
        }else {
            //已经绑定，提醒用户
            Toast.makeText(this, "SIM卡已经绑定", Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);
        }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:
               if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   bindSIM();
               }else{
                   Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                   finish();
               }
               break;
           default:
       }
    }
}
