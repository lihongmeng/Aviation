package com.hzlz.aviation.kernel.media.databind;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.QuestionModel;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfoPid;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.FavoritePlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.LoadMorePlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.base.utils.StringUtils;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.MediaRuntime;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.player.MediaPlayManager;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * ????????????data bind
 */
public class MediaToolBarDataBind {

    /**
     * TAG
     */
    private static final String TAG = MediaToolBarDataBind.class.getSimpleName();

    /**
     * ?????????????????????
     */
    private boolean mIsDarkMode;

    private String mFragmentId;
    private int mPosition;
    private StatFromModel mStatFromModel;
    private MediaModel mMediaModel;

    public ObservableBoolean showDate = new ObservableBoolean();

    public ObservableBoolean showFollow = new ObservableBoolean();

    /**
     * ????????????????????????
     */
    public ObservableBoolean showSetTop = new ObservableBoolean();

    /**
     * ????????????????????????
     */
    public ObservableBoolean showCream = new ObservableBoolean();

    /**
     * ????????????????????????
     */
    public ObservableBoolean showCircle = new ObservableBoolean();
    public ObservableField<String> showCircleName = new ObservableField<>();

    /**
     * ??????????????????
     */
    public ObservableBoolean showTopic = new ObservableBoolean();
    public ObservableField<String> showTopicContent = new ObservableField<>();

    /**
     * ??????????????????
     */
    private ObservableField<String> authorSourceText = new ObservableField<>();
    /**
     * ???????????????????????????????????????????????????????????????
     */
    private SpannableStringBuilder authorSourceSpanner = null;

    /**
     * ??????-????????????
     */
    public ObservableField<String> qaQuestionContent = new ObservableField<>();


    public SpannableStringBuilder getAuthorSourceText() {
        if (authorSourceSpanner != null) {
            return authorSourceSpanner;
        } else {
            String s = TextUtils.isEmpty(authorSourceText.get()) ? "" : authorSourceText.get();
            return new SpannableStringBuilder(s);
        }
    }

    /**
     * tag??????
     */
    public ObservableField<GroupInfoPid> tag = new ObservableField<>();

    /**
     * ????????????
     *
     * @param isDarkMode ?????????????????????
     */
    public MediaToolBarDataBind(boolean isDarkMode, MediaModel model, String fragmentId,int position) {
        this.mPosition=position;
        this.mIsDarkMode = isDarkMode;
        this.mFragmentId = fragmentId;
        this.mMediaModel = model;

        BaseFragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
        if (fragment == null) {
            if (true) {
                Log.e(TAG, "currentFragment not Attached");
            }
            return;
        }
        String channelId = "";
        if (fragment instanceof MediaPageFragment) {
            channelId = ((MediaPageFragment) fragment).getChannelId();
        }
        mStatFromModel = new StatFromModel(model.getId(), fragment.getPid(), channelId, "", "");

        String pid = fragment.getPid();
        String label = fragment.getFragmentLabel();
        showDate.set(TextUtils.equals(pid, StatPid.PGC));
        showFollow.set(TextUtils.equals(pid, StatPid.PGC));
        if (model.getAnswerSquareId() > 0 && model.getAnswerSquareType() == 2 && model.getQuestionVO() != null) {
            qaQuestionContent.set(model.getQuestionVO().content);
        }
        updateCircleTopicInfo(label, model);

        updateAuthorSourceText(model);
    }

