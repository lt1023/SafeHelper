package com.safehelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.safehelper.R;

/**
 * Created by Administrator on 2017/9/30.
 */
public class SettinActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initListener();
    }
    //初始化数据
    private void initListener() {
        initData();
        initTitleText("setting");
    }

}
