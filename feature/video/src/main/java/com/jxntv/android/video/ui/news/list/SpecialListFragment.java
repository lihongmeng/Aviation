package com.jxntv.android.video.ui.news.list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jxntv.android.video.Constants;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.FragmentSpecialBinding;
import com.jxntv.android.video.ui.news.adapter.SpecialChannelAdapter;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


/**
 * @author huangwei
 * date : 2021/5/22
 * desc : 专题
 **/
public class SpecialListFragment extends MediaPageFragment<FragmentSpecialBinding> {

    private NewsListViewModel viewModel;
    private String mediaId;
    private String tagId;


    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
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
        return R.layout.fragment_special;
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
    protected void initView() {
        super.initView();
        setToolbarTitle("专题");
        enableRightOperationImageView(true);
        showRightOperationImageView(true);
        setRightOperationImage(R.drawable.ic_common_share_top_black);
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
        mBinding.specialChannel.setLayoutManager(new GridLayoutManager(getContext(),4));
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
    }


    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        getActivity().finish();
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        if(viewModel==null){
            return;
        }
        VideoModel videoModelValue=viewModel.getVideoModel().getValue();
        if(videoModelValue==null){
            return;
        }
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setVideoModel(videoModelValue)
                .setShowShare(videoModelValue.mediaStatus != null && videoModelValue.mediaStatus == 3)
                .setShowFollow(false)
                .setShowFontSetting(false)
                .setShowDelete(false)
                .setShowCreateBill(true)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(view.getContext(), false,true, shareDataModel, null);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(NewsListViewModel.class);
        viewModel.setSpecial(true);
        viewModel.init(mAdapter,this);

        viewModel.getVideoModel().observe(this, videoModel -> {

            mBinding.setVideoModel(videoModel);

            if (videoModel.getSpecialTagList()!=null && videoModel.getSpecialTagList().size()>0) {
                SpecialChannelAdapter adapter = new SpecialChannelAdapter();
                adapter.addData(videoModel.getSpecialTagList());
                mBinding.specialChannel.setAdapter(adapter);
                mBinding.specialChannelName.setText(videoModel.getSpecialTagList().get(0).getName());
                tagId = videoModel.getSpecialTagList().get(0).getId();
                viewModel.refresh(tagId);
                mTabItemId = viewModel.SPECIAL_LIST + tagId;

                adapter.setListener(tagModel -> {
                    mBinding.specialChannelName.setText(tagModel.getName());
                    tagId = tagModel.getId();
                    viewModel.refresh(tagId);
                    mTabItemId = viewModel.SPECIAL_LIST + tagId;
                });
            }
        });
    }

    @Override
    protected void loadData() {
        viewModel.loadData(mediaId);
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
}
