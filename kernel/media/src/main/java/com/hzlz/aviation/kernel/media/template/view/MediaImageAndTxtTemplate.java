package com.hzlz.aviation.kernel.media.template.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.JsonObject;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.liteav.AviationSoundView;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaToolBarDataBind;
import com.hzlz.aviation.kernel.media.databind.MediaVideoDataBind;
import com.hzlz.aviation.kernel.media.databinding.MediaTplImageTxtBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/4/6
 * desc : 图文
 **/
public class MediaImageAndTxtTemplate extends MediaBaseTemplate {

    private MediaTplImageTxtBinding mBinding;
    protected MediaVideoDataBind mFeedVideoDataBind;
    protected AviationSoundView mPlayerView;

    private long mPlayStart;
    private long mPlayDuration;
    private long mVideoTime;
    private StatFromModel mStatFromModel;

    //用户点击才能播放
    private boolean isUserClick = false;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public MediaImageAndTxtTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.media_tpl_image_txt, parent, false);
        topBinding = mBinding.feedToolbarTop;
        bottomBinding = mBinding.feedToolbarInclude;

    }

    @Override
    public TextView getTitleView() {
        return mBinding.imageTxtLayout.content.getContentText();
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId,int position) {
        if (!mediaModel.isImageAudioTxt()) {
            return;
        }
        super.update(mediaModel, isDarkMode, fragmentId,position);

        mBinding.imageTxtLayout.setVariable(BR.feedModel, mMediaModel);
        mBinding.imageTxtLayout.setVariable(BR.toolBind, new MediaToolBarDataBind(isDarkMode, mediaModel, fragmentId,position));

        if (topBinding != null && mediaModel.getAuthor() != null) {
            topBinding.toolbarNameText.setText(mediaModel.getAuthor().getName());
        }

        if (mediaModel.getMediaType() == MediaType.AUDIO_TXT) {

            if (mFeedVideoDataBind == null) {
                mFeedVideoDataBind = new MediaVideoDataBind(this,mediaModel, fragmentId,position);
            }

            mPlayerView = mBinding.imageTxtLayout.soundView;
            mPlayerView.setSoundText(mMediaModel.getSoundContent());
            mPlayerView.setTotalSecondTime(mMediaModel.getLength());
            mPlayerView.setVideoModel(mMediaModel);

            List<String> mediaUrlArray = mMediaModel.getMediaUrls();
            if (mediaUrlArray == null || mediaUrlArray.isEmpty()) {
                mPlayerView.setSoundUrl("");
            } else {
                mPlayerView.setSoundUrl(mMediaModel.getMediaUrls().get(0));
            }

            mPlayerView.setPlayOnClickListener(view -> {
                isUserClick = true;
                if (mFeedVideoDataBind == null) {
                    mFeedVideoDataBind = new MediaVideoDataBind(this,mediaModel, fragmentId,position);
                }
                mFeedVideoDataBind.dealMediaClick(mPlayerView, mMediaModel);
            });

        }


        mBinding.imageTxtLayout.content.setText(mediaModel.getContent());

        mBinding.rootLayout.setOnClickListener(view -> handleNavigateToDetail(false));

        getRootLayout().requestLayout();
        getRootLayout().invalidate();

        BaseFragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
        if (fragment == null) {
            return;
        }
        String pid = "";
        String channelId = "";
        if (fragment instanceof MediaPageFragment) {
            channelId = ((MediaPageFragment) fragment).getChannelId();
            pid = fragment.getPid();
        }
        mStatFromModel = new StatFromModel(mMediaModel.getId(), pid, channelId, "", "");

        updateQuestionLayout(mBinding.imageTxtLayout.feedLayoutQa.qaContent);
        updateQualityComment(mediaModel);
        updateLinkLayout(mediaModel);
    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    @Override
    protected View getPlayView() {
        return mPlayerView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPlayerView != null) {
            mPlayerView.stop();
        }
        mFeedVideoDataBind = null;
        statPlay(false);
    }

    @Override
    public void play() {
        super.play();
        if (mPlayerView != null && isUserClick) {
            mPlayerView.start();
            isUserClick = false;
        }
    }

    @Override
    public void resume() {
        super.resume();
        if (mPlayerView != null) {
            mPlayerView.resume();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (mPlayerView != null) {
            mPlayerView.stop();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (mPlayerView != null) {
            mPlayerView.stop();
        }
    }

    @Override
    public void onChangeToDetail() {
        stop();
        statPlay(false);
    }

    @Override
    public void onBackFeed() {

    }

    @Override
    public void mute(boolean value) {

    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    protected void statPlay(boolean finish) {
        String channelId = mStatFromModel != null ? mStatFromModel.channelId : "";
        if (mPlayDuration > 1000 && !TextUtils.isEmpty(channelId)) {
            String isFinish = finish ? "1" : "0";
            JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStatFromModel);
            ds.addProperty(StatConstants.DS_KEY_PLAY_DURATION, String.valueOf(mPlayDuration));
            ds.addProperty(StatConstants.DS_KEY_VIDEO_TIME, String.valueOf(mVideoTime));
            ds.addProperty(StatConstants.DS_KEY_IS_FINISH, isFinish);
            StatEntity statEntity = StatEntity.Builder.aStatEntity()
                    .withEv(StatConstants.EV_PLAY)
                    .withDs(ds.toString())
                    .withPid(mStatFromModel != null ? mStatFromModel.pid : "")
                    .build();
            GVideoStatManager.getInstance().stat(statEntity);
        }

        mPlayStart = 0;
        mPlayDuration = 0;
        mVideoTime = 0;
    }
}
