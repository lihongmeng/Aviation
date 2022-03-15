package com.hzlz.aviation.feature.account.ui.favortite;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentFavoriteBinding;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.kernel.base.model.share.FavoriteChangeModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;


/**
 * 喜欢界面
 *
 * @since 2020-02-10 15:12
 */
@SuppressWarnings("FieldCanBeLocal")
public final class FavoriteFragment extends MediaPageFragment<FragmentFavoriteBinding> {

    private FavoriteViewModel mFavoriteViewModel;

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        mTabItemId = FavoriteFragment.class.getName();
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
    }

    @Override
    protected void bindViewModels() {
        Author author = getArguments() != null ? getArguments().getParcelable("author") : null;
        mFavoriteViewModel = bingViewModel(FavoriteViewModel.class);
        mFavoriteViewModel.updateTabId(mTabItemId);
        mFavoriteViewModel.setFromAuthor(author);
        mBinding.setViewModel(mFavoriteViewModel);

        mFavoriteViewModel.getFavoriteData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean){

                }
            }
        });
    }

    @Override
    public void onReload(@NonNull View view) {
//        if (mFavoriteViewModel != null) {
//            mFavoriteViewModel.loadRefreshData();
//        }

    }

    /**
     * 监听时间
     */
    @Override
    public void listenEvent() {
        super.listenEvent();
        //收藏状态变化
        GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class)
                .observe(this, favoriteChangeModel -> {
                    mFavoriteViewModel.loadRefreshData();
                });
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return null;
    }
}
