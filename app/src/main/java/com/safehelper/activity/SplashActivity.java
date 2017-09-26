package com.safehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import com.safehelper.R;
import com.safehelper.utils.ToastUtil;
import static com.safehelper.utils.NetWorkUtil.isNetworkAvailable;
import static com.safehelper.utils.UpdateUtil.getPackageVersionName;


public class SplashActivity extends Activity {
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
        String versionName = getPackageVersionName(this);
        version = (TextView) findViewById(R.id.tv_slpash_version);
        version.setText("version : " + versionName);
        mHandler.sendEmptyMessageDelayed(SPLASH_TO_MAIN,5*1000);
        initAnimation();
    }

    /***
     * 动画效果
     */
    private void initAnimation() {
        Animation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(2*1000);
        findViewById(R.id.activity_splash).startAnimation(alpha);
    }

    /**
     * 检查网络是否可用
     */
    private void checkNetWork() {
        if (isNetworkAvailable(this)){
            turn2MainActivity();
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
        finish();
    }
}
