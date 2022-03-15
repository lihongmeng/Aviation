package com.hzlz.aviation.feature.video.ui.vshort;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.MUTE_VIDEO;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME;
import static com.hzlz.aviation.kernel.base.plugin.SharePlugin.EVENT_COMPOSITION_DELETE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.vshort.comment.CommentPopupFragment;
import com.hzlz.aviation.feature.video.ui.vshort.info.VideoShortInfoViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoShortControllerBinding;
import com.hzlz.aviation.feature.video.databinding.VideoShortFragmentBinding;

public class VideoShortFragment extends BaseFragment<VideoShortFragmentBinding> {

    private static final String TAG = VideoShortFragment.class.getSimpleName();

    // VideoShortViewModel
    private VideoShortViewModel mViewModel;

    // 视频进度条以及视频相关信息的View集合
    private VideoShortControllerBinding mControllerBinding;

    // 是否初始化时就打开评论弹窗
    private boolean mStartComment;

    // 神策相关数据
    private StatFromModel mStatFromModel;

    // 当前页面是否可见，用于神策数据上报
    private boolean mVisible = false;

    // 是否需要展示下方的播放进度条
    private boolean mShowProgress;

    // 底部播放进度条的高度
    // 点击屏幕时需要先用ValueAnimator改变高度，再显示或隐藏播放进度条
    private int mProgressHeight;

