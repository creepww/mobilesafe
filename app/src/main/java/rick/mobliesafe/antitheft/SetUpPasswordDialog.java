package rick.mobliesafe.antitheft;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import rick.mobliesafe.R;

public class SetUpPasswordDialog extends Dialog implements android.view.View.OnClickListener {
    private TextView mTitleTV;
    public EditText mFirstPWDET;
    public EditText mAffirmET;
    private MyCallBack myCallBack;
    public  SetUpPasswordDialog(Context context){
        super(context, R.style.dialog_custom);
    }
    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack =  myCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.setup_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        mTitleTV=(TextView)findViewById(R.id.tv_setuppwd_title);
        mFirstPWDET=(EditText)findViewById(R.id.et_firstpwd);
        mAffirmET=(EditText)findViewById(R.id.et_affrim_password);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }
    public void setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                myCallBack.ok();
                break;
            case R.id.btn_cancel:
                myCallBack.cancel();
                break;
        }
    }
    public interface MyCallBack{
        void ok();
        void cancel();
    }
}
