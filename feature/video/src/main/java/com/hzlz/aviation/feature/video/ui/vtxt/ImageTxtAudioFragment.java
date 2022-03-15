package com.hzlz.aviation.feature.video.ui.vtxt;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.NEED_HIDE_COMMUNITY_LAYOUT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.video.ui.BaseCommentFragment;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentAdapter;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.share.FavoriteChangeModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.model.video.VideoObservable;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutBottomNoDataBindBinding;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.ImageTxtAudioFragmentBinding;
import com.hzlz.aviation.feature.video.databinding.ImageTxtAudioInfoBinding;

/**
 * @author huangwei
 * date : 2021/4/9
 * desc : 图、文、语音 动态详情页
 **/
public class ImageTxtAudioFragment extends BaseCommentFragment<ImageTxtAudioFragmentBinding> {

    private ImageTxtAudioViewModel viewModel;

    private ImageTxtAudioInfoBinding topBinding;

    private MediaToolbarLayoutBottomNoDataBindBinding toolBarBind;

    @Override
    protected int getLayoutId() {
        return R.layout.image_txt_audio_fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbarTitle(getString(R.string.moment_detail));

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
        mRefreshLayout.setEnableFooterFollowWhenNoMoreData(false);

        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(
                this, o -> finishActivity()
        );
    }