    /**
     * 创建VideoShortFragment实例
     *
     * @param videoModel    视频数据
     * @param bundle        携带数据
     * @param isShowComment 是否需要展示评论弹窗
     * @return VideoShortFragment
     */
    public static VideoShortFragment newInstance(
            VideoModel videoModel,
            Bundle bundle,
            boolean isShowComment
    ) {
        VideoShortFragment fragment = new VideoShortFragment();
        if (bundle != null) {
            bundle.putParcelable(Constants.EXTRA_VIDEO_MODEL, videoModel);
            if (!isShowComment) {
                bundle.putBoolean(Constants.EXTRA_COMMENT, false);
            }
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getPid() {
        return StatPid.DETAIL;
    }

    @Override
    public VideoModel getVideoModel() {
        if (mViewModel == null) {
            return null;
        }
        return mViewModel.videoModel;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (true) {
            LogUtils.d(TAG, this + " : onDestroy vm = " + mViewModel);
        }
        if (mViewModel != null) {
            mViewModel.destroy(mBinding.shortVideoPlayView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.resume(mBinding.shortVideoPlayView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.pause(mBinding.shortVideoPlayView);
            mViewModel.statPlay();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        mVisible = true;
        if (mViewModel != null) {
            mViewModel.setVisible(true);
            mViewModel.resume(mBinding.shortVideoPlayView);
        }

        if (true) {
            LogUtils.d(TAG, this + " : onVisible vm = " + mViewModel + " v= " + mVisible);
        }
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();


        mVisible = false;
        if (mViewModel != null) {
            mViewModel.seek(mBinding.shortVideoPlayView, 0);
            mViewModel.pause(mBinding.shortVideoPlayView);
            mViewModel.setVisible(false);
            mViewModel.statPlay();
        }
        if (true) {
            LogUtils.d(TAG, this + " : onInVisible vm = " + mViewModel + " v= " + mVisible);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_short_fragment;
    }

    @Override
    protected void initView() {
        VideoShortController controller = new VideoShortController(requireContext());
        mBinding.shortVideoPlayView.setCanFullscreen(false);
        mBinding.shortVideoPlayView.setMediaController(controller);

        mControllerBinding = controller.getBinding();
        controller.show();

        mProgressHeight = SizeUtils.dp2px(40);
    }

    @Override
    protected void bindViewModels() {
        String mFromPid = "", mFromChannelId = "";
        VideoModel videoModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            videoModel = bundle.getParcelable(Constants.EXTRA_VIDEO_MODEL);
            mStartComment = bundle.getBoolean(Constants.EXTRA_COMMENT);
            mFromPid = bundle.getString(Constant.EXTRA_FROM_PID);
            mFromChannelId = bundle.getString(Constant.EXTRA_FROM_CHANNEL_ID);
        }
        String mediaId = (mViewModel == null || mViewModel.videoModel == null) ? "" : mViewModel.videoModel.getId();
        mStatFromModel = new StatFromModel(mediaId, getPid(), "", mFromPid, mFromChannelId);
        mViewModel = bingViewModel(VideoShortViewModel.class);
        mViewModel.initKSView(mBinding.shortVideoPlayView, videoModel);
        mViewModel.setStat(mStatFromModel);
        mViewModel.setVisible(mVisible);
        if (true) {
            Log.d(TAG, this + " : bindViewModels visible = " + mVisible + " ;isVisible=" + isVisible());
        }
    }

    @Override
    protected void loadData() {
        GVideoEventBus.get(EVENT_COMPOSITION_DELETE, String.class).observe(this,
                mediaId -> {
                    if (!isVisible()) return;
                    String currentMediaId = (mViewModel == null || mViewModel.videoModel == null) ? "" : mViewModel.videoModel.getId();
                    if (!TextUtils.equals(currentMediaId, mediaId)) return;
                    //退出当前页面
                    if (mControllerBinding != null) {
                        mControllerBinding.getViewModel().onBackPressed(mControllerBinding.ivBack);
                    }
                });

        GVideoEventBus.get(MUTE_VIDEO, String.class).observe(this, o -> mBinding.shortVideoPlayView.setMute(true));

        GVideoEventBus.get(RESUME_VIDEO_VOLUME, String.class).observe(this, o -> mBinding.shortVideoPlayView.setMute(false));

        mViewModel.videoLiveData.observe(this, o -> {
            if (mViewModel.videoModel == null) {
                return;
            }
            AuthorModel authorModel = mViewModel.videoModel.getAuthor();
            if (authorModel == null) return;

            // 关注按钮和大V标识靠得太近
            // 如果需要展示关注按钮，就隐藏大V标识
            // 如果不需要，再判断是否需要展示大V标识
            mControllerBinding.authentication.setVisibility(
                    authorModel.isSelf() && authorModel.isAuthentication() ? VISIBLE : GONE);

            initData(mViewModel.videoModel);

            if (mStartComment) {
                mControllerBinding.getViewModel().mCommentLiveData.setValue(0);
                mStartComment = false;
            }
        });

        // 开始播放视频数据
        mViewModel.loadInfo(mBinding.shortVideoPlayView);
    }

    private void initData(VideoModel videoModel) {
        if (mControllerBinding == null) {
            return;
        }
        VideoShortInfoViewModel infoViewModel = new VideoShortInfoViewModel(GVideoRuntime.getApplication(), videoModel);
        infoViewModel.setStatFromModel(mStatFromModel);
        mControllerBinding.setVideoObservable(videoModel.getObservable());
        mControllerBinding.setAuthorObservable(videoModel.getAuthor().getObservable());
        mControllerBinding.setViewModel(infoViewModel);

        mControllerBinding.linkLayout.setContentTextSize(R.dimen.sp_13);
        mControllerBinding.linkLayout.setContentTextColor(R.color.color_ffffff);

        if (TextUtils.isEmpty(mViewModel.videoModel.outShareUrl)) {
            mControllerBinding.linkLayout.setVisibility(GONE);
        } else {
            mControllerBinding.linkLayout.updateLinkTitle(
                    TextUtils.isEmpty(mViewModel.videoModel.outShareTitle)
                            ? ResourcesUtils.getString(R.string.share_link_default_tip) : mViewModel.videoModel.outShareTitle
            );
            mControllerBinding.linkLayout.updateLinkValue(mViewModel.videoModel.outShareUrl);

            // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
            // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
            // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
            mControllerBinding.linkLayout.setVisibility(VISIBLE);
        }


        infoViewModel.mCommentLiveData.observe(this, integer -> {
            CommentPopupFragment mCommentFragment = new CommentPopupFragment();
            boolean canComment = videoModel.isCanComment();
            mCommentFragment.setUp(videoModel, getArguments(), canComment);
            getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.push_top_in, R.anim.push_bottom_out)
                    .replace(R.id.comment_container, mCommentFragment)
                    .commit();
        });
        getChildFragmentManager().registerFragmentLifecycleCallbacks(
                new FragmentManager.FragmentLifecycleCallbacks() {

                    @Override
                    public void onFragmentCreated(
                            @NonNull FragmentManager fragmentManager,
                            @NonNull Fragment fragment,
                            @Nullable Bundle savedInstanceState
                    ) {
                        mBinding.commentContainer.setVisibility(VISIBLE);
                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.SUPPORT_SLIDE, Boolean.class).post(false);
                    }

                    @Override
                    public void onFragmentDestroyed(
                            @NonNull FragmentManager fragmentManager,
                            @NonNull Fragment fragment
                    ) {
                        mBinding.commentContainer.setVisibility(GONE);
                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.SUPPORT_SLIDE, Boolean.class).post(true);
                    }

                }, false);
    }

    public void onRootViewDoubleTap(MotionEvent e) {
        if (mControllerBinding == null) {
            return;
        }
        mViewModel.triggerPlay(mBinding.shortVideoPlayView);
        if (mBinding.shortVideoPlayView.getMediaController() != null) {
            mBinding.shortVideoPlayView.getMediaController().updatePausePlay();
        }
    }

    public void onRootViewSingleTapUp(MotionEvent e) {
        if (mControllerBinding == null) {
            return;
        }
        showProgress(!mShowProgress);
    }

    private void showProgress(boolean show) {
        //pauseView && progress 向下动画
        float startValue = 0.0f;
        if (!show) {
            startValue = 1.0f - startValue;
        }

        View[] viewInfoArray = {
                mControllerBinding.avatar, mControllerBinding.name, mControllerBinding.description,
                mControllerBinding.follow, mControllerBinding.title, mControllerBinding.simpleProgress
        };
        View viewInfo = mControllerBinding.info;
        View viewProgress = mControllerBinding.progress;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, 1.0f - startValue);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            for (View view : viewInfoArray) {
                view.setAlpha(1 - value);
            }
            viewProgress.setAlpha(value);
            ViewGroup.LayoutParams params = viewProgress.getLayoutParams();
            params.height = (int) (1.0f * value * mProgressHeight);
            //params.width = (int) ((1.2f - 0.2f * value) * mProgressWidth);
            viewProgress.setLayoutParams(params);
        });
        valueAnimator.setDuration(400);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewProgress.setVisibility(VISIBLE);
                viewInfo.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShowProgress = show;
                viewProgress.setVisibility(show ? VISIBLE : GONE);
                viewInfo.setVisibility(show ? GONE : VISIBLE);
            }
        });
        valueAnimator.start();
    }

    @Override
    protected Long getTenantId() {
        if (mViewModel == null
                || mViewModel.videoModel == null) {
            return null;
        }
        GroupInfo groupInfo=mViewModel.videoModel.getGroupInfo();
        if(groupInfo==null){
            return null;
        }
        return groupInfo.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (mViewModel == null
                || mViewModel.videoModel == null) {
            return "";
        }
        GroupInfo groupInfo=mViewModel.videoModel.getGroupInfo();
        if(groupInfo==null){
            return "";
        }
        return groupInfo.tenantName;
    }

}