    public void onTopicClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }
        GroupInfoPid groupInfoPid = (GroupInfoPid) tag;
        if (TextUtils.isEmpty(groupInfoPid.pid) || groupInfoPid.groupInfo == null) {
            return;
        }
        PluginManager.get(CirclePlugin.class).startTopicDetail(view, groupInfoPid);
    }

    public void onCircleClick(View view) {
        Object tag = view.getTag();
        if (tag == null || mMediaModel == null) {
            return;
        }
        GroupInfoPid groupInfoPid = (GroupInfoPid) tag;
        if (TextUtils.isEmpty(groupInfoPid.pid) || groupInfoPid.groupInfo == null) {
            return;
        }
        PluginManager.get(CirclePlugin.class).startCircleDetail(view, groupInfoPid);
        GVideoSensorDataManager.getInstance().clickCommunity(mMediaModel);
    }

    public void updateCircleTopicInfo(String pid, MediaModel model) {
        GroupInfo groupInfo = model.getGroupInfo();
        if (groupInfo == null) {
            showSetTop.set(false);
            showCream.set(false);
            showCircle.set(false);
            showTopic.set(false);
        } else {

            tag.set(new GroupInfoPid(groupInfo, pid));

            switch (pid) {
                case StatPid.CIRCLE_DETAIL:
                    showSetTop.set(groupInfo.top);
                    showCream.set(groupInfo.essence);
                    showCircle.set(false);
                    setShowTopicName(groupInfo);
                    break;
                case StatPid.CIRCLE_MINE:
                case StatPid.HOME_RECOMMEND:
                case StatPid.HOME_FOLLOW:
                case StatPid.MINE:
                case StatPid.UGC:
                case StatPid.UGC_COMPOSITION:
                case StatPid.UGC_FAVORITE:
                case StatPid.VIDEO_AUDIO:
                case StatPid.HOME_FOLLOW_CONTENT_LIST:
                case StatPid.SEARCH:
                    showCircle.set(model.getAuthUgcReply() == null && model.getGroupInfo() != null);
                    showSetTop.set(false);
                    showCream.set(false);
                    setShowCircleName(groupInfo);
                    setShowTopicName(groupInfo);
                    break;
                case StatPid.PID_TOPIC_DETAIL:
                default:
                    showSetTop.set(false);
                    showCream.set(false);
                    showCircle.set(false);
                    showTopic.set(false);
                    break;
            }
        }
    }

    private void setShowTopicName(@NonNull GroupInfo groupInfo) {
        String topicName = groupInfo.getTopicName();
        if (TextUtils.isEmpty(topicName)) {
            showTopic.set(false);
        } else {
            showTopic.set(true);
            showTopicContent.set("# " + topicName + " #");
        }
    }

    private void setShowCircleName(@NonNull GroupInfo groupInfo) {
        String groupName = groupInfo.getGroupName();
        if (TextUtils.isEmpty(groupName)) {
            showCircle.set(false);
        } else {
            showCircle.set(true);
            showCircleName.set(groupName);
        }
    }

    public boolean showFavoriteItem() {
//        return mMediaModel != null && mMediaModel.showInPgc();
        return false;
    }

    public void updateAuthorSourceText(MediaModel mediaModel) {
        this.mMediaModel = mediaModel;
        if (mediaModel == null) {
            return;
        }
        if (mediaModel.getAnswerSquareId() > 0) {
            if (mediaModel.getAnswerSquareType() == 1) {
                authorSourceText.set("?????????????????????");
            } else {
                String name = "";
                QuestionModel questionModel = mediaModel.getQuestionVO();
                if (questionModel != null && questionModel.author != null) {
                    name = questionModel.author.getName();
                }
                name = "@" + StringUtils.showMaxLengthString(name, 8);
                String all = "?????????" + name + "?????????";
                authorSourceSpanner = SpannableStringUtils.setSpanColor(all, name, R.color.color_0063b9);
            }
        } else if (mediaModel.showMediaPageSource == MediaPageSource.PageSource.MINE) {
            //????????????????????????
            authorSourceText.set(DateUtils.friendlyTime2(mediaModel.getCreateDate()));
        } else {
            if (mediaModel.getAuthor()!=null) {
                authorSourceText = mediaModel.getAuthor().getObservable().getAuthenticationIntro();
            }
        }
    }

    /**
     * ??????????????????
     *
     * @reutrn ??????????????????
     */
    public Drawable getMoreItem() {
        int res = mIsDarkMode ? R.drawable.media_toolbar_more_dark : R.drawable.media_toolbar_more;
        return MediaRuntime.getAppContext().getResources().getDrawable(res);
    }

    /**
     * ??????????????????
     *
     * @reutrn ????????????
     */
    public Drawable getCommentItem() {
        int answerId = mMediaModel != null ? mMediaModel.getAnswerSquareId() : 0;
        int res = answerId > 0 ? R.drawable.ic_common_qa_comment : R.drawable.ic_common_comment;
        return MediaRuntime.getAppContext().getResources().getDrawable(res);
    }

    /**
     * ??????????????????
     *
     * @reutrn ????????????
     */
    public Drawable getCommentPgcItem() {
        int res = R.drawable.ic_common_comment;
        return MediaRuntime.getAppContext().getResources().getDrawable(res);
    }

    /**
     * ??????????????????
     *
     * @reutrn ????????????
     */
    public Drawable getShareItem() {
        int res = mIsDarkMode ? R.drawable.media_toolbar_share_dark : R.drawable.media_toolbar_share;
        return MediaRuntime.getAppContext().getResources().getDrawable(res);
    }

    /**
     * ????????????????????????
     *
     * @reutrn ??????????????????
     */
    public int getCommentTextColor() {
        int res = mIsDarkMode ? R.color.color_ffffff : R.color.color_333333;
        return MediaRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * ???????????????????????????
     *
     * @reutrn ?????????????????????
     */
    public int getCommentTextBackColor() {
        int res = mIsDarkMode ? R.color.color_1b1c1f : R.color.color_ffffff;
        return MediaRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * ????????????????????????
     *
     * @reutrn ??????????????????
     */
    public int getAuthorNameColor() {
        int res = mIsDarkMode ? R.color.color_ffffff : R.color.color_4c4c4c;
        return MediaRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * ????????????????????????
     *
     * @reutrn ??????????????????
     */
    public int getAuthorSourceColor() {
        int res = mIsDarkMode ? R.color.color_7f7f7f : R.color.color_999999;
        return MediaRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * ??????????????????
     */
    public void onAttentionClick(View view) {
        Context context = view.getContext();
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(mMediaModel.getPid()),
                    ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(context)) {
            return;
        }
        ObservableBoolean followObservable = mMediaModel.getAuthor().getObservable().getIsFollow();
        accountPlugin.getFollowRepository()
                .followAuthor(mMediaModel.getAuthor().getId(), mMediaModel.getAuthor().getType(),
                        mMediaModel.getAuthor().getName(),
                        !followObservable.get()).subscribe(
                new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        AuthorModel author = mMediaModel.getAuthor();
                        GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(), author.getId(),
                                author.getName(), author.getType(), throwable.getMessage());
                    }
                });
    }

    /**
     * PGC????????????
     *
     * @param view ????????????
     */
    public void onPGCClick(View view) {
        if (mStatFromModel != null) {
            //??????????????????????????????id???????????????id???????????????
            if (TextUtils.equals(mStatFromModel.pid, StatPid.MINE_COMMENT) ||
                    ((TextUtils.equals(mStatFromModel.pid, StatPid.UGC_COMPOSITION) || (TextUtils.equals(mStatFromModel.pid, StatPid.UGC_FAVORITE))
                            && TextUtils.equals(mMediaModel.getAuthor().getId(), PluginManager.get(AccountPlugin.class).getUserId())))) {
                return;
            }
        }
        PluginManager.get(AccountPlugin.class).startPgcActivity(view, mMediaModel.getAuthor());
    }

    /**
     * ????????????
     *
     * @param view
     */
    public void onFavoriteClick(View view) {
        Context context = view.getContext();
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(mMediaModel.getPid()),
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(context)) {
            return;
        }

        if (mMediaModel.isQaAnswerType()) {
            ObservableBoolean praiseObservable = mMediaModel.getObservable().getIsPraise();
            PluginManager.get(VideoPlugin.class).commentPraise(mMediaModel.getCommentId(), !praiseObservable.get(),
                    new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                        @Override
                        protected void onRequestData(Boolean aBoolean) {
                            super.onRequestData(aBoolean);
                            praiseObservable.set(aBoolean);
                        }

                        @Override
                        protected void onRequestError(Throwable throwable) {
                            super.onRequestError(throwable);
                        }
                    });
        } else {
            ObservableBoolean favorObservable = mMediaModel.getObservable().getIsFavor();
            PluginManager.get(FavoritePlugin.class).getFavoriteRepository()
                    .favoriteMedia(mMediaModel.getId(), !favorObservable.get())
                    .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {

                        @Override
                        protected void onRequestData(Object o) {
                            super.onRequestData(o);
                            if (mStatFromModel != null) {
                                GVideoSensorDataManager.getInstance().favoriteContent(mMediaModel,
                                        mStatFromModel.pid, !favorObservable.get(), null);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            ToastUtils.showShort(throwable.getMessage());
                            if (mStatFromModel != null) {
                                GVideoSensorDataManager.getInstance().favoriteContent(mMediaModel,
                                        mStatFromModel.pid, !favorObservable.get(), throwable.getMessage());
                            }
                        }
                    });
        }

    }

    /**
     * ??????????????????
     */
    public void onShareClick(View view) {
        ObservableBoolean favorObservable = mMediaModel.getObservable().getIsFavor();
        boolean followObservable = mMediaModel.getAuthor() != null && mMediaModel.getAuthor().getObservable().getIsFollow().get();
        String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + mMediaModel.getId();
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setVideoModel(mMediaModel)
                .setShowShare(mMediaModel.mediaStatus != null && mMediaModel.mediaStatus == 3)
                .setReportUrl(reportUrl)
                .setFavorite(favorObservable.get())
                .setFollow(followObservable)
                .setShowFollow(mMediaModel.getAuthor() != null)
                .setShowDelete(mMediaModel.getAuthor() != null && mMediaModel.getAuthor().isSelf())
                .setShowCreateBill(true)
                .setContentIsComment(mMediaModel.isQaAnswerType())
                .build();
        PluginManager.get(SharePlugin.class)
                .showShareDialog(view.getContext(), mIsDarkMode, shareDataModel, mStatFromModel);
    }

    /**
     * ??????????????????
     */
    public void onMoreClick(View view) {
        onShareClick(view);
    }

    /**
     * ??????????????????
     */
    public void onCommentClick(View view) {
        handleNavigateToDetail(view.getContext(), mMediaModel, true, false);
        int answerId = mMediaModel != null ? mMediaModel.getAnswerSquareId() : 0;
        if (answerId > 0) {
            GVideoSensorDataManager.getInstance().clickCommentButton(mMediaModel);
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void onItemClick(View view) {
        handleNavigateToDetail(view.getContext(), mMediaModel, false, false);
    }

    /**
     * ??????????????????
     */
    public void onQualityClick(View view) {
        handleNavigateToDetail(view.getContext(), mMediaModel, true, true);
    }

    /**
     * ??????????????????????????????
     *
     * @param context         ???????????????
     * @param model           ?????????????????????feed model
     * @param needShowComment ????????????????????????
     */
    public void handleNavigateToDetail(Context context, MediaModel model, boolean needShowComment, boolean isNeedShowCommentPosition) {
        if (model == null) {
            return;
        }
        if (model.showMediaPageSource == MediaPageSource.PageSource.SEARCH) {
            GVideoSensorDataManager.getInstance().clickSearchResult(
                    model.getId(),
                    model.getTitle(),
                    "??????",
                    model.searchWord,
                    mPosition,
                    model.hintSearchWord
            );
        }
        Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
        Bundle extras = new Bundle();

        extras.putString(LoadMorePlugin.KEY_MODULE, LoadMorePlugin.MODULE_FEED);
        extras.putString(FeedPlugin.KEY_FRAGMENT_UUID, mFragmentId);

        // ???????????????Fragment???Pid
        if (fragment != null) {
            extras.putString(VideoPlugin.EXTRA_FROM_PID, ((BaseFragment) fragment).getPid());
        }

        if (isNeedShowCommentPosition && model.qualityComment != null) {
            long primaryId = 0L;
            try {
                primaryId = Long.parseLong(model.qualityComment.primaryId);
            } catch (NumberFormatException exception) {
                LogUtils.d("??????????????????");
            }
            extras.putLong(VideoPlugin.EXTRA_START_COMMENT_ID, primaryId);
            extras.putInt(VideoPlugin.EXTRA_START_COMMENT_TYPE, 0);
        }

        // ????????????????????????????????????????????????
        extras.putInt(VideoPlugin.EXTRA_START,
                needShowComment ? VideoPlugin.START_COMMENT : VideoPlugin.START_DEFAULT);

        if (fragment instanceof MediaPageFragment) {
            String pid = ((MediaPageFragment) fragment).getPid();
            String channelId = ((MediaPageFragment) fragment).getChannelId();
            extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
            extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
        }

        MediaPlayManager.getInstance().onChangeToDetail(model);
        if (model.showInTVCollection()){
            try {
                PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(context,
                                Long.parseLong(model.getColumnId()), Long.parseLong(model.getId()), model.getPid());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, model, extras);
        }
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    public @ColorInt
    int getFollowedColor() {
        if (mIsDarkMode) {
            return MediaRuntime.getAppContext().getResources().getColor(R.color.color_8a8c99);
        }
        return MediaRuntime.getAppContext().getResources().getColor(R.color.color_a1a4b3);
    }

}
