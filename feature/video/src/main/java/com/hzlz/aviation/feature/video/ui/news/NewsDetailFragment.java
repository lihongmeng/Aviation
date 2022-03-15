package com.hzlz.aviation.feature.video.ui.news;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.dialog.FontSizeSettingDialog;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.base.webview.GVWebView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.FragmentNewsDetailBinding;
import com.hzlz.aviation.feature.video.databinding.ViewNewsContentBinding;
import com.hzlz.aviation.feature.video.databinding.ViewNewsSpecialBinding;

/**
 * @author huangwei
 * date : 2021/5/19
 * desc : 新闻详情页
 **/
public class NewsDetailFragment extends BaseFragment<FragmentNewsDetailBinding> {

    private NewsDetailViewModel viewModel;
    private String mediaId;
    private VideoModel videoModel;
    private WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }


    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        videoModel = bundle.getParcelable(Constants.EXTRA_VIDEO_MODEL);
        mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
        if (TextUtils.isEmpty(mediaId) && videoModel!=null){
            mediaId = videoModel.getId();
        }
    }

    @Override
    protected void initView() {
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
        mBinding.back.setOnClickListener(view -> getActivity().finish());
        setupPlaceholderLayout(R.id.empty_container);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(NewsDetailViewModel.class);
        mBinding.setViewModel(viewModel);
        mBinding.newsDetailBottom.setViewModel(viewModel);
        viewModel.loadData(mediaId);

        viewModel.getVideoModel().observe(this, videoModel -> {

            mBinding.newsDetailBottom.setVideoObservable(videoModel.getObservable());

            mBinding.setVideoModel(videoModel);
            if (!TextUtils.isEmpty(videoModel.getDetailBigPic()) && !TextUtils.isEmpty(videoModel.getDetailSmallPic())) {
                initSpecialNews(videoModel);
            }else {
                initNormalNews(videoModel);
            }

//            SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper("FontSizeSettingDialog");
//            int f = prefsWrapper.getInt("font_size",-1);
//            if (f>0) {
//                webView.getSettings().setTextZoom(((f * 100 - 35 * 100) / 35) + 100);
//            }

        });

        GVideoEventBus.get(SharePlugin.EVENT_SHOW_FONT_SETTING).observe(this, o -> {
            new FontSizeSettingDialog(getContext(),webView).show();
        });

    }


    @Override
    protected void loadData() {

    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        viewModel.loadData(mediaId);
    }

    /**
     * 普通新闻
     */
    private void initNormalNews(VideoModel videoModel) {

        ImmersiveUtils.enterImmersive(this, Color.WHITE, true);
        mBinding.layoutContent.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.DIMEN_44DP), 0, 0);

        LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflaters.inflate(R.layout.view_news_content, null);
        mBinding.layoutContent.addView(view);
        ViewNewsContentBinding binding = DataBindingUtil.bind(view);
        webView = binding.web;
        webView.getSettings().setUseWideViewPort(false);
        binding.setViewModel(viewModel);
        binding.setVideoModel(videoModel);

        binding.web.setPageListener(new GVWebView.onPageListener() {
            @Override
            public void onPageFinished(WebView view) {
                if (getActivity() != null && ! getActivity().isFinishing()) {
                    binding.share.addView(PluginManager.get(SharePlugin.class).getShareView(
                                    getContext(), viewModel.getShareModel(), viewModel.getStatFromModel()));
                    binding.shareText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void netError() {

            }
        });

        webView.loadDataWithBaseURL(null,videoModel.getContent(),"text/html","UTF-8",null);
    }


    /**
     * 专题新闻
     */
    private void initSpecialNews(VideoModel videoModel) {

        ImmersiveUtils.enterImmersiveFullTransparent(getActivity());
        mBinding.topView.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        mBinding.more.setImageResource(R.drawable.ic_news_more_white);

        LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflaters.inflate(R.layout.view_news_special, null);
        mBinding.layoutContent.addView(view);
        ViewNewsSpecialBinding binding = DataBindingUtil.bind(view);

        ViewGroup.LayoutParams titleLayoutParams = binding.bgSpecialSmall.getLayoutParams();
        titleLayoutParams.width = ScreenUtils.getScreenWidth(getContext());
        titleLayoutParams.height = ScreenUtils.getScreenWidth(getContext()) / 4;
        binding.bgSpecialSmall.setLayoutParams(titleLayoutParams);

        ViewGroup.LayoutParams params = binding.bgSpecialBig.getLayoutParams();
        params.width = ScreenUtils.getScreenWidth(getContext());
        params.height = ScreenUtils.getScreenWidth(getContext()) / 2;
        binding.bgSpecialBig.setLayoutParams(params);

        webView = binding.newsContent.web;
        webView.getSettings().setUseWideViewPort(false);
        binding.setViewModel(viewModel);
        binding.setVideoModel(videoModel);
        binding.newsContent.setViewModel(viewModel);
        binding.newsContent.setVideoModel(videoModel);

        binding.newsContent.web.setPageListener(new GVWebView.onPageListener() {
            @Override
            public void onPageFinished(WebView view) {
                if (getActivity() != null && ! getActivity().isFinishing()) {
                    binding.newsContent.share.addView(PluginManager.get(SharePlugin.class).getShareView(getContext(),
                                    viewModel.getShareModel(), viewModel.getStatFromModel()));
                    binding.newsContent.shareText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void netError() {

            }
        });

        webView.loadDataWithBaseURL(null,videoModel.getContent(),"text/html","UTF-8",null);

        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            //垂直方向偏移量
            int offset = Math.abs(verticalOffset);
            //最大偏移距离
            int scrollRange = appBarLayout.getTotalScrollRange();
            if (scrollRange > 0) {
                //在布局中设置的是0.7进行显示和隐藏的切换
                if (offset <= scrollRange * 0.7) {
                    binding.bgSpecialSmall.setVisibility(View.GONE);
                } else {
                    binding.bgSpecialSmall.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.bgSpecialBig.setOnClickListener(view1 -> {
            PluginManager.get(VideoPlugin.class).startSpecialList(getContext(),videoModel,null);
        });

        binding.bgSpecialSmall.setOnClickListener(view1 -> binding.bgSpecialBig.performClick());

    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @Override
    public String getPid() {
        return StatPid.DETAIL_NEWS;
    }
}
