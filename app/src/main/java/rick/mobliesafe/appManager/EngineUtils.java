package rick.mobliesafe.appManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class EngineUtils {
    //分享应用
    public static void ShareApplication(Context context,Appinfo appInfo){
        Intent intent= new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"推荐您使用一款app,名称叫："+
        appInfo.appName+"下载路径: https://play.google.com/store/apps/details?id="+
        appInfo.packgeName);
        context.startActivity(intent);
    }
    //开启应用程序
    public static void startApplication(Context context,Appinfo appInfo){
        //打开这个应用程序的入口activity
        PackageManager  pm=context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packgeName);
        if (intent!=null){
            context.startActivity(intent);
        }else {
            Toast.makeText(context, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }
    public static void SettingAppDetail(Context context,Appinfo appInfo){
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+appInfo.packgeName));
        context.startActivity(intent);
    }
    public static void uninstallApplication(Context context,Appinfo appInfo){
        if (appInfo.isUserApp){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:"+appInfo.packgeName));
            context.startActivity(intent);
        }else {
            //需要root才能卸载
            Toast.makeText(context, "需要root才能卸载应用", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
