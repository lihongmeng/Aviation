package com.jxntv.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;


/**
 * 空页面，用于解决切到后台，点击桌面快捷方式返回时，会杀死HomeActivity栈之上Activity的问题
 */
public class BlankActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(BlankActivity.this, SplashActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}

