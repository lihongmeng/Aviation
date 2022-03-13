package com.jxntv.android.video.ui.news.list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.android.video.Constants;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.FragmentRecyclerListBinding;
import com.jxntv.base.decoration.RecyclerDividerItemDecoration;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.recycler.BaseRecyclerFragment;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.media.recycler.MediaRecyclerAdapter;
import com.jxntv.stat.StatPid;
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