    @SuppressWarnings("DanglingJavadoc")
    @SuppressLint("SetTextI18n")
    @Override
    protected void bindViewModels() {
        super.bindViewModels();
        LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View topView = inflaters.inflate(R.layout.image_txt_audio_info, null);

        topBinding = DataBindingUtil.bind(topView);
        if (topBinding == null) {
            return;
        }

        ((CommentAdapter) mAdapter).addHeaderView(topView);

        viewModel = bingViewModel(ImageTxtAudioViewModel.class);

        /**
         * 某些场景下需要借助上层传递进来的videoModel
         * 这个videoModel数据保存在父类中，父类负责从Bundle中抽取出来
         * {@link BaseCommentFragment#videoModel}
         * 所以这里需要从父类借用
         */
        viewModel.videoModel = videoModel;

        viewModel.setStatFromModel(getStat());
        viewModel.loadData(mediaId);

        mBinding.inputPanel.setViewModel(commentViewModel);
        topBinding.setViewModel(viewModel);

        initToolBarBottom(topView);

        viewModel.videoModelLiveData.observe(this, o -> {
            viewModel.videoModel.setPid(getPid());

            this.videoModel = viewModel.videoModel;
            ((CommentAdapter) mAdapter).setStatFromModel(getStat(), videoModel);
            commentViewModel.setStatFromModel(getStat(), videoModel);
            topBinding.setVideoModel(videoModel);
            AuthorModel author = videoModel.getAuthor();
            if (author != null) {
                topBinding.setAuthorObservable(author.getObservable());
            }
            topBinding.setVideoObservable(videoModel.getObservable());

            if (videoModel.isAudioTxt()) {
                topBinding.soundView.setSoundUrl(videoModel.getMediaUrls().get(0));
                topBinding.soundView.setTotalSecondTime(videoModel.getLength());
                topBinding.soundView.setSoundText(videoModel.getSoundContent());
                topBinding.soundView.setVideoModel(videoModel);
                topBinding.soundView.setPlayOnClickListener(view -> {
                    AudioPlayManager.getInstance().start(topBinding.soundView);
                });
            }

            GroupInfo groupInfo = videoModel.getGroupInfo();
            StatFromModel statFromModel = commentViewModel.getmStat();

            // 上一个页面如果是话题详情页，不需要展示话题
            if (groupInfo == null
                    || TextUtils.isEmpty(groupInfo.getTopicName())
                    || StatPid.PID_TOPIC_DETAIL.equals(statFromModel.fromPid)) {
                toolBarBind.topic.setVisibility(View.GONE);
            } else {
                toolBarBind.topic.setVisibility(View.VISIBLE);
                // 点击话题
                toolBarBind.topic.setOnClickListener(v -> viewModel.onTopicClick(v, getPid()));
            }

            if (TextUtils.isEmpty(videoModel.outShareUrl)) {
                toolBarBind.linkLayout.setVisibility(View.GONE);
            } else {
                toolBarBind.linkLayout.updateLinkTitle(TextUtils.isEmpty(videoModel.outShareTitle)
                        ? ResourcesUtils.getString(R.string.share_link_default_tip) : videoModel.outShareTitle);
                toolBarBind.linkLayout.updateLinkValue(videoModel.outShareUrl);

                // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
                // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
                // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
                toolBarBind.linkLayout.setVisibility(View.VISIBLE);
            }

            if (this.viewModel.videoModel.getValueToBundle(NEED_HIDE_COMMUNITY_LAYOUT)
                    || groupInfo == null
            ) {
                topBinding.circleLayout.setVisibility(View.GONE);
                topBinding.circleLayoutTopDivider.setVisibility(View.GONE);
            } else {
                topBinding.circleLayout.setVisibility(View.VISIBLE);
                topBinding.circleLayoutTopDivider.setVisibility(View.VISIBLE);
            }

            // 更新MediaToolBarBottomNoBindStatus
            // 之后会自动触发MediaToolBarBottomNoBindStatus观测者的处理
            viewModel.updateToolBarStatus(videoModel);

            mRecyclerView.postDelayed(checkListItemRunnable, 300);


        });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN, String.class).observe(this,
                s -> {
                    if (isVisible()) {
                        loadData();
                    }
                });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT, String.class).observe(this,
                s -> {
                    if (isVisible()) {
                        loadData();
                    }
                });

        //收藏状态变化
        GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class)
                .observe(this, favoriteChangeModel -> {
                    MediaToolBarBottomNoBindStatus statusData = viewModel.status.getValue();
                    if (statusData == null) {
                        return;
                    }
                    statusData.isFlavor = (favoriteChangeModel != null && favoriteChangeModel.favorite);
                    viewModel.status.setValue(statusData);
                });

        viewModel.status.observe(this, status -> {
            if (status.isFlavor) {
                toolBarBind.toolbarFavoriteImg.setImageResource(R.drawable.media_toolbar_favorite_checked);
            } else {
                toolBarBind.toolbarFavoriteImg.setImageResource(R.drawable.media_toolbar_favorite_unchecked);
            }
            StatFromModel statFromModel = commentViewModel.getmStat();
            if (statFromModel != null) {
                if (!StatPid.PID_TOPIC_DETAIL.equals(statFromModel.fromPid)) {
                    toolBarBind.topic.setText("# " + status.topicName + " #");
                }
            }

            toolBarBind.toolbarCommentNumText.setText(
                    VideoObservable.getCommentCountText(videoModel.getReviews(), null, ""));

        });

        viewModel.needFinish.observe(
                this,
                needFinish -> {
                    finishActivity();
                }
        );

    }

    /**
     * 对ToolBar初始化点击事件
     *
     * @param topView ToolBar父布局
     */
    private void initToolBarBottom(View topView) {
        toolBarBind = DataBindingUtil.bind(topView.findViewById(R.id.media_toolbar_layout_bottom));
        if (toolBarBind == null) {
            return;
        }
        // 喜欢按钮
        toolBarBind.toolbarFavoriteImg.setOnClickListener(v -> viewModel.onFavorClicked(v));
        // 评论按钮
        toolBarBind.toolbarChatImg.setOnClickListener(v -> commentViewModel.onClickShowTxtInputDialog(v));
        // 更多按钮
        toolBarBind.toolbarMoreImg.setOnClickListener(v -> viewModel.onMoreClick(v));
    }


    @Override
    public String getPid() {
        return StatPid.DETAIL_COMPOSITION;
    }

    @Override
    public VideoModel getVideoModel() {
        return videoModel;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            LogUtils.e(data.getDataString());
        }
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null
                || viewModel.videoModel == null) {
            return null;
        }
        GroupInfo groupInfo = viewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
            return null;
        }
        return groupInfo.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (viewModel == null
                || viewModel.videoModel == null) {
            return "";
        }
        GroupInfo groupInfo = viewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
            return "";
        }
        return groupInfo.tenantName;
    }
}
