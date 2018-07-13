package rick.mobliesafe.antitheft;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


import rick.mobliesafe.R;

public class ContactSelectActivity extends Activity implements View.OnClickListener {
    private ListView mListView;
    private ContactAdapter adapter;
    private List<ContactInfo>systemContacts;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case 10:
                    if (systemContacts!=null){
                        adapter = new ContactAdapter(systemContacts,ContactSelectActivity.this);
                        mListView.setAdapter(adapter);
                    }
                break;
            }
        };
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_select);
        initView();

    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText("选择联系人");
        ImageView mLeftImgv=(ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
       mListView=(ListView)findViewById(R.id.lv_contact);
       new Thread(){
           public void run(){
               systemContacts=ContactInfoParser.getSystemContact(ContactSelectActivity.this);
               systemContacts.addAll(ContactInfoParser.getSystemContact(ContactSelectActivity.this));
               mHandler.sendEmptyMessage(10);
           }
       }.start();
       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ContactInfo item=(ContactInfo) adapter.getItem(position);
               Intent intent = new Intent();
               intent.putExtra("phone",item.phone);
               intent.putExtra("name",item.name);
               setResult(0,intent);
               finish();
           }
       });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgv_leftbtn:
                finish();
                break;
        }
    }


}
