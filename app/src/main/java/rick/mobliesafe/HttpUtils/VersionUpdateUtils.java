package rick.mobliesafe.HttpUtils;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;
import rick.mobliesafe.HomeActivity;
import rick.mobliesafe.R;
import rick.mobliesafe.VersionEntity;

import static android.content.Context.DOWNLOAD_SERVICE;


public class VersionUpdateUtils {
    private static final int MESSAGE_NET_EEOR=101;
    private static final int MESSAGE_IO_EEOR=102;
    private static final int MESSAGE_JSON_EEOR=103;
    private static final int MESSAGE_SHOEW_DIALOG=104;
    protected static final int MESSAGE_ENTERHOME=105;
    private static final String BASE_URL="http://192.168.1.44:8080/updateinfo.json";

    //用于更新UI
    private Handler handler=new Handler() {
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context, "IO异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context, "解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent =new Intent(context,HomeActivity.class);
                    context.startActivity(intent);
                    break;
            }
        };
    };
    //本地版本号
    private String mVersion;
    private AppCompatActivity context;
    private ProgressDialog mProgressDialog;
    private VersionEntity versionEntity;
    public VersionUpdateUtils(String Version,AppCompatActivity activity){
        this.mVersion = Version;
        this.context =activity;
    }

    //获取服务器版本号
    public void getCloudVersion(){
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn= (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode()==200){
                InputStream is = conn.getInputStream();//获取输入流
                byte[] data = readStream(is);//输入流转换为字符串组
                String json = new String(data,"UTF-8");//把字符串组转换为字符串
                System.out.println(json);
                Log.i("MOBILESAFE",json);
                JSONObject jsonObject = new JSONObject(json);
                String code = jsonObject.getString("code");
                String des = jsonObject.getString("des");
                String apkurl = jsonObject.getString("apkurl");
                versionEntity =new VersionEntity(code,des,apkurl);
                if (!mVersion.equals(versionEntity.getVersioncode())){
                    //版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                }
            }
        }catch (IOException e){
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        }catch (JSONException e){
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }catch (Exception e){
            handler.sendEmptyMessage(MESSAGE_ENTERHOME);
            e.printStackTrace();
        }

    }
    /*弹出更新提示对话框*/
    private void showUpdateDialog(final VersionEntity versionEntity){
        //创建dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本:"+versionEntity.getVersioncode());//设置标题
        builder.setMessage(versionEntity.getDescription());//根据服务器返回描述，设置升级描述信息
        builder.setCancelable(false);//设置不能点击手机返回按钮隐藏对话框
        builder.setIcon(R.drawable.ic_launcher);//设置对话框图标
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                intiProgressDialog();
                downloadNewApk(versionEntity.getApkurl());
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builder.show();
    }
    //初始化进度条对话框
    private void intiProgressDialog(){
        mProgressDialog =new ProgressDialog(context);
        mProgressDialog.setMessage("准备下载");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();

    }
    /*下载新版本*/
    protected void downloadNewApk(String apkurl){
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.apk", new DownLoadUtils.MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }

            @Override
            public void onLoading(long total, long current, boolean isUpdating) {
                mProgressDialog.setMax((int)total);
                mProgressDialog.setMessage("正在下载.......");
                mProgressDialog.setProgress((int)current);
            }
        });
    }

    private void enterHome(){
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);
    }
    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }
}
