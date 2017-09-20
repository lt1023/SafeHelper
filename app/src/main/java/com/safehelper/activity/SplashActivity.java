package com.safehelper.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.safehelper.R;
import com.safehelper.utils.ToastUtil;
import static com.safehelper.utils.NetWorkUtil.isNetworkAvailable;


public class SplashActivity extends AppCompatActivity {
    private TextView version;
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
        getPackageVersion();
        mHandler.sendEmptyMessageDelayed(SPLASH_TO_MAIN,5*1000);
    }

    /**
     * 获取版本名并在界面设置版本名称
     */
    private void getPackageVersion() {
        version = (TextView) findViewById(R.id.tv_slpash_version);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("com.safehelper",0);
            String versionName = pi.versionName;
            version.setText("version : " + versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查网络是否可用
     */
    private void checkNetWork() {
        if (isNetworkAvailable(this)){
            turn2MainActivity();
            finish();
        }else {
            ToastUtil.doToast(this,"网络连接失败");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    turn2MainActivity();
                }
            },2000);
        }
    }

    /**
     * 跳转到主界面
     */
    private void turn2MainActivity() {
        startActivity(new Intent(this,MainActivity.class));
    }
}
