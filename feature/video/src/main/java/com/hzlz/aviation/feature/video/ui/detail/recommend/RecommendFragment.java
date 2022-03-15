package com.hzlz.aviation.feature.video.ui.detail.recommend;

import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_ITEM;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.video.ui.detail.DetailFragment;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;

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
