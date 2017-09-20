package com.safehelper.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.safehelper.R;
import com.safehelper.utils.ToastUtil;
import static com.safehelper.utils.NetWorkUtil.isNetworkAvailable;


public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TO_MAIN = 0X123;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SPLASH_TO_MAIN){
                checkNetWork();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.sendEmptyMessageDelayed(SPLASH_TO_MAIN,5*1000);
    }

    private void checkNetWork() {
        if (isNetworkAvailable(this)){
            turn2MainActivity();
            finish();
        }else {
            ToastUtil.doToast(this,"网络连接失败");
        }
    }

    private void turn2MainActivity() {
        startActivity(new Intent(this,MainActivity.class));
    }
}
