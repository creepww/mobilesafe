package rick.mobliesafe.blackcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class InterceptSmsReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mSP=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean BlackNumStatus=mSP.getBoolean("BlackNumStatus",true);
        if (BlackNumStatus==false){
            //黑名单关闭
            return;
        }
        //如果是黑名单则终止广播
        BlackNumberDao dao =new BlackNumberDao(context);
        Bundle bundle= intent.getExtras();
        Object []pdus=(Object[])bundle.get("pdus");
        String format=intent.getStringExtra("format");
        SmsMessage[] messages=new SmsMessage[pdus.length];
        for (int i=0;i<messages.length;i++) {
            byte[] sms = (byte[]) pdus[i];
            messages[i] =SmsMessage.createFromPdu(sms,format);
        }
        String sender=messages[0].getOriginatingAddress();
        if (sender.startsWith("+86")){
                sender=sender.substring(3,sender.length());
        }
        Log.i("texting","sender="+sender);
            int mode=dao.getBlackContactMode(sender);
            if (mode==2||mode==3){
                //拦截短信
                abortBroadcast();
            }
        }
    }

