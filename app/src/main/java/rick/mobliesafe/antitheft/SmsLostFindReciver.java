package rick.mobliesafe.antitheft;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsLostFindReciver extends BroadcastReceiver {
    private static final String TAG=SmsLostFindReciver.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        boolean protecting = sharedPreferences.getBoolean("protecting",true);
        if (protecting){//防盗保护开启
            //获取超级管理员
            DevicePolicyManager dpm =(DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
           /* Object[] objs = (Object[])intent.getExtras().get("pdus");
            for (Object obj:objs){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
                String sender = smsMessage.getOriginatingAddress();
                String body=smsMessage.getMessageBody();*/
           Bundle bundle= intent.getExtras();
           Object []pdus=(Object[])bundle.get("pdus");
           String format=intent.getStringExtra("format");
           SmsMessage[] messages=new SmsMessage[pdus.length];
           for (int i=0;i<messages.length;i++) {
               byte[] sms = (byte[]) pdus[i];
               messages[i] =SmsMessage.createFromPdu(sms,format);
           }
           String sender=messages[0].getOriginatingAddress();
           String body  =messages[0].getMessageBody();
            String safephone=sharedPreferences.getString("safephone",null).trim().replace(" ", "");
            Log.i(TAG,"safephone="+safephone);
            Log.i(TAG,"sender="+sender);
            Log.i(TAG,"body="+body);
                //如果该短信是安全号码发送的
                if (!TextUtils.isEmpty(safephone)&sender.equals(safephone)){
                    if ("*location*".equals(body)){
                        Log.i(TAG,"返回位置信息");
                        //获取位置放在服务实现
                        Intent service= new Intent(context,GPSLocationService.class);
                        context.startService(service);
                        abortBroadcast();
                    }else if ("#*alarm*#".equals(body)) {
                        Log.i(TAG, "播放报警音乐");
                       /* MediaPlayer player = MediaPlayer.create(context, android.R.raw.ylzs);
                        player.setVolume(1.0f, 1.0f);
                        player.start();*/
                        abortBroadcast();
                    }else if ("#*wipedata*#".equals(body)){
                        /*Log.i(TAG,"远程清除数据");
                        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);*/
                        abortBroadcast();

                    }else if ("*lockscreen*".equals(body)){
                        Log.i(TAG,"远程锁屏");
                        dpm.resetPassword("123",0);
                        dpm.lockNow();
                        abortBroadcast();
                    }

                }
            }

        }
    }


