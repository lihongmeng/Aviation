package com.tencent.qcloud.tuikit.tuigroup.ui.page;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jxntv.base.immersive.ImmersiveUtils;
import com.tencent.qcloud.tuikit.tuigroup.R;


public class GroupInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_activity);
        ImmersiveUtils.enterImmersive(this, Color.WHITE,true,true);
        GroupInfoFragment fragment = new GroupInfoFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.group_manager_base, fragment).commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        setResult(1001);
    }
}
