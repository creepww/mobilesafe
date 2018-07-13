package rick.mobliesafe.appManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.text.format.Formatter;
import java.util.List;

import rick.mobliesafe.R;

public class AppManagerAdapter extends BaseAdapter {
    private List<Appinfo> UserAppInfos;
    private List<Appinfo> SystemAppInfos;
    private Context context;
    public AppManagerAdapter(List<Appinfo> userAppInfos,List<Appinfo> systemAppInfos,Context context){
        super();
        UserAppInfos=userAppInfos;
        SystemAppInfos=systemAppInfos;
        this.context=context;
    }

    @Override
    public int getCount() {
        //因为有两个条目需要显示用户进程，因此系统进程需要加2
        return UserAppInfos.size()+SystemAppInfos.size()+2;
    }

    @Override
    public Object getItem(int position) {
        if (position==0){
            //第0个位置显示的应该是用户程序个数的标签
            return null;
        }else if (position==(UserAppInfos.size()+1)){
            return null;
        }
        Appinfo appInfo;
        if (position<(UserAppInfos.size()+1)){
            //用户程序
            appInfo=UserAppInfos.get(position-1);
            //多了一个textview标签,位置-1
        }else {
            //系统程序
            int location=position-UserAppInfos.size()-2;
            appInfo=SystemAppInfos.get(location);
        }
        return appInfo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //如果position为0则为TextView
        if (position ==0){
            TextView tv=getTextView();
            tv.setText("用户程序:"+UserAppInfos.size()+"个");
            return tv;
        }else if (position==(UserAppInfos.size()+1)){
            TextView tv=getTextView();
            tv.setText("系统程序:"+SystemAppInfos.size()+"个");
            return tv;
        }
        //获取当前App的对象
        Appinfo appInfo;
        if (position<(UserAppInfos.size()+1)){
            //position为textView
            appInfo=UserAppInfos.get(position-1);
        }else {
            appInfo=SystemAppInfos.get(position-UserAppInfos.size()-2);
        }
        ViewHolder viewHolder= null;
        if (convertView!=null& convertView instanceof LinearLayout){
            viewHolder=(ViewHolder) convertView.getTag();
        }else {
            viewHolder= new ViewHolder();
            convertView=View.inflate(context, R.layout.item_appmanager_list,null);
            viewHolder.mAppIconImgv=(ImageView)convertView.findViewById(R.id.imgv_appicon);
            viewHolder.mAppLocationTV=(TextView)convertView.findViewById(R.id.tv_appisroom);
            viewHolder.mAppSizeTV=(TextView)convertView.findViewById(R.id.tv_appsize);
            viewHolder.mAppNameTV=(TextView) convertView.findViewById(R.id.tv_appname);
            viewHolder.mLaunchAppTV=(TextView)convertView.findViewById(R.id.tv_launch_app);
            viewHolder.mSettingAppTV=(TextView)convertView.findViewById(R.id.tv_setting_app);
            viewHolder.mShareAppTV=(TextView)convertView.findViewById(R.id.tv_share_app);
            viewHolder.mUninstallTV=(TextView)convertView.findViewById(R.id.tv_uninstall_app);
            viewHolder.mAppOptionLL=(LinearLayout)convertView.findViewById(R.id.ll_option_app);
            convertView.setTag(viewHolder);
        }
        if (appInfo!=null){
            viewHolder.mAppLocationTV.setText(appInfo.getAppLocation(appInfo.isInRoom));
            viewHolder.mAppIconImgv.setImageDrawable(appInfo.icon);
            viewHolder.mAppSizeTV.setText(Formatter.formatFileSize(context,appInfo.appSize));
            viewHolder.mAppNameTV.setText(appInfo.appName);
            if(appInfo.isSelected){
                viewHolder.mAppOptionLL.setVisibility(View.VISIBLE);
            }else {
                viewHolder.mAppOptionLL.setVisibility(View.GONE);
            }
        }
        MyClickListener listener= new MyClickListener(appInfo);
        viewHolder.mLaunchAppTV.setOnClickListener(listener);
        viewHolder.mSettingAppTV.setOnClickListener(listener);
        viewHolder.mShareAppTV.setOnClickListener(listener);
        viewHolder.mUninstallTV.setOnClickListener(listener);
        return convertView;
    }



    /*创建一个TextView*/
    private TextView getTextView(){
        TextView tv=new TextView(context);
        tv.setBackgroundColor(context.getResources().getColor(R.color.light_gary));
        tv.setPadding(DensityUtil.dip2px(context,5),
         DensityUtil.dip2px(context,5),DensityUtil.dip2px(context,5),DensityUtil.dip2px(context,5));
        tv.setTextColor(context.getResources().getColor(R.color.black30));
        return tv;
    }
   static class ViewHolder{
        //启动app
       TextView mLaunchAppTV;
       //卸载app
       TextView mUninstallTV;
       //分享app
       TextView mShareAppTV;
       //设置app
       TextView mSettingAppTV;
       //app图标
       ImageView mAppIconImgv;
       //app位置
       TextView mAppLocationTV;
       //app大小
       TextView mAppSizeTV;
       //app名称
       TextView mAppNameTV;
       //操作App的线性布局
       LinearLayout mAppOptionLL;
   }
   class MyClickListener implements View.OnClickListener{
        private Appinfo appinfo;
        public MyClickListener(Appinfo appInfo){
            super();
            this.appinfo=appInfo;
        }

       @Override
       public void onClick(View view) {
           switch (view.getId()){
               case R.id.tv_launch_app:
                   //启动应用
                   EngineUtils.startApplication(context,appinfo);
                   break;
               case R.id.tv_share_app:
                   EngineUtils.ShareApplication(context,appinfo);
                   break;
               case R.id.tv_setting_app:
                   //设置应用
                   EngineUtils.SettingAppDetail(context,appinfo);
                   break;
               case R.id.tv_uninstall_app:
                   //卸载应用需要注册广播接受者
                   if (appinfo.packgeName.equals(context.getPackageName())){
                       Toast.makeText(context, "您没有权限卸载此应用", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   EngineUtils.uninstallApplication(context,appinfo);
                   break;
                }
       }
   }
}
