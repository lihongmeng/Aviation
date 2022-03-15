package com.hzlz.aviation.feature.video.ui.news.list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.FragmentRecyclerListBinding;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


/**
 * @author huangwei
 * date : 2021/5/22
 * desc : 新闻内容列表
 **/
public class NewsListFragment extends MediaPageFragment<FragmentRecyclerListBinding> {

    private NewsListViewModel viewModel;
    private String mediaId;
    private VideoModel videoModel;


    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
        videoModel = bundle.getParcelable(Constants.EXTRA_VIDEO_MODEL);
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.placeholder;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refreshLayout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_list;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }


    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return null;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        getActivity().finish();
    }

    @Override
    protected void bindViewModels() {

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        viewModel = bingViewModel(NewsListViewModel.class);
        init(viewModel.NEWS_LIST+mediaId,false);
        viewModel.init(mAdapter,this);
        viewModel.updateTabId(mTabItemId);
        viewModel.refresh(mediaId);
        if (viewModel!=null) {
            setToolbarTitle(videoModel.getTitle());
        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        viewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        viewModel.loadRefreshData();
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
        return StatPid.HOME_NEWS;
    }
}
