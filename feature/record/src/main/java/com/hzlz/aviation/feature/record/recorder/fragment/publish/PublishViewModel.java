package com.hzlz.aviation.feature.record.recorder.fragment.publish;

import static com.hzlz.aviation.kernel.base.Constant.SP_KEY.HISTORY_COMMUNITY_ID;

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
import com.hzlz.aviation.feature.record.recorder.data.SoundEntity;
import com.hzlz.aviation.feature.record.recorder.dialog.SelectCommunityDialog;
import com.hzlz.aviation.feature.record.recorder.fragment.preview.PreviewVideoFragment;
import com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.feature.record.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishViewModel extends BaseViewModel {

    // ????????????????????????
    private static final String UPLOAD_IMAGE_SUFFIX = ".jepg";

    // ????????????
    public final ObservableField<ArrayList<String>> imagePathList;

    // ??????Fragment????????????
    public final CheckThreadLiveData<Boolean> dealKeyboard = new CheckThreadLiveData<>();

    // ??????Fragment????????????
    public final CheckThreadLiveData<Object> finishActivity = new CheckThreadLiveData<>();

    // ??????Fragment????????????????????????
    public final CheckThreadLiveData<Object> updateCommunityLayout = new CheckThreadLiveData<>();

    public final ObservableField<Integer> qaTitleCount = new ObservableField<>();
    public final ObservableField<Boolean> isNeedShowTitle = new ObservableField<>();

    // ????????????
    public ObservableField<String> qaTitle = new ObservableField<>();

    // ???????????????
    public ObservableField<String> text = new ObservableField<>();

    // ??????
    public SoundEntity soundEntity;

    // ?????????????????????????????????
    public String videoPath;

    // ??????????????????
    public String outShareTitle;

    // ????????????
    public String outShareUrl;

    // ??????????????????
    public Drawable coverImageDrawable;

    // ??????
    public Circle circle;

    // ??????
    public TopicDetail topicDetail;

    // ???????????????
    public boolean isQaType = false;

    // ???????????????
    public AuthorModel authorModel;

    // ??????????????????????????????URL??????????????????????????????????????????????????????????????????
    public boolean hasDealReceivedError = false;

    // ????????????Tab???Id
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
     * ??????
     */
    public void publish() {

        if (isQaType && (TextUtils.isEmpty(qaTitle.get()) || qaTitle.get().trim().length() == 0)) {
            ToastUtils.showShort("???????????????");
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
        } else if (fragmentName.contains("??????-") || fragmentName.equals(StatPid.getPageName(StatPid.QUESTION_ANSWER_LIST))) {
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
        //?????????????????????????????????????????????????????????
        text.set("");
        qaTitle.set("");
        // ??????????????????????????????????????????????????????????????????????????????
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
        // ??????????????????
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
            LogUtils.d("???????????????????????????????????????...");
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

        // ???????????????????????????????????????????????????????????????????????????
        if (circle != null) {
            updateCommunityLayout.setValue(true);
            return;
        }

        // ???????????????????????????????????????
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        String userId = accountPlugin.getUserId();
        if (TextUtils.isEmpty(userId)) {
            updateCommunityLayout.setValue(true);
            return;
        }

        try {
            // ????????????????????????????????????????????????
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
