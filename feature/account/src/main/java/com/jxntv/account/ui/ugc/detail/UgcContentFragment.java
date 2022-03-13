package com.jxntv.account.ui.ugc.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentUgcContentBinding;
import com.jxntv.base.Constant;
import com.jxntv.base.model.share.FavoriteChangeModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.stat.StatPid;

import java.util.ArrayList;
import java.util.List;

/**
 * 喜欢界面
 *
 * @since 2020-02-10 15:12
 */
@SuppressWarnings("FieldCanBeLocal")
public final class UgcContentFragment extends MediaPageFragment<FragmentUgcContentBinding> {

    private UgcContentViewModel viewModel;
    private int type;
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
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        type = bundle.getInt("type", UgcContentType.COMPOSITION);
        author = bundle.getParcelable("author");
    }

    @Override
    protected void initView() {
        super.initView();
        mTabItemId ="tagType_"+type;
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);

        mBinding.error.errorImage.setOnClickListener(view -> {
            onReload(view);
        });
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(UgcContentViewModel.class);
        init(mTabItemId,false);
        viewModel.updateTabId(mTabItemId);
        viewModel.setFromAuthor(author);
        viewModel.setContentType(type);

    }

    @Override
    protected void updatePlaceholderLayoutType(int type) {
        //todo 因布局原因暂时这样处理占位布局
        if (type == PlaceholderType.EMPTY){
            mBinding.placeHolder.setVisibility(View.VISIBLE);
            mBinding.empty.root.setVisibility(View.VISIBLE);
            mBinding.error.root.setVisibility(View.GONE);
            mBinding.loading.root.setVisibility(View.GONE);
        }else if (type == PlaceholderType.LOADING){
            mBinding.placeHolder.setVisibility(View.VISIBLE);
            mBinding.empty.root.setVisibility(View.GONE);
            mBinding.error.root.setVisibility(View.GONE);
            mBinding.loading.root.setVisibility(View.VISIBLE);
        } else if (type == PlaceholderType.NONE){
            mBinding.placeHolder.setVisibility(View.GONE);
        }else {
            mBinding.placeHolder.setVisibility(View.VISIBLE);
            mBinding.empty.root.setVisibility(View.GONE);
            mBinding.error.root.setVisibility(View.VISIBLE);
            mBinding.loading.root.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.loadRefreshData();
        }
    }

    /**
     * 监听时间
     */
    @Override
    public void listenEvent() {
        super.listenEvent();
        if (author.isSelf()) {
            if (type == UgcContentType.FAVORITE) {
                //喜欢状态变化
                GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class).observe(this, o -> {
                            viewModel.loadRefreshData();
                        });
            }

            //动态
            if (type == UgcContentType.COMPOSITION){
                GVideoEventBus.get(Constant.EVENT_MSG.COMPOSITION_ADD).observe(this, o -> {
                    viewModel.loadRefreshData();
                });
            }

            //提问
            if (type == UgcContentType.QUESTION){
                GVideoEventBus.get(Constant.EVENT_MSG.COMPOSITION_ADD_QUESTION).observe(this, o -> {
                    viewModel.loadRefreshData();
                });
            }

            //回答
            if (type == UgcContentType.ANSWER){
                GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD_QA).observe(this, o -> {
                    viewModel.loadRefreshData();
                });
            }
        }

    }


    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        List<VideoModel> list = new ArrayList<>();
        list.add(mediaModel);
        ShortVideoListModel listModel = ShortVideoListModel.Builder.aFeedModel()
                .withList(list)
                .build();
        return listModel;
    }

    @Override
    public String getPid() {
        switch (type){
            case UgcContentType.COMPOSITION:
                return StatPid.UGC_COMPOSITION;
            case UgcContentType.FAVORITE:
                return StatPid.UGC_FAVORITE;
            case UgcContentType.QUESTION:
                return StatPid.UGC_QUESTION;
            case UgcContentType.ANSWER:
                return StatPid.UGC_ANSWER;
            default:
                return "";
        }
    }
}
