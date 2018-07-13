package rick.mobliesafe.HttpUtils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;

import java.io.File;

import rick.mobliesafe.BuildConfig;

public class MyUtils {
    /**
     * 获取版本号
     *
     * @param context
     * @return 返回版本号
     */
    public static String getVersion(Context context) {
        //PackgeManager可以获取清单文件中所有信息
        PackageManager manager = context.getPackageManager();
        try {
            //getPackgeName()获取当前程序的包名
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /*
    安装新版本
    @param activity
     */
    public static void installApk(AppCompatActivity activity){
        Intent intent=new Intent("android.intent.action.VIEW");
        //添加默认分类
        intent.addCategory("android.intent.category.DEFAULT");
        //设置数据和类型，在文件中
        intent.setDataAndType(
                Uri.fromFile(new File("/mnt/sdcard/mobilesafe2.apk")),
                "application/vnd.android.package-archive"
        );
        //如果开启的activity退出的时候，会回调当前activity的onActivityResult
        activity.startActivityForResult(intent,0);

    }

}
