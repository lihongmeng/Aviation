package com.jxntv.account.ui.favortite;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentFavoriteBinding;
import com.jxntv.account.model.Author;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.share.FavoriteChangeModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;


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
