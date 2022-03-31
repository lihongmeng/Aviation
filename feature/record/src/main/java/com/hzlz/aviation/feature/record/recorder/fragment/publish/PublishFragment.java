package com.hzlz.aviation.feature.record.recorder.fragment.publish;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_ADD;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_REPLACE;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.SINGLE_PLACE;
import static com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.AUTHOR_MODEL;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.GATHER_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.IS_QA;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TOPIC_DETAIL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzlz.aviation.feature.record.RecordPluginImpl;
import com.hzlz.aviation.feature.record.databinding.LayoutPublishBinding;
import com.hzlz.aviation.feature.record.recorder.dialog.LinkAnalysisDialog;
import com.hzlz.aviation.feature.record.recorder.dialog.ProcessVideoDialog;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.feature.record.recorder.model.ChooseImageListModel;
import com.hzlz.aviation.feature.record.recorder.model.ChooseVideoModel;
import com.hzlz.aviation.feature.record.recorder.view.soundrecord.AviationSoundRecordView;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.model.PublishLinkWhiteListItem;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.utils.VideoUtils;
import com.hzlz.aviation.kernel.base.view.PublishImageRecyclerView;
import com.hzlz.aviation.kernel.base.view.floatwindow.FloatingView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.RegexUtil;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.hzlz.aviation.library.util.SoftKeyBoardListener;
import com.hzlz.aviation.library.util.VibratorUtil;
import com.hzlz.aviation.feature.record.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PublishFragment extends BaseFragment<LayoutPublishBinding> {

    // 发布过程中的Loading弹窗
    private ProcessVideoDialog loadingDialog;

    // 识别链接的弹窗
    private LinkAnalysisDialog linkAnalysisDialog;

    // RecordPlugin
    private RecordPlugin recordPluginImpl;

    // PublishViewModel
    private PublishViewModel viewModel;

    private boolean isQaType;

    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_publish;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        isQaType = bundle.getBoolean(IS_QA);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {

        FloatingView.get().remove();

        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }


        mBinding.publish.setBackgroundResource(R.drawable.icon_publish_disable);
        mBinding.publish.setEnabled(false);

        // 用于启动Record其他功能
        recordPluginImpl = new RecordPluginImpl();

        // 网络通信过程中的Loading弹窗
        loadingDialog = new ProcessVideoDialog(activity);

        // 录音Icon按钮
        mBinding.soundRecordIcon.setTag(true);
        mBinding.soundRecordIcon.setOnClickListener(v -> {
            //停止后台音频播放
            GVideoEventBus.get(Constant.EVENT_MSG.AUDIO_BG_NEED_PAUSE).post(null);
            hideSoftInput();
            if (!(boolean) v.getTag()) {
                toastAudioButtonClickTip();
                return;
            }
            if (mBinding.soundRecord.getVisibility() == VISIBLE) {
                mBinding.soundRecord.setVisibility(GONE);
            } else {
                mBinding.soundRecord.setVisibility(VISIBLE);
            }
        });

        mBinding.linkLayout.setDeleteClickListener(v -> {
            viewModel.outShareTitle = "";
            viewModel.outShareUrl = "";
            mBinding.linkLayout.setVisibility(GONE);
            updateCanPublishStatus();
        });

        // 录音结果回调
        mBinding.soundRecord.setCompleteListener(new AviationSoundRecordView.OperationListener() {

            @Override
            public void startRequestPermission() {
                hideSoftInputNoToken();
            }

            @Override
            public void start() {
                VibratorUtil.getInstance(activity).vibrate(500);

                setAudioRecordButtonEnableStatus(false);
                setImageButtonEnableStatus(false);
                setVideoButtonEnableStatus(false);

            }

            @Override
            public void stop() {
            }

            @Override
            public void errorOnBackgroundThread(String message, boolean isFirstCallBack) {
                //noinspection ConstantConditions
                if (mBinding == null
                        || mBinding.soundRecord == null
                        || !isFirstCallBack) {
                    return;
                }
                mBinding.soundRecord.post(() -> showToast(getString(R.string.record_failed)));
            }

            @Override
            public void result(String soundFilePath, String soundText, long operationTime) {
                if (operationTime < 1000) {
                    showToast(R.string.sound_time_too_short);
                    return;
                }
                if (!TextUtils.isEmpty(soundFilePath)) {
                    mBinding.soundView.setSoundText(soundText);
                    mBinding.soundView.setTotalTime(operationTime);
                    mBinding.soundView.setSoundUrl(soundFilePath);
                    mBinding.soundView.setDeleteOnClickListener(v -> soundChange("", "", 0));
                    soundChange(soundFilePath, soundText, operationTime);
                } else {
                    soundChange("", "", 0);
                }
            }

            @Override
            public void onLoadingStart(String loadingMessage) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                if (loadingDialog == null) {
                    loadingDialog = new ProcessVideoDialog(activity);
                }
                loadingDialog.setTipText(loadingMessage);
                loadingDialog.show();
            }

            @Override
            public void onLoadingEnd() {
                if (loadingDialog == null) {
                    return;
                }
                loadingDialog.dismiss();
            }

        });

        // 图片Icon按钮
        mBinding.picIcon.setTag(true);
        mBinding.picIcon.setOnClickListener(v -> {
            hideSoftInput();
            if (!(boolean) v.getTag()) {
                toastImageButtonClickTip();
                return;
            }
            recordPluginImpl.startImageSelectFragment(
                    activity,
                    viewModel.imagePathList.get(),
                    BATCH_ADD,
                    -1
            );
        });

        mBinding.selectCommunity.setOnClickListener(v -> {
            if (viewModel == null) {
                return;
            }
            viewModel.onSelectCommunityClick(v);
        });

        mBinding.circleTopicLayout.setOnClickListener(view -> {
            if (viewModel == null) {
                return;
            }
            viewModel.onTopicClick(view);
        });

        // 图片Icon按钮
        mBinding.linkIcon.setTag(true);
        mBinding.linkIcon.setOnClickListener(v -> {
            hideSoftInput();
            if (!(boolean) v.getTag()) {
                toastLinkButtonClickTip();
                return;
            }
            if (linkAnalysisDialog == null) {
                linkAnalysisDialog = new LinkAnalysisDialog(activity, contentUrl -> {

                    if (TextUtils.isEmpty(contentUrl)) {
                        showToast(getString(R.string.link_address_is_null));
                        return;
                    }

                    // 从输入的内容中匹配URL
                    String result = RegexUtil.match(contentUrl, Constant.REGEX_PATTERN.URL);

                    if (TextUtils.isEmpty(result)) {
                        String errorMessage = getString(R.string.link_address_illegal);
                        GVideoSensorDataManager.getInstance().addLinkFail(contentUrl, errorMessage);
                        showToast(errorMessage);
                        return;
                    }

                    // 抽取出主域名
                    String primaryDomain = "";
                    try {
                        URI uri = new URI(result);
                        primaryDomain = uri.getHost();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    boolean hasOnce = false;
                    for (PublishLinkWhiteListItem publishLinkWhiteListItem : StaticParams.whiteListItemArrayList) {
                        if (!result.contains(publishLinkWhiteListItem.pattern)) {
                            continue;
                        }
                        hasOnce = true;
                        break;
                    }
                    if (!hasOnce && !StaticParams.whiteListItemArrayList.isEmpty()) {
                        String errorMessage = getString(R.string.can_not_support_share_that_address);
                        GVideoSensorDataManager.getInstance().addLinkFail(
                                TextUtils.isEmpty(primaryDomain) ? result : primaryDomain, errorMessage);
                        showToast(errorMessage);
                        return;
                    }

                    showNetworkDialog("正在解析...");
                    viewModel.hasDealReceivedError = false;
                    mBinding.web.setTag(
                            TextUtils.isEmpty(primaryDomain) ? result : primaryDomain);
                    mBinding.web.loadUrl(result);
                });
            }
            linkAnalysisDialog.show();
        });

        // 图片列表
        mBinding.imageList.setMaxShowCount(VideoChooseHelper.IMAGE_NUM_LIMIT);
        mBinding.imageList.setOperationListener(new PublishImageRecyclerView.OperationListener() {
            @Override
            public void delete(int index) {
                List<String> imageList = viewModel.imagePathList.get();
                if (imageList == null || imageList.isEmpty()) {
                    updateCanPublishStatus();
                    return;
                }
                imageList.remove(index);
                mBinding.imageList.setImagesData(imageList);
                if (imageList.isEmpty()) {
                    updateCanPublishStatus();
                } else {
                    setImageButtonEnableStatus(imageList.size() != DEFAULT_MAX_SELECT_IMAGE_NUM);
                }
            }

            @Override
            public void replace(int index) {
                recordPluginImpl.startImageSelectFragment(
                        activity,
                        viewModel.imagePathList.get(),
                        SINGLE_PLACE,
                        index
                );
            }

            @Override
            public void add() {
                recordPluginImpl.startImageSelectFragment(
                        activity,
                        viewModel.imagePathList.get(),
                        BATCH_ADD,
                        -1
                );
            }
        });

        // 视频Icon按钮
        mBinding.videoIcon.setVisibility(VISIBLE);
        mBinding.videoIcon.setTag(true);
        mBinding.videoIcon.setOnClickListener(v -> {
            GVideoEventBus.get(Constant.EVENT_MSG.AUDIO_BG_NEED_PAUSE).post(null);
            hideSoftInput();
            if (!(boolean) v.getTag()) {
                toastVideoButtonClickTip();
                return;
            }
            if (mBinding.soundRecord.getVisibility() == VISIBLE) {
                mBinding.soundRecord.setVisibility(GONE);
            }
            recordPluginImpl.startVideoRecordFragment(activity);
        });

        // 视频的删除按钮
        mBinding.videoViewDelete.setOnClickListener(V -> {
            viewModel.videoPath = "";
            updateCanPublishStatus();
        });

        // 语音View不需要显示删除按钮
        mBinding.soundView.isShowDelete(true);
        // 语音View不需要转文字功能
        mBinding.soundView.isEnableTextChange(false);

        // 文本编辑框内容变化监听
        mBinding.edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCanPublishStatus();
            }
        });

        // 触摸到文字输入框，确保录音控件隐藏
        mBinding.edittext.setOnTouchListener((v, event) -> {
            if (mBinding.soundRecord.getVisibility() == VISIBLE) {
                mBinding.soundRecord.setVisibility(GONE);
            }
            return false;
        });

        // 根据键盘的弹起与隐藏，改变图片列表的高度
        new SoftKeyBoardListener(activity).setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                LogUtils.d("弹起键盘");
                ViewGroup.LayoutParams layoutParams = mBinding.linkImageVideoSound.getLayoutParams();
                layoutParams.height = SizeUtils.dp2px(120);
                mBinding.linkImageVideoSound.setLayoutParams(layoutParams);
                mBinding.shadow.setVisibility(GONE);
            }

            @Override
            public void keyBoardHide(int height) {
                LogUtils.d("隐藏键盘");
                ViewGroup.LayoutParams layoutParams = mBinding.linkImageVideoSound.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mBinding.linkImageVideoSound.setLayoutParams(layoutParams);
                mBinding.shadow.setVisibility(VISIBLE);
            }
        });

        // 添加外链时，使用WebView加载链接，获取相关数据
        mBinding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadWebTitleData(view.getTitle(), url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideNetworkDialog();

                if (viewModel == null || viewModel.hasDealReceivedError) {
                    return;
                }
                Object object = view.getTag();
                if (object == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GVideoSensorDataManager.getInstance().addLinkFail(String.valueOf(object), error.getDescription());
                } else {
                    GVideoSensorDataManager.getInstance().addLinkFail(String.valueOf(object), "未知错误");
                }
                viewModel.hasDealReceivedError = true;
            }
        });

        mBinding.qaTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateCanPublishStatus();
            }
        });

    }

    private void loadWebTitleData(String title, String url) {
        AsyncUtils.runOnUIThread(() -> {
            hideNetworkDialog();
            linkAnalysisDialog.dismiss();
            LogUtils.i("Html Analysis title-->>", title);
            LogUtils.i("Html Analysis url-->>", url);

            viewModel.outShareTitle = (TextUtils.isEmpty(title))
                    ? getString(R.string.share_link_default_tip) : title;
            viewModel.outShareUrl = url;

            mBinding.linkLayout.updateLinkTitle(viewModel.outShareTitle);
            mBinding.linkLayout.updateLinkValue(viewModel.outShareUrl);

            // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
            // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
            // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
            mBinding.linkLayout.setVisibility(VISIBLE);

            updateCanPublishStatus();
        });
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(PublishViewModel.class);
        mBinding.setViewModel(viewModel);

        Bundle arguments = getArguments();
        if (arguments != null) {
            viewModel.circle = arguments.getParcelable(CIRCLE);
            viewModel.topicDetail = arguments.getParcelable(TOPIC_DETAIL);
            viewModel.isQaType = arguments.getBoolean(IS_QA);
            viewModel.authorModel = arguments.getParcelable(AUTHOR_MODEL);
            viewModel.selectTabId = arguments.getLong(GATHER_ID);
        }

        // 先检查上层传递进来的社区信息，以及历史记录的社区信息，做一些判断
        // 再通过LiveData通知Fragment更新
        viewModel.checkHistoryCommunity();

        updateTopicBtnState();
        updateTopicLayout();
        showSoftInput();

        String key = (viewModel.isQaType ? "qa" : "normal") + getClass().getName();
        viewModel.text.set(PluginManager.get(RecordPlugin.class).getInputCacheText(key));
        if (isQaType) {
            String titleKey = "title" + getClass().getName();
            viewModel.qaTitle.set(PluginManager.get(RecordPlugin.class).getInputCacheText(titleKey));
        }

        viewModel.dealKeyboard.observe(
                this, isShow -> {
                    if (isShow) {
                        showSoftInput();
                    } else {
                        hideSoftInput();
                    }
                }
        );

        viewModel.finishActivity.observe(this, o -> finishActivity());

        viewModel.updateCommunityLayout.observe(
                this, o -> {
                    Context context = getContext();
                    if (context == null || viewModel == null || isQaType) {
                        return;
                    }
                    Bundle bundle = getArguments();
                    boolean isCircleNull = viewModel.circle == null;
                    boolean bundleHasNoCircleData = (bundle == null || bundle.get(CIRCLE) == null);

                    Glide.with(this)
                            .load(isCircleNull
                                    ? ContextCompat.getDrawable(context, R.drawable.home_tab_icon_community)
                                    : viewModel.circle.imageVO.url
                            )
                            .centerCrop()
                            .into(mBinding.selectCommunityIcon);

                    mBinding.selectCommunityName.setText(
                            isCircleNull
                                    ? getString(R.string.select_community)
                                    : viewModel.circle.name);

                    // 如果Bundle里面有圈子相关的数据，说明上层业务限定了改圈子，不能再更改，需要禁用点击
                    mBinding.selectCommunity.setClickable(bundleHasNoCircleData);

                    mBinding.selectCommunityTip.setText(
                            isCircleNull && bundleHasNoCircleData
                                    ? getString(R.string.select_community_tip)
                                    : ""
                    );
                    mBinding.selectCommunityTip.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            bundleHasNoCircleData
                                    ? ContextCompat.getDrawable(context, R.drawable.icon_arrow_right_text)
                                    : null,
                            null
                    );

                    updateTopicBtnState();
                    updateTopicLayout();

                }
        );

        updateCanPublishStatus();
    }

    /**
     * 更新话题相关按钮的点击是否启用
     */
    private void updateTopicBtnState() {
        if (viewModel.isQaType) {
            return;
        }
        mBinding.circleTopicLayout.setClickable(viewModel.circle == null || viewModel.topicDetail == null);
    }

    private void updateTopicLayout() {
        Context context = getContext();
        if (context == null
                || viewModel == null
                || viewModel.isQaType) {
            mBinding.circleTopicLayout.setVisibility(GONE);
            return;
        }

        if (viewModel.circle == null) {
            mBinding.circleTopicLayout.setVisibility(GONE);
        } else {
            mBinding.circleTopicLayout.setVisibility(VISIBLE);
            if (viewModel.topicDetail == null) {
                mBinding.topic.setText(getString(R.string.please_select_topic));
                mBinding.topic.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
            } else {
                mBinding.topic.setText(viewModel.topicDetail.content);
                mBinding.topic.setTextColor(ContextCompat.getColor(context, R.color.color_006fbb));
            }
        }
    }

    @Override
    protected void loadData() {
        GVideoEventBus.get(RecordPluginImpl.EVENT_BUS_VIDEO_ADDRESS, ChooseVideoModel.class)
                .observe(this, new NotNullObserver<ChooseVideoModel>() {
                    @Override
                    protected void onModelChanged(@NonNull ChooseVideoModel chooseVideoModel) {
                        if (TextUtils.isEmpty(chooseVideoModel.videoPath)) {
                            videoChange("");
                        } else {
                            videoChange(chooseVideoModel.videoPath);
                        }
                        Activity activity = getActivity();
                        if (activity == null) {
                            return;
                        }

                        Bitmap firstFrame = VideoUtils.getFirstFrameBitmap(viewModel.videoPath);

                        Glide.with(mBinding.videoViewImage.getContext())
                                .load(VideoUtils.getFirstFrameBitmap(viewModel.videoPath))
                                .placeholder(ContextCompat.getDrawable(activity, R.drawable.ic_choose_default))
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(
                                            @NonNull Drawable resource,
                                            @Nullable Transition<? super Drawable> transition
                                    ) {
                                        int width = firstFrame.getWidth();
                                        if (width < SizeUtils.dp2px(100)) {
                                            mBinding.videoViewImage.setMinimumWidth(width * 2);
                                        } else {
                                            mBinding.videoViewImage.setMinimumWidth(0);
                                        }
                                        int height = firstFrame.getHeight();
                                        if (height < SizeUtils.dp2px(100)) {
                                            mBinding.videoViewImage.setMinimumHeight(height * 2);
                                        } else {
                                            mBinding.videoViewImage.setMinimumHeight(0);
                                        }
                                        mBinding.videoViewImage.setImageDrawable(resource);
                                        viewModel.coverImageDrawable = resource;
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });

        GVideoEventBus.get(RecordPluginImpl.EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST, ChooseImageListModel.class)
                .observe(this, new NotNullObserver<ChooseImageListModel>() {
                    @Override
                    protected void onModelChanged(@NonNull ChooseImageListModel chooseVideoModel) {
                        ArrayList<String> currentImagePathList = viewModel.imagePathList.get();

                        switch (chooseVideoModel.operationType) {
                            case BATCH_REPLACE:
                                if (chooseVideoModel.imagePathList == null) {
                                    imageListChange(new ArrayList<>());
                                } else {
                                    imageListChange(chooseVideoModel.imagePathList);
                                }
                                break;
                            case SINGLE_PLACE:
                                if (chooseVideoModel.singleOperationIndex < 0) {
                                    return;
                                }
                                if (currentImagePathList == null) {
                                    return;
                                }
                                currentImagePathList.set(
                                        chooseVideoModel.singleOperationIndex,
                                        chooseVideoModel.imagePathList.get(0)
                                );
                                imageListChange(currentImagePathList);
                                break;
                            case BATCH_ADD:
                                if (chooseVideoModel.imagePathList == null) {
                                    return;
                                }
                                if (currentImagePathList == null) {
                                    currentImagePathList = new ArrayList<>();
                                }
                                currentImagePathList.addAll(chooseVideoModel.imagePathList);
                                imageListChange(currentImagePathList);
                                break;
                        }
                        if (currentImagePathList == null) {
                            mBinding.imageList.setImagesData(new ArrayList<>());
                            return;
                        }
                        setImageButtonEnableStatus(currentImagePathList.size() != DEFAULT_MAX_SELECT_IMAGE_NUM);
                        mBinding.imageList.setImagesData(currentImagePathList);
                    }
                });

        GVideoEventBus.get(RecordPluginImpl.EVENT_BUS_SELECT_CIRCLE, Circle.class)
                .observe(this, new NotNullObserver<Circle>() {
                    @Override
                    protected void onModelChanged(@NonNull Circle circle) {
                        viewModel.circle = circle;
                        viewModel.topicDetail = null;
                        updateTopicLayout();
                    }
                });

        GVideoEventBus.get(RecordPluginImpl.EVENT_BUS_SELECT_TOPIC, TopicDetail.class)
                .observe(this, new NotNullObserver<TopicDetail>() {
                    @Override
                    protected void onModelChanged(@NonNull TopicDetail circle) {
                        viewModel.topicDetail = circle;
                        updateTopicLayout();
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
        if (linkAnalysisDialog != null) {
            linkAnalysisDialog.initPasteBubble();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
        hideSoftInput();
    }

    @Override
    public String getPid() {
        return isQaType ? StatPid.PUBLISH_QA : StatPid.PUBLISH_COMPOSITION;
    }

    @Override
    protected String getSourcePage() {
        return isQaType ? getStringDataFromBundle(Constant.EXTRA_FROM_PID) : super.getSourcePage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding.soundRecord.destroy();
        mBinding.soundView.stop();
        VideoChooseHelper.IMAGE_NUM_LIMIT = DEFAULT_MAX_SELECT_IMAGE_NUM;
    }

    // =============================================
    // 文字、语音、视频、图片发生变化后，先更新ViewModel，
    // 再统一使用updateCanPublishStatus更新View状态
    // =============================================

    /**
     * Fragment中语音发生变化
     * 更新{@link PublishViewModel#soundEntity}
     *
     * @param soundFilePath 语音地址
     */
    public void soundChange(
            String soundFilePath,
            String soundText,
            long operationTime
    ) {
        viewModel.soundEntity.soundFilePath = soundFilePath;
        viewModel.soundEntity.soundText = soundText;
        viewModel.soundEntity.operationTime = operationTime;
        LogUtils.d(
                "soundFilePath -->> " + soundFilePath + "\n"
                        + "soundText -->> " + soundText + "\n"
                        + "operationTime -->> " + operationTime + "\n"
        );

        viewModel.videoPath = "";
        viewModel.coverImageDrawable = null;
        viewModel.imagePathList.set(new ArrayList<>());

        updateCanPublishStatus();
    }

    /**
     * Fragment中视频发生变化
     * 更新{@link PublishViewModel#videoPath}
     *
     * @param videoUrl 视频地址
     */
    public void videoChange(String videoUrl) {
        viewModel.videoPath = videoUrl;
        LogUtils.d("videoUrl -->>" + videoUrl);

        viewModel.soundEntity.soundFilePath = "";
        viewModel.soundEntity.soundText = "";
        viewModel.soundEntity.operationTime = 0;
        viewModel.imagePathList.set(new ArrayList<>());

        updateCanPublishStatus();
    }

    /**
     * Fragment中图片发生变化
     * 更新{@link PublishViewModel#imagePathList}
     *
     * @param imageUrlArray 图片地址
     */
    public void imageListChange(
            ArrayList<String> imageUrlArray
    ) {
        viewModel.imagePathList.set(imageUrlArray);

        StringBuilder logResult = new StringBuilder("Image url array -->> \n");
        for (String imageUrl : imageUrlArray) {
            logResult.append(imageUrl).append("\n");
        }
        LogUtils.d(logResult.toString());

        viewModel.soundEntity.soundFilePath = "";
        viewModel.soundEntity.soundText = "";
        viewModel.soundEntity.operationTime = 0;
        viewModel.videoPath = "";
        viewModel.coverImageDrawable = null;

        updateCanPublishStatus();

    }

    /**
     * 文字、语音、图片、视频、链接任何一项不为空
     * 都可以发布
     * <p>
     * 如果只有文字，那所有按钮都可以编辑
     * <p>
     * 链接只能存在一个，与语音、图片、视频不冲突
     * <p>
     * 语音、图片、视频三者之间只能存在一个
     * 其它两个要处于禁用状态
     * <p>
     * 在设置语音、图片、视频其中某一个数据时
     * 需要讲其他两个数据置空
     * <p>
     */
    public void updateCanPublishStatus() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Link的状态设置独立，可以先设置好
        boolean isLinkInvalid = TextUtils.isEmpty(viewModel.outShareUrl);
        setLinkButtonEnableStatus(isLinkInvalid);

        boolean isTextInValid = TextUtils.isEmpty(viewModel.qaTitle.get()) && TextUtils.isEmpty(viewModel.text.get());
        boolean isSoundInValid = !viewModel.soundEntity.isValid();
        boolean isVideoInvalid = TextUtils.isEmpty(viewModel.videoPath);

        List<String> imageLis = viewModel.imagePathList.get();
        boolean isImageInvalid = imageLis == null || imageLis.isEmpty();

        if (isTextInValid
                && isSoundInValid
                && isVideoInvalid
                && isImageInvalid
                && isLinkInvalid
        ) {
            mBinding.publish.setBackgroundResource(R.drawable.icon_publish_disable);
            mBinding.publish.setEnabled(false);

            setAudioRecordButtonEnableStatus(true);
            setImageButtonEnableStatus(true);
            setVideoButtonEnableStatus(true);

            mBinding.soundView.setVisibility(GONE);
            mBinding.videoViewLayout.setVisibility(GONE);
            mBinding.imageList.setVisibility(GONE);
            mBinding.linkLayout.setVisibility(GONE);
            return;
        }

        mBinding.publish.setBackgroundResource(R.drawable.icon_publish_enable);
        mBinding.publish.setEnabled(true);

        if (isTextInValid
                && isSoundInValid
                && isVideoInvalid
                && isImageInvalid
        ) {
            setAudioRecordButtonEnableStatus(true);
            setImageButtonEnableStatus(true);
            setVideoButtonEnableStatus(true);

            mBinding.soundView.setVisibility(GONE);
            mBinding.videoViewLayout.setVisibility(GONE);
            mBinding.imageList.setVisibility(GONE);
            return;
        }

        if (!isTextInValid
                && isSoundInValid
                && isVideoInvalid
                && isImageInvalid) {

            mBinding.soundView.setVisibility(GONE);
            mBinding.videoViewLayout.setVisibility(GONE);
            mBinding.imageList.setVisibility(GONE);

            setAudioRecordButtonEnableStatus(true);
            setImageButtonEnableStatus(true);
            setVideoButtonEnableStatus(true);
            return;
        }

        if (!isSoundInValid) {
            mBinding.soundView.setVisibility(VISIBLE);
            mBinding.videoViewLayout.setVisibility(GONE);
            mBinding.imageList.setVisibility(GONE);

            setAudioRecordButtonEnableStatus(false);
            setImageButtonEnableStatus(false);
            setVideoButtonEnableStatus(false);

            mBinding.soundRecord.setVisibility(GONE);

            return;
        }

        if (!isVideoInvalid) {
            mBinding.soundView.setVisibility(GONE);
            mBinding.videoViewLayout.setVisibility(VISIBLE);
            mBinding.imageList.setVisibility(GONE);

            setAudioRecordButtonEnableStatus(false);
            setImageButtonEnableStatus(false);
            setVideoButtonEnableStatus(true);
            return;
        }

        // 如果上方条件均不满足，则可以得出，当前含有图片数据
        mBinding.soundView.setVisibility(GONE);
        mBinding.videoViewLayout.setVisibility(GONE);
        mBinding.imageList.setVisibility(VISIBLE);

        setAudioRecordButtonEnableStatus(false);
        setImageButtonEnableStatus(true);
        setVideoButtonEnableStatus(false);

    }

    /**
     * 设置录音按钮的启用状态
     *
     * @param enableStatus 启用状态
     */
    private void setAudioRecordButtonEnableStatus(boolean enableStatus) {
        mBinding.soundRecordIcon.setTag(enableStatus);
        mBinding.soundRecordIcon.setImageResource(enableStatus ? R.drawable.icon_publish_sound_record_enable : R.drawable.icon_publish_sound_record_disable);

    }

    /**
     * 设置图片按钮的启用状态
     *
     * @param enableStatus 启用状态
     */
    private void setImageButtonEnableStatus(boolean enableStatus) {
        mBinding.picIcon.setTag(enableStatus);
        mBinding.picIcon.setImageResource(enableStatus ? R.drawable.icon_publish_pic : R.drawable.icon_publish_pic_disable);
    }

    /**
     * 设置视频按钮的启用状态
     *
     * @param enableStatus 启用状态
     */
    private void setVideoButtonEnableStatus(boolean enableStatus) {
        mBinding.videoIcon.setTag(enableStatus);
        mBinding.videoIcon.setImageResource(enableStatus ? R.drawable.icon_publish_video : R.drawable.icon_publish_video_disable);
    }

    /**
     * 设置链接按钮的启用状态
     *
     * @param enableStatus 启用状态
     */
    private void setLinkButtonEnableStatus(boolean enableStatus) {
        mBinding.linkIcon.setTag(enableStatus);
        Drawable drawable = mBinding.linkIcon.getDrawable();
        if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) mBinding.linkIcon.getDrawable();
            vectorDrawable.setTint(ResourcesUtils.getColor(enableStatus ? R.color.color_000000 : R.color.color_cccccc));
            mBinding.linkIcon.setImageDrawable(vectorDrawable);
        }
    }

    // =============================================

    private void hideSoftInput() {
        final Activity activity = getActivity();
        if (activity != null) {
            SoftInputUtils.hideSoftInput(activity);
        }
    }

    private void showSoftInput() {
        final Activity activity = getActivity();
        if (activity != null && viewModel != null) {
            SoftInputUtils.showSoftInput(viewModel.isQaType ? mBinding.qaTitle : mBinding.edittext, activity, 100);
        }

    }

    // ======================  点击被禁用按钮的时候需要Toast提示 ======================

    private void toastLinkButtonClickTip() {
        if (!TextUtils.isEmpty(viewModel.outShareUrl)) {
            showToast(getString(R.string.publish_only_one_link));
        }
    }

    private void toastAudioButtonClickTip() {
        if (viewModel.soundEntity.isValid()) {
            showToast(getString(R.string.publish_only_one_audio));
            return;
        }

        if (viewModel.imagePathList != null) {
            ArrayList<String> imageList = viewModel.imagePathList.get();
            if (imageList != null && !imageList.isEmpty()) {
                showToast(getString(R.string.publish_disable_audio_because_pic));
                return;
            }
        }

        if (!TextUtils.isEmpty(viewModel.videoPath)) {
            showToast(getString(R.string.publish_disable_audio_because_video));
        }
    }

    private void toastImageButtonClickTip() {

        if (viewModel.soundEntity.isValid()) {
            showToast(getString(R.string.publish_disable_pic_because_audio));
            return;
        }

        if (!TextUtils.isEmpty(viewModel.videoPath)) {
            showToast(getString(R.string.publish_disable_pic_because_video));
        }
    }

    private void toastVideoButtonClickTip() {

        if (viewModel.soundEntity.isValid()) {
            showToast(getString(R.string.publish_disable_video_because_audio));
            return;
        }

        if (viewModel.imagePathList != null) {
            ArrayList<String> imageList = viewModel.imagePathList.get();
            if (imageList != null && !imageList.isEmpty()) {
                showToast(getString(R.string.publish_disable_video_because_pic));
            }
        }
    }

    // ===========================================================================

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            String key = (viewModel.isQaType ? "qa" : "normal") + getClass().getName();
            PluginManager.get(RecordPlugin.class).setInputCacheText(key, viewModel.text.get());
            if (isQaType) {
                String title = "title" + getClass().getName();
                PluginManager.get(RecordPlugin.class).setInputCacheText(title, viewModel.qaTitle.get());
            }
        }
    }

}
