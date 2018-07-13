package rick.mobliesafe.antitheft;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSLocationService extends Service {
    private LocationManager lm;
    private MyListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyListener();
        //criteria查询条件
        //true只返回可用的位置提供者
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取准确的位置
        criteria.setCostAllowed(true);//允许产生开销
        String name = lm.NETWORK_PROVIDER;
        System.out.println("最好的位置提供者" + name);
        try {
            lm.requestLocationUpdates(name, 0, 0, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    private class MyListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            StringBuilder sb =new StringBuilder();
            sb.append("accuracy:"+location.getAccuracy()+"/n");
            sb.append("speed:"+location.getSpeed()+"/n");
            sb.append("longitude:"+location.getLongitude()+"/n");
            sb.append("latitude:"+location.getLatitude()+"/n");
            String result=sb.toString();
            SharedPreferences sp =getSharedPreferences("config",MODE_PRIVATE);
            String safenumber = sp.getString("safephone","");
            Log.i("Gps","safenumber="+safenumber);
            SmsManager.getDefault().sendTextMessage(safenumber,null,result,null,null);
            stopSelf();
        }
        //当位置提供者状态发生变化时调用的方法
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        //当某个位置提供者可用的时候调用的方法

        @Override
        public void onProviderEnabled(String s) {

        }
        //当某个位置提供者不可用的时候调用的方法

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);
        listener=null;
    }
}
