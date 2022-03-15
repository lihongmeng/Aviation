package com.hzlz.aviation.feature.search.page.fragment;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.search.databinding.SearchRecyclerLayoutBinding;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.page.SearchBlankRefreshFooterView;
import com.hzlz.aviation.feature.search.page.SearchPageViewModel;
import com.hzlz.aviation.feature.search.page.SearchRecyclerAdapter;
import com.hzlz.aviation.feature.search.repository.SearchResultRepository;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.media.template.IMediaTemplate;
import com.hzlz.aviation.kernel.media.template.view.MediaBaseVideoTemplate;
import com.hzlz.aviation.feature.search.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索页面fragment基类
 */
public abstract class SearchBasePageFragment extends BaseRecyclerFragment<SearchDetailModel, SearchRecyclerLayoutBinding> {

    protected SearchPageViewModel mSearchPageViewModel;
    /**
     * 当前播放视频的位置
     */
    private int currentPlayVideoPosition = -1;

    @NonNull
    @Override
    protected BaseRecyclerAdapter<SearchDetailModel, BaseRecyclerViewHolder> createAdapter() {
        return new SearchRecyclerAdapter(getContext(), getTabType());
    }

    protected RefreshFooter createRefreshFooterView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new SearchBlankRefreshFooterView(activity);
    }

    @Override
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_recycler_layout;
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
//        stopVideoAll();
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopVideoAll();
    }

    public void stopVideoAll(){
        if (mAdapter instanceof SearchRecyclerAdapter) {
            Map<Integer,IMediaTemplate> momentViews = ((SearchRecyclerAdapter) mAdapter).getMomentViews();
            for (int position : momentViews.keySet()) {
                IMediaTemplate template = momentViews.get(position);
                if (template instanceof MediaBaseVideoTemplate) {
                    ((MediaBaseVideoTemplate) template).stop();
                }
            }
        }
    }

    /**
     * 检查视频的下一个位置
     */
    public int checkNextVideoPosition() {
        int nextPosition = -1;
        if (mAdapter instanceof SearchRecyclerAdapter) {
            HashMap<Integer, IMediaTemplate> momentViews = ((SearchRecyclerAdapter) mAdapter).getMomentViews();
            List<Integer> nextPositionList = new ArrayList<>();
            for (int position : momentViews.keySet()) {
                if (position > currentPlayVideoPosition) {
                    nextPositionList.add(position);
                }
            }
            if (nextPositionList.size() > 0) {
                Collections.sort(nextPositionList);
                nextPosition = nextPositionList.get(0);
            }
        }
        return nextPosition;
    }

    @Override
    protected void initView() {
        super.initView();
        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        /*GVideoEventBus.get(Constant.EVENT_BUS_EVENT.VIDEO_PLAY_END).observe(getViewLifecycleOwner(),
                new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                //上一个视频播放结束，自动播放下一个
                int  nextPlayVideoPosition = checkNextVideoPosition();
                if (nextPlayVideoPosition == -1) {
                    return;
                }
                smoothMoveToPosition(nextPlayVideoPosition);
            }
        });
        mBinding.recyclerView.addRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
               if( mAdapter instanceof  SearchRecyclerAdapter){
                   Map<Integer,IMediaTemplate> momentViews = ((SearchRecyclerAdapter) mAdapter).getMomentViews();
                   int bindingAdapterPosition = holder.getBindingAdapterPosition();
                   momentViews.remove(bindingAdapterPosition);
               }
            }
        });
        mBinding.recyclerView.addOnScrollListener(new RecyclerViewVideoOnScrollListener(mRecyclerView,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {
                        if (mAdapter instanceof SearchRecyclerAdapter) {
                            Map<Integer, IMediaTemplate> momentViews = ((SearchRecyclerAdapter) mAdapter).getMomentViews();
                            for (int position : momentViews.keySet()) {
                                IMediaTemplate template = momentViews.get(position);
                                if (template instanceof MediaBaseVideoTemplate) {
                                    if (position == fistVisible) {
                                        currentPlayVideoPosition = position;
                                        ((MediaBaseVideoTemplate) template).play();
                                        ((MediaBaseVideoTemplate) template).mute(true);
                                    } else {
                                        ((MediaBaseVideoTemplate) template).stop();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {

                    }

                    @Override
                    public void onItemEnter(int position) {

                    }

                    @Override
                    public void onItemExit(int position) {

                    }
                }));*/
    }

    /**
     * 使指定的项平滑到顶部
     *
     * @param position 待指定的项
     */
    private void smoothMoveToPosition(int position) {
        if (mRecyclerView == null || position < 0) {
            return;
        }
        int firstPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        int lastPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstPosition) {
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastPosition) {
            int movePosition = position - firstPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);//dx>0===>向左  dy>0====>向上
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    @Override
    protected void bindViewModels() {
        mSearchPageViewModel = bingViewModel(SearchPageViewModel.class);
        mSearchPageViewModel.setType(getTabType());
    }

    @Override
    protected void loadData() {
        SearchResultRepository.getInstance().getLiveData().observe(this,
                new Observer<SearchModel>() {
                    @Override
                    public void onChanged(SearchModel model) {
                        updateModel(model);
                    }
                });
        updateModel(SearchResultRepository.getInstance().getLiveData().getValue());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mSearchPageViewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    /**
     * 获取页面对应的tab类型
     *
     * @return tab类型
     */
    protected abstract int getTabType();

    /**
     * 更新模板数据型
     *
     * @param model 模板数据
     */
    private void updateModel(SearchModel model) {
        List<SearchDetailModel> list = getFromModel(model);
        if (list != null) {
            mSearchPageViewModel.setQuery(model.query);
            if (mAdapter instanceof SearchRecyclerAdapter) {
                ((SearchRecyclerAdapter) mAdapter).setQuery(model.query);
                ((SearchRecyclerAdapter) mAdapter).setHintQuery(model.hintQuery);
            }
            mAdapter.clearList();
            mSearchPageViewModel.loadSuccess(list);
            if (!hasMoreData()) {
                mSearchPageViewModel.showNoMoreData(R.string.all_nor_more_data);
            } else {
                mSearchPageViewModel.hideNoMoreData();
            }
            mSearchPageViewModel.loadComplete();
            if (list.size() == 0) {
                updatePlaceholderLayoutType(PlaceholderType.EMPTY_SEARCH);
            } else {
                updatePlaceholderLayoutType(PlaceholderType.NONE);
            }
        }
    }

    protected abstract List<SearchDetailModel> getFromModel(SearchModel model);

    protected abstract boolean hasMoreData();
}
