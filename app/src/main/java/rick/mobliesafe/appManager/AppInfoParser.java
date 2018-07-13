package rick.mobliesafe.appManager;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppInfoParser {
    public static List<Appinfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<Appinfo> appinfos= new ArrayList<Appinfo>();
        for (PackageInfo packageInfo:packageInfos){
            Appinfo appinfo = new Appinfo();
            String packname=packageInfo.packageName;
            appinfo.packgeName=packname;
            Drawable icon=packageInfo.applicationInfo.loadIcon(pm);
            appinfo.icon=icon;
            String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
            appinfo.appName=appname;
            String apkpath=packageInfo.applicationInfo.sourceDir;
            appinfo.apkPath=apkpath;
            File file= new File(apkpath);
            long appSize = file.length();
            appinfo.appSize=appSize;
            int flags=  packageInfo.applicationInfo.flags;
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags)!=0){
                //外部存储
                appinfo.isInRoom=false;
            }else {
                //手机内存
                appinfo.isInRoom=true;
            }
            if ((ApplicationInfo.FLAG_SYSTEM & flags)!=0){
                //系统应用
                appinfo.isUserApp=false;
            }else {
                //用户应用
                appinfo.isUserApp=true;
            }
            appinfos.add(appinfo);
            appinfo=null;
            }
            return appinfos;
    }
}
