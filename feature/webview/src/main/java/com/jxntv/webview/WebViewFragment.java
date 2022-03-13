package com.jxntv.webview;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.webview.GVWebView;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.webview.databinding.FragmentWebViewBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * WebView 界面
 *
 * @since 2020-02-20 10:52
 */
public final class WebViewFragment extends BaseFragment<FragmentWebViewBinding> {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TAG = WebViewFragment.class.getSimpleName();

    //<editor-fold desc="属性">

    private WebViewViewModel mWebViewViewModel;

    private GVWebView mWebView;

    private String linkUrl;
    private Map<String, String> map;
    private boolean needSetTitle = false;
    private boolean isLoadError = false;

    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web_view;
    }

    @Override
    protected void initView() {
        try {
            mWebView = mBinding.web;
        } catch (Resources.NotFoundException e) {
            // Some older devices can crash when instantiating a WebView, due to a Resources$NotFoundException
            // Creating with the application Context fixes this, but is not generally recommended for view creation
            mWebView = new GVWebView(requireContext().getApplicationContext());
        }
        setupPlaceholderLayout(R.id.empty);
    }

    @Override
    protected void bindViewModels() {
        mWebViewViewModel = bingViewModel(WebViewViewModel.class);

        updatePlaceholderLayoutType(PlaceholderType.NONE);

        //开启页面自动播放，二级页面需要处理
        if (!TextUtils.isEmpty(getPageName()) && !TextUtils.equals(getPid(), getPageName())) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(true);
            mBinding.progressHorizontal.setVisibility(View.GONE);
        } else {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            mWebView.setProgressChangedListener(newProgress -> {
                mBinding.progressHorizontal.setProgress(newProgress);
                mBinding.progressHorizontal.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            });
        }

        mWebViewViewModel.getTitleLiveData().observe(this, new NotNullObserver<String>() {
            @Override
            protected void onModelChanged(@NonNull String title) {
                if (!TextUtils.isEmpty(title)) {
                    if (TextUtils.equals(title,WebViewActivity.class.getName())){
                        setToolbarTitle("");
                        needSetTitle = true;
                    }else {
                        setToolbarTitle(title);
                        setPageName(title + "_H5页面");
                        onFragmentResume();
                    }
                }else {
                    getToolbar().hide();
                }
            }
        });
        mWebViewViewModel.getUrlLiveData().observe(this, new NotNullObserver<String>() {
            @Override
            protected void onModelChanged(@NonNull String url) {
                map = new HashMap<>();
                linkUrl = url;
                PackageManager pm = getContext().getPackageManager();
                PackageInfo pi = null;
                try {
                    pi = pm.getPackageInfo(getContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + "/tvcVersion" + pi.versionName);
                mWebView.loadUrl(linkUrl, map);
            }
        });

        mWebView.setPageListener(new GVWebView.onPageListener() {
            @Override
            public void onPageFinished(WebView view) {
                if (needSetTitle && !TextUtils.isEmpty(view.getTitle())) {
                    setToolbarTitle(view.getTitle());
                    setPageName(view.getTitle() + "_H5页面");
                    onFragmentResume();
                }
            }

            @Override
            public void netError() {
                isLoadError = true;
                updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            }
        });

        mWebView.setOpenFileChooserListener(i -> startActivityForResult(i,2));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWebView!=null){
            mWebView.setOpenFileResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onReload(@NonNull View view) {
        isLoadError = false;
        updatePlaceholderLayoutType(PlaceholderType.NONE);
        mWebView.loadUrl(linkUrl, map);
    }

    @Override
    protected void loadData() {
        listenEvent();
        Bundle arguments = getArguments();
        if (arguments != null) {
            mWebViewViewModel.loadData(WebViewFragmentArgs.fromBundle(arguments));
        }
    }

    //</editor-fold>

    private void listenEvent() {
        GVideoEventBus.get(WebViewPlugin.EVENT_CLOSE_PAGE).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (!Navigation.findNavController(mWebView).navigateUp()) {
                    requireActivity().finish();
                }
            }
        });
        GVideoEventBus.get(WebViewPlugin.EVENT_HIDE_NAV).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (DEBUG) {
                    Log.d(TAG, "requestHideToolbar");
                }
                getToolbar().hide();
            }
        });
        GVideoEventBus.get(WebViewPlugin.EVENT_SET_TITLE, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String title) {
                setToolbarTitle(title);
            }
        });
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        if (mWebView.canGoBack() && !isLoadError) {
            mWebView.goBack();
        } else {
            if (getActivity() instanceof WebViewActivity) {
                getActivity().finish();
            } else {
                super.onLeftBackPressed(view);
            }

        }
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        //通知进入
        loadJs("javascript:activeAppTab()");
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        //通知离开
        loadJs("javascript:inactiveAppTab()");
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        loadJs("javascript:activeAppTab()");
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        loadJs("javascript:inactiveAppTab()");
    }

    public void loadJs(String js) {
        if (mWebView == null) {
            return;
        }
        mWebView.evaluateJavascript(js, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    public String getPageName() {
        if (TextUtils.isEmpty(super.getPageName())){
            if (mWebView == null) {
                return "";
            }
            String webTitle = mWebView.getTitle();
            webTitle = TextUtils.isEmpty(webTitle) ? "" : (webTitle+"_");
            return webTitle + "H5页面";
        }
        return super.getPageName();
    }

}
