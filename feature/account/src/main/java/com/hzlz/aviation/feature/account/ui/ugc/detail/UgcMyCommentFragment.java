package com.hzlz.aviation.feature.account.ui.ugc.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentUgcContentBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;


/**
 * ugc 评论界面
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public final class UgcMyCommentFragment extends BaseFragment<FragmentUgcContentBinding> {

    private UgcMyCommentViewModel viewModel;
    private AuthorModel author;

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ugc_content;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }


    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        author = bundle.getParcelable("author");
    }

    @Override
    protected void initView() {
        MediaFragmentManager.getInstance().addFragmentRef(this);
    }

    @Override
    protected void bindViewModels() {

        viewModel = bingViewModel(UgcMyCommentViewModel.class);
        viewModel.setFromAuthor(author);

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(viewModel.getAdapter());
        mBinding.refreshLayout.setEnableRefresh(false);
        mBinding.refreshLayout.setGVideoOnLoadMoreListener(view -> viewModel.onLoadMore(mBinding.refreshLayout));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.adapter.setFragmentId(getGvFragmentId());
        mBinding.recyclerView.setAdapter(viewModel.adapter);
        mBinding.refreshLayout.setEnableRefresh(false);
        mBinding.refreshLayout.setGVideoOnLoadMoreListener(view -> viewModel.onLoadMore(mBinding.refreshLayout));

        if (author.isSelf()){
            GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_DELETE).observe(this, new Observer<Object>() {
                @Override
                public void onChanged(Object o) {
                    viewModel.onRefresh(mBinding.refreshLayout);
                }
            });

            GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD).observe(this, new Observer<Object>() {
                @Override
                public void onChanged(Object o) {
                    viewModel.onRefresh(mBinding.refreshLayout);
                }
            });
        }

    }

    @Override
    protected void loadData() {
        viewModel.onRefresh(mBinding.refreshLayout);
    }


    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.onRefresh(mBinding.refreshLayout);
        }
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        AudioPlayManager.getInstance().release();
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        AudioPlayManager.getInstance().release();
    }

    @Override
    public String getPid() {
        return StatPid.MINE_COMMENT;
    }
}
