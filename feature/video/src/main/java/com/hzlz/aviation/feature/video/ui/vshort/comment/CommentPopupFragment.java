package com.hzlz.aviation.feature.video.ui.vshort.comment;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.SOURCE_FRAGMENT_PID;

import android.os.Bundle;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentFragment;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoShortCommentPopupLayoutBinding;

public class CommentPopupFragment extends BaseFragment<VideoShortCommentPopupLayoutBinding> {

    private VideoModel mVideoModel;

    private CommentFragment commentFragment;

    private final BackPressHandler mBackPressHandler = this::onBackPressed;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_short_comment_popup_layout;
    }

    @Override
    protected void initView() {
        mBinding.shortVideoCommentTopBar.setOnClickListener(v -> onBackPressed());
        mBinding.shortVideoCommentEmpty.setOnClickListener(v -> onBackPressed());

        mBinding.shortVideoCommentEdit.audioComment.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if(commentFragment==null){
                        return;
                    }
                    commentFragment.onClickShowAudioDialog();
                }
        );

        mBinding.shortVideoCommentEdit.commentTxt.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if(commentFragment==null){
                        return;
                    }
                    commentFragment.onClickShowTxtInputDialog();
                });

        mBinding.shortVideoCommentEdit.imageComment.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if(commentFragment==null){
                        return;
                    }
                    commentFragment.onClickShowImageDialog();
                });
    }

    private boolean checkCanComment() {
        Bundle bundle = getArguments();
        return bundle != null
                && !bundle.getBoolean(Constants.EXTRA_CAN_COMMENT, true);
    }

    @Override
    protected void bindViewModels() {
    }

    @Override
    protected void loadData() {
        if (!commentFragment.isAdded()) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.short_video_comment_frame, commentFragment)
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .show(commentFragment)
                    .commit();
        }
    }

    private boolean onBackPressed() {
        requireFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.push_top_in, R.anim.push_bottom_out)
                .remove(CommentPopupFragment.this).commit();
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
    }

    private void showCommentInputPanel() {
        boolean canComment =
                getArguments() == null || getArguments().getBoolean(Constants.EXTRA_CAN_COMMENT, true);
        if (!canComment) {
            showToast(R.string.video_comment_cant_comment);
            return;
        }
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.SHOW_COMMENT_INPUT_PANEL, CommentModel.class)
                .post(null);
    }

    public void setUp(VideoModel videoModel, Bundle bundle, boolean canComment) {
        mVideoModel = videoModel;
        if (bundle == null) {
            bundle = new Bundle();
        }
        commentFragment = new CommentFragment();
        bundle.putParcelable(Constants.EXTRA_VIDEO_MODEL, videoModel);
        bundle.putString(Constants.EXTRA_MEDIA_ID, videoModel.getId());
        bundle.putBoolean(Constants.EXTRA_CAN_COMMENT, canComment);
        bundle.putString(SOURCE_FRAGMENT_PID, videoModel.getPid());
        commentFragment.setArguments(bundle);
    }
}
