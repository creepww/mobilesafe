package rick.mobliesafe.antitheft;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rick.mobliesafe.R;

public class InterPasswordDialog extends Dialog implements android.view.View.OnClickListener {
    private TextView mTitleTV;
    private EditText mInterPWDET;
    private MyCallBack myCallBack;
    public InterPasswordDialog(Context context){
        super(context, R.style.dialog_custom);
    }
    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.inter_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }
    public void initView(){
        mTitleTV = (TextView) findViewById(R.id.tv_interpwd_title);
        mInterPWDET  = (EditText) findViewById(R.id.et_inter_password);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_dismiss).setOnClickListener(this);

    }
    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }

        }
    public String getPassword(){
        return mInterPWDET.getText().toString();
    }

    @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_confirm:
                    myCallBack.confirm();
                    break;
                case R.id.btn_dismiss:
                    myCallBack.cancel();
                    break;
            }

        }
    public interface MyCallBack{
        void confirm();
        void cancel();
    }
}


