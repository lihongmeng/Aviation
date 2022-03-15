package com.hzlz.aviation.kernel.base.scan;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ActivityScanBinding;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScanActivity extends BaseActivity<ActivityScanBinding> implements DecoratedBarcodeView.TorchListener {

    private CaptureManager captureManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initView() {
        mBinding.back.setOnClickListener(v -> finish());

        int halfScreenHeight = ScreenUtils.getScreenHeight(this) / 2 - 480;
        if (mBinding.tip.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mBinding.tip.getLayoutParams();
            p.setMargins(0, 0, 0, halfScreenHeight);
            mBinding.tip.requestLayout();
        }
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, mBinding.scanView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        mBinding.scanView.setTorchListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mBinding.scanView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);

    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

}
