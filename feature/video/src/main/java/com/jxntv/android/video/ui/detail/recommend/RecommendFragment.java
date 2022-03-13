package com.jxntv.android.video.ui.detail.recommend;

import static com.jxntv.android.video.ui.detail.DetailAdapter.ACTION_ITEM;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.jxntv.android.video.ui.detail.DetailFragment;
import com.jxntv.android.video.ui.detail.DetailViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;

public class RecommendFragment extends DetailFragment<RecommendModel> {

    private RecommendViewModel mRecommendViewModel;

    @NonNull
    @Override
    protected BaseRecyclerAdapter<RecommendModel, ?> createAdapter() {
        RecommendAdapter adapter = new RecommendAdapter(requireContext());
        adapter.setStat(getStat());
        adapter.mActionLiveData.observe(this,
                recommendModelActionModel -> {
                    if (recommendModelActionModel.type == ACTION_ITEM) {
                        String fromPid = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_PID) : null;
                        mRecommendViewModel.navigateToVideoFragment(mRecyclerView, recommendModelActionModel.model, fromPid);
                    }
                });
        return adapter;
    }

    @Override
    protected void bindViewModels() {
        mRecommendViewModel = bingViewModel(RecommendViewModel.class);
        mRecommendViewModel.setMediaId(mediaId);
        mRecommendViewModel.getNoMoreLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean noMoreData) {
                mRefreshLayout.setNoMoreData(noMoreData);
            }
        });
    }

    @NonNull
    @Override
    protected DetailViewModel<RecommendModel> getDetailViewModel() {
        return mRecommendViewModel;
    }

}
