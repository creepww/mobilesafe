package rick.mobliesafe.antitheft;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        correctSIM();

    }
    public void correctSIM(){
        //检查SIM卡是否发生变化
        SharedPreferences sp =getSharedPreferences("config", Context.MODE_PRIVATE);
        //获取防盗保护的状态
        boolean protecting = sp.getBoolean("protecting",true);
        if (protecting){
            //得到绑定sim卡串号
            String bindsim = sp.getString("sim","");
            //得到现在的sim卡串号
            TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //为了测试在手机序列号上的data，已模拟sim卡被更换的情况
            try {
                String realsim = tm.getSimSerialNumber();
                if (bindsim.equals(realsim)) {
                    Log.i("", "sim卡未发生变化");
                } else {
                    Log.i("", "sim卡发生了变化");
                    String safenumber = sp.getString("safephone", "");
                    if (!TextUtils.isEmpty(safenumber)) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(safenumber, null, "你的亲友手机的sim卡已经被更换", null, null);
                    }
                }
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
    }
}
