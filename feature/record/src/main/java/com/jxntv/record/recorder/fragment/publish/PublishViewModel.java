package com.jxntv.record.recorder.fragment.publish;

import static com.jxntv.base.Constant.SP_KEY.HISTORY_COMMUNITY_ID;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.utils.SpannableStringUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.record.R;
import com.jxntv.record.recorder.data.SoundEntity;
import com.jxntv.record.recorder.dialog.SelectCommunityDialog;
import com.jxntv.record.recorder.fragment.preview.PreviewVideoFragment;
import com.jxntv.record.recorder.helper.RecordSharedPrefs;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.FileUtils;
import com.jxntv.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishViewModel extends BaseViewModel {

    // 本地保存图片后缀
    private static final String UPLOAD_IMAGE_SUFFIX = ".jepg";

    // 图片数组
    public final ObservableField<ArrayList<String>> imagePathList;

    // 通知Fragment处理键盘
    public final CheckThreadLiveData<Boolean> dealKeyboard = new CheckThreadLiveData<>();

    // 通知Fragment结束自身
    public final CheckThreadLiveData<Object> finishActivity = new CheckThreadLiveData<>();

    // 通知Fragment更新社区相关信息
    public final CheckThreadLiveData<Object> updateCommunityLayout = new CheckThreadLiveData<>();

    public final ObservableField<Integer> qaTitleCount = new ObservableField<>();
    public final ObservableField<Boolean> isNeedShowTitle = new ObservableField<>();

    // 问答标题
    public ObservableField<String> qaTitle = new ObservableField<>();

    // 填写的文字
    public ObservableField<String> text = new ObservableField<>();

    // 语音
    public SoundEntity soundEntity;

    // 视频文件在手机上的路径
    public String videoPath;

    // 链接标题数据
    public String outShareTitle;

    // 链接地址
    public String outShareUrl;

    // 视频封面图片
    public Drawable coverImageDrawable;

    // 圈子
    public Circle circle;

    // 话题
    public TopicDetail topicDetail;

    // 是否是问答
    public boolean isQaType = false;

    // 被提问对象
    public AuthorModel authorModel;

    // 添加外链的时候，解析URL失败会多次回调，这里加一层判断，以免上报多次
    public boolean hasDealReceivedError = false;

    // 当前选中Tab的Id
    public long selectTabId = -1;

    public PublishViewModel(Application application) {
        super(application);
        text.set("");
        videoPath = "";
        soundEntity = new SoundEntity();
        imagePathList = new ObservableField<>();
        qaTitle.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (qaTitle.equals(sender)) {
                    if (!TextUtils.isEmpty(qaTitle.get())) {
                        qaTitleCount.set(qaTitle.get().trim().length());
                    } else {
                        qaTitleCount.set(0);
                    }
                    isNeedShowTitle.set(isQaType && qaTitleCount.get() > 0);
                }
            }
        });
    }

    public ObservableField<ArrayList<String>> getImagePathList() {
        return imagePathList;
    }

    /**
     * 发布
     */
    public void publish() {

        if (isQaType && (TextUtils.isEmpty(qaTitle.get()) || qaTitle.get().trim().length() == 0)) {
            ToastUtils.showShort("请填写问题");
            return;
        }
        boolean isSoundInValid = !soundEntity.isValid();
        boolean isVideoInvalid = TextUtils.isEmpty(videoPath);

        List<String> imageLis = imagePathList.get();
        boolean isImageInvalid = imageLis == null || imageLis.isEmpty();

        String startPublishFrom;
        Activity activity = AppManager.getAppManager().getLastWithIndexActivity(1);
        String fragmentName = StatPid.getPageName(((BaseActivity) activity).getCurrentFragmentPageName());
        CirclePlugin circlePlugin = PluginManager.get(CirclePlugin.class);

        if (TextUtils.isEmpty(fragmentName)) {
            startPublishFrom = Constant.START_PUBLISH_FROM.HOME;
        } else if (fragmentName.contains("社区-") || fragmentName.equals(StatPid.getPageName(StatPid.QUESTION_ANSWER_LIST))) {
            startPublishFrom = Constant.START_PUBLISH_FROM.CIRCLE_DETAIL;
        } else if (fragmentName.equals(StatPid.getPageName(StatPid.PID_TOPIC_DETAIL))) {
            startPublishFrom = Constant.START_PUBLISH_FROM.TOPIC_DETAIL;
        } else {
            startPublishFrom = Constant.START_PUBLISH_FROM.HOME;
        }

        PublishHelper.get().setData(
                text.get() == null ? "" : text.get().trim(),
                getIntroduction(),
                circle,
                getTopicId(),
                circle == null ? null : circle.tenantId,
                circle == null ? "" : circle.tenantName,
                getTopicContent(),
                selectTabId,
                isQaType,
                authorModel != null ? authorModel.getId() : null,
                outShareTitle,
                outShareUrl,
                startPublishFrom
        );

        if (isSoundInValid && isVideoInvalid && isImageInvalid) {
            PublishHelper.get().publishPic(null, outShareTitle, outShareUrl);
            text.set("");
            qaTitle.set("");
            return;
        }

        if (!isSoundInValid) {
            PublishHelper.get().publishSound(soundEntity);
            text.set("");
            qaTitle.set("");
            return;
        }

        if (!isVideoInvalid) {
            dealPublishVideo();
            text.set("");
            qaTitle.set("");
            return;
        }
        //置空文本，防止退出时被记录到输入缓存中
        text.set("");
        qaTitle.set("");
        // 经过以上判断之后，执行到此处则表示，必然存在视频数据
        dealPublishPic();
    }

    private String getIntroduction() {

        if (isQaType) {
            return qaTitle.get() == null ? "" : qaTitle.get().trim();
        } else {
            if (TextUtils.isEmpty(text.get())) {
                return "";
            }
            if (text.get().length() < 30) {
                return text.get();
            }
            return text.get().substring(0, 30);
        }
    }

    private void dealPublishVideo() {
        // 先上传封面图
        final String coverImage = FileUtils.getFileNameWithOutSuffix(videoPath) + UPLOAD_IMAGE_SUFFIX;
        final File coverImageFile = FileUtils.saveDrawableFile(coverImage, coverImageDrawable);
        if (coverImageFile == null) {
            return;
        }
        PublishHelper.get().publishVideo(coverImageFile, videoPath);
    }

    private void dealPublishPic() {
        List<String> imageList = imagePathList.get();
        if (imageList == null || imageList.isEmpty()) {
            return;
        }
        PublishHelper.get().publishPic(imageList, outShareTitle, outShareUrl);
    }

    private Long getGroupId() {
        if (circle == null) {
            return null;
        }
        return circle.groupId;
    }

    private String getGroupName() {
        if (circle == null) {
            return null;
        }
        return circle.name;
    }

    private Long getTopicId() {
        if (topicDetail == null) {
            return null;
        }
        return topicDetail.id;
    }

    private String getTopicContent() {
        if (topicDetail == null) {
            return null;
        }
        return topicDetail.content;
    }

    public SpannableStringBuilder getHintText() {
        qaTitleCount.set(0);
        isNeedShowTitle.set(isQaType);
        if (isQaType) {
            String all = GVideoRuntime.getAppContext().getString(R.string.publish_qa_hint);
            String text = GVideoRuntime.getAppContext().getString(R.string.publish_additional_notes);
            return SpannableStringUtils.setSpanColor(
                    all,
                    R.color.color_cccccc,
                    13,
                    Typeface.NORMAL,
                    text,
                    R.color.color_cccccc,
                    15,
                    Typeface.NORMAL
            );
        } else {
            String publish = GVideoRuntime.getAppContext().getString(R.string.publish_hint);
            return new SpannableStringBuilder(publish);
        }
    }

    public SpannableStringBuilder getQaTitleHintText() {
        String all = GVideoRuntime.getAppContext().getString(R.string.publish_qa_title_hint);
        String text = GVideoRuntime.getAppContext().getString(R.string.hint_question);
        return SpannableStringUtils.setSpanColor(
                all,
                R.color.color_cccccc,
                13,
                Typeface.NORMAL,
                text,
                R.color.color_cccccc,
                19,
                Typeface.BOLD
        );
    }


    public void onSelectCommunityClick(View view) {
        dealKeyboard.setValue(false);
        SelectCommunityDialog selectCommunityDialog = new SelectCommunityDialog(view.getContext());
        selectCommunityDialog.setOnSelectListener(callbackData -> {
            circle = callbackData;
            updateCommunityLayout.setValue(true);
        });
        selectCommunityDialog.show();
        selectCommunityDialog.updateCircle(circle);
        selectCommunityDialog.refreshDataList();
    }

    public void onTopicClick(View view) {
        PluginManager.get(CirclePlugin.class).startSearchTopicWithActivity(view.getContext(), circle);
    }

    public void onBackClick() {
        dealKeyboard.setValue(false);
        finishActivity.setValue(true);
    }

    public void onPublish() {
        if (PublishHelper.get().isPublishing()) {
            LogUtils.d("当前正在发布，重复点击无效...");
            return;
        }
        dealKeyboard.setValue(false);
        publish();
    }

    public void onVideoLayoutClick(View view) {
        dealKeyboard.setValue(false);
        Bundle bundle = new Bundle();
        bundle.putString(PreviewVideoFragment.INTENT_PREVIEW_TYPE, PreviewVideoFragment.INTENT_PREVIEW_TYPE_NORMAL);
        Navigation.findNavController(view).navigate(R.id.previewVideoFragment, bundle);
    }

    public void checkHistoryCommunity() {

        // 如果上层传递进来的社区信息不为空，就有限使用该数据
        if (circle != null) {
            updateCommunityLayout.setValue(true);
            return;
        }

        // 逻辑上不会出现未登录的情况
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        String userId = accountPlugin.getUserId();
        if (TextUtils.isEmpty(userId)) {
            updateCommunityLayout.setValue(true);
            return;
        }

        try {
            // 获取上次发布时选择的社区社区信息
            String historyCommunity = RecordSharedPrefs.getInstance()
                    .getString(HISTORY_COMMUNITY_ID + "_" + userId, "");
            circle = new Gson().fromJson(historyCommunity, Circle.class);
        } catch (Exception exception) {
            circle = null;
            updateCommunityLayout.setValue(true);
            return;
        }

        if (circle == null) {
            updateCommunityLayout.setValue(true);
            return;
        }

        accountPlugin.requestUserIsJoinedCommunity(userId, circle.groupId, new BaseResponseObserver<Boolean>() {
            @Override
            protected void onRequestData(Boolean value) {
                circle = value ? circle : null;
                updateCommunityLayout.setValue(true);
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                circle = null;
                updateCommunityLayout.setValue(true);
            }
        });
    }

}
