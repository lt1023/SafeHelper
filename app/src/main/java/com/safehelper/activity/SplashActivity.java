package com.safehelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.safehelper.R;

import static com.safehelper.utils.NetWorkUtil.isNetworkAvailable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isNetworkAvailable(this);
    }

}
