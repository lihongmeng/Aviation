package com.hzlz.aviation.kernel.media.template;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.kernel.media.BR;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaToolBarDataBind;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutBottomAudiovisualBinding;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutBottomBinding;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutTopBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;

/**
 * media 模板实现类
 */
class MediaTemplateImpl implements IMediaTemplate {

    /**
     * 数据model
     */
    private MediaModel mMediaModel;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 子视图点击事件
     */
    private OnChildViewClickListener mClickListener;
    /**
     * 持有的toolbar binding
     */
    private MediaToolbarLayoutTopBinding mFeedToolBarBindingTop;
    /**
     * 持有的toolbar binding
     */
    private MediaToolbarLayoutBottomBinding mFeedToolBarBindingBottom;
    /**
     * tool bar数据data bind
     */
    private MediaToolBarDataBind mToolBarDataBind;
    ///** 持有的slide layout binding */
    //private FeedSlideLayoutBinding mSlideBinding;
    ///** 对应的slide 适配器 */
    //private SlideAdapter mSlideAdapter;
    /**
     * 是否为暗黑模式
     */
    private boolean mIsDarkMode;
    protected int mCurrentPosition;

    /**
     * 构造函数
     */
    public MediaTemplateImpl(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            v.setTag(mMediaModel);
            mClickListener.onClick(v);
        }
    }

    @Override
    public void setOnChildViewClickListener(OnChildViewClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public void onFragmentSwitchVisible() {
        // 无需实现
    }

    @Override
    public void update(MediaModel MediaModel, boolean isDarkMode, String fragmentId, int position) {
        mMediaModel = MediaModel;
        mIsDarkMode = isDarkMode;
        mCurrentPosition = position;
    }

    @Override
    public MediaModel getMediaModel() {
        return mMediaModel;
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return null;
    }

    @Override
    public void setViewGroup(ViewGroup group) {

    }

    /**
     * 初始化tool bar
     *
     * @param model   feed数据模型
     * @param binding 数据绑定模型
     */
    public void initToolBarTop(@NonNull MediaModel model, MediaToolbarLayoutTopBinding binding,
                               String fragmentId,int position) {
        if (binding == null || model == null) {
            return;
        }
        mFeedToolBarBindingTop = binding;
        bindToolBarTop(model, fragmentId,position);

        switch (model.showMediaPageSource) {
            case MediaPageSource.PageSource.NEWS:
            case MediaPageSource.PageSource.PGC:
            case MediaPageSource.PageSource.AUDIOVISUAL:
            case MediaPageSource.PageSource.TV_COLLECTION:
                mFeedToolBarBindingTop.getRoot().setVisibility(View.GONE);
                break;
            default:
        }

    }

    public void initToolBarBottom(@NonNull MediaModel model, MediaToolbarLayoutBottomBinding binding,
                                  String fragmentId,int position) {
        if (binding == null) {
            return;
        }
        mFeedToolBarBindingBottom = binding;
        bindToolBarBottom(model, fragmentId,position);

        switch (model.showMediaPageSource) {
            case MediaPageSource.PageSource.NEWS:
            case MediaPageSource.PageSource.AUDIOVISUAL:
                mFeedToolBarBindingBottom.getRoot().setVisibility(View.GONE);
                break;
            case MediaPageSource.PageSource.MINE_ANSWER:
                int dp15 = binding.getRoot().getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_15DP);
                mFeedToolBarBindingBottom.getRoot().setPadding(0,dp15,0,0);
                mFeedToolBarBindingBottom.feedLayout.setVisibility(View.GONE);
                mFeedToolBarBindingBottom.pgcLayout.setVisibility(View.GONE);
                break;
            default:
                mFeedToolBarBindingBottom.getRoot().setVisibility(View.VISIBLE);
        }

    }


    public void initToolbarBottomAudiovisual(
            @NonNull MediaModel model,
            MediaToolbarLayoutBottomAudiovisualBinding binding,
            String fragmentId,
            int position
    ) {
        if (binding == null) {
            return;
        }
        switch (model.showMediaPageSource) {
            case MediaPageSource.PageSource.AUDIOVISUAL:
                binding.getRoot().setVisibility(View.VISIBLE);
                mToolBarDataBind = new MediaToolBarDataBind(mIsDarkMode, model, fragmentId,position);
                binding.setVariable(BR.toolBind, mToolBarDataBind);
                binding.setVariable(BR.feedModel, model);
                binding.setVideoObservable(model.getObservable());
                binding.setAuthorObservable(model.getAuthor().getObservable());
                binding.executePendingBindings();
                break;
            default:
                binding.getRoot().setVisibility(View.GONE);
        }
    }


    /**
     * 绑定tool bar
     *
     * @param model      feed数据模型
     * @param fragmentId 对应的fragment id
     */
    private void bindToolBarTop(MediaModel model, String fragmentId,int position) {
        mToolBarDataBind = new MediaToolBarDataBind(mIsDarkMode, model, fragmentId,position);
        mFeedToolBarBindingTop.setVariable(BR.toolBind, mToolBarDataBind);
        mFeedToolBarBindingTop.setVariable(BR.feedModel, model);
        mFeedToolBarBindingTop.setVideoObservable(model.getObservable());
        if (model.getAuthor()!=null) {
            mFeedToolBarBindingTop.setAuthorObservable(model.getAuthor().getObservable());
        }
        mFeedToolBarBindingTop.executePendingBindings();
    }


    /**
     * 绑定底部部tool bar
     *
     * @param model      feed数据模型
     * @param fragmentId 对应的fragment id
     */
    private void bindToolBarBottom(MediaModel model, String fragmentId,int position) {

        mFeedToolBarBindingBottom.setVariable(BR.toolBind, new MediaToolBarDataBind(mIsDarkMode, model, fragmentId,position));
        mFeedToolBarBindingBottom.setVariable(BR.feedModel, model);
        mFeedToolBarBindingBottom.setVideoObservable(model.getObservable());
        if (model.getAuthor()!=null) {
            mFeedToolBarBindingBottom.setAuthorObservable(model.getAuthor().getObservable());
        }
        mFeedToolBarBindingBottom.executePendingBindings();

        mFeedToolBarBindingBottom.feedLayout.setVisibility(View.VISIBLE);
        mFeedToolBarBindingBottom.pgcLayout.setVisibility(View.GONE);
//        switch (model.showMediaPageSource) {
//            case MediaPageSource.PageSource.PGC:
//                mFeedToolBarBindingBottom.pgcLayout.setVisibility(View.VISIBLE);
//                mFeedToolBarBindingBottom.feedLayout.setVisibility(View.GONE);
//                break;
//            default:
//                mFeedToolBarBindingBottom.feedLayout.setVisibility(View.VISIBLE);
//                mFeedToolBarBindingBottom.pgcLayout.setVisibility(View.GONE);
//        }
    }

    /**
     * 处理跳转到详情页事件
     *
     * @param needShowComment 是否需要显示评论
     */
    public void handleNavigateToDetail(boolean needShowComment) {
        if (mToolBarDataBind != null) {
            mToolBarDataBind.handleNavigateToDetail(mContext, mMediaModel, needShowComment,false);
        }
    }

}
