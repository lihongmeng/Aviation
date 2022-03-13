package com.jxntv.search;

import static com.jxntv.base.Constant.EVENT_BUS_EVENT.START_SCAN_HOME;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.START_SCAN_SEARCH;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.scan.ScanActivity;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.search.databinding.ActivitySearchBinding;
import com.jxntv.search.utils.SearchFragmentHelper;

/**
 * 搜索activity
 */
public class SearchActivity extends BaseActivity<ActivitySearchBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {

        NavController navController = Navigation.findNavController(this, R.id.fragment_entry);
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        NavGraph graph = navController.getNavInflater().inflate(R.navigation.search_module_nav_graph);
        navController.setGraph(graph, bundle);

        GVideoEventBus.get(START_SCAN_SEARCH).observe(
                this,
                o -> {
                    new IntentIntegrator(this)
                            .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                            //.setPrompt("请对准二维码")// 设置提示语
                            .setCameraId(0)// 选择摄像头,可使用前置或者后置
                            .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                            .setCaptureActivity(ScanActivity.class)//自定义扫码界面
                            .initiateScan();// 初始化扫码
                }
        );
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchFragmentHelper.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult == null) {
            return;
        }
        String result = intentResult.getContents();
        if (TextUtils.isEmpty(result)) {
            return;
        }
        Log.d("TAG", "扫码结果：" + result);
        PluginManager.get(WebViewPlugin.class).startWebViewActivity(this, result, "");
    }

}
