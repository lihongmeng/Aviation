package com.jxntv.account.ui.pgc;


import static com.jxntv.base.Constant.AUTHOR_TYPE;
import static com.jxntv.base.Constant.EXTRA_AUTHOR_ID;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentPgcContentBinding;
import com.jxntv.base.decoration.RecyclerDividerItemDecoration;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.stat.StatPid;

/**
 * @author huangwei
 * date : 2021/5/18
 * desc : 作品
 **/
public class PgcContentFragment extends MediaPageFragment<FragmentPgcContentBinding> {

    private PgcContentViewModel viewModel;
    private int tagType, authorType;
    private String authorId;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pgc_content;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        authorId = getArguments() != null ? getArguments().getString(EXTRA_AUTHOR_ID) : "";
        authorType = getArguments() != null ? getArguments().getInt(AUTHOR_TYPE) : AuthorType.PGC;
        tagType = getArguments() != null ? getArguments().getInt("tagType") : 0;
    }

    @Nullable
    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return tagType >= 4 ? new RecyclerDividerItemDecoration(getContext(),
                LinearLayoutManager.HORIZONTAL,
                getResources().getDimensionPixelOffset(R.dimen.DIMEN_D5P), R.color.c_line02) : null;
    }


//    @Override
//    protected void updatePlaceholderLayoutType(int type) {
//        super.updatePlaceholderLayoutType(type);
//        if (type == PlaceholderType.NONE && mRefreshLayout!=null) {
//            smartRefreshLayout.finishRefresh();
//            smartRefreshLayout.finishLoadMore();
//        }
//    }


    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return viewModel.createShortListModel(mediaModel);
    }

//    public void loadMore(SmartRefreshLayout refreshLayout){
//        this.smartRefreshLayout = refreshLayout;
//        viewModel.onLoadMore();
//    }
//
//    public void refresh(SmartRefreshLayout refreshLayout){
//        this.smartRefreshLayout = refreshLayout;
//        viewModel.onRefresh();
//    }

    @Override
    protected void bindViewModels() {

        viewModel = bingViewModel(PgcContentViewModel.class);
        init("tagType_" + tagType, false);
        viewModel.updateTabId(mTabItemId);
        viewModel.setAuthorId(authorId, authorType, tagType);
        mBinding.setViewModel(viewModel);
        mBinding.setApter(mAdapter);
    }

    @Override
    public String getPid() {
        return StatPid.PGC_CONTENT;
    }

    //    /**
//     * 结束刷新
//     */
//    protected void finishRefresh() {
//        super.finishRefresh();
//        if (smartRefreshLayout == null) {
//            return;
//        }
//        if (smartRefreshLayout.getState() == RefreshState.Refreshing) {
//            smartRefreshLayout.finishRefresh();
//        } else if (smartRefreshLayout.getState() == RefreshState.Loading) {
//            smartRefreshLayout.finishLoadMore();
//        }
//        smartRefreshLayout = null;
//    }
}
