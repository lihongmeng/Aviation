package com.jxntv.android.video.ui.vtxt;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.google.gson.JsonObject;
import com.jxntv.android.video.R;
import com.jxntv.android.video.repository.MediaRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.GroupInfo;
import com.jxntv.base.model.circle.GroupInfoPid;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.GVideoStatManager;
import com.jxntv.stat.StatConstants;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.utils.ResourcesUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * @author huangwei
 * date : 2021/4/9
 * desc : 图、文、语音 动态详情
 **/
public class ImageTxtAudioViewModel extends BaseViewModel {

    private MediaRepository repository = new MediaRepository();

    // 通过上层传递进来的Id查询而得的详情数据
    public VideoModel videoModel;
    public final CheckThreadLiveData<Boolean> videoModelLiveData = new CheckThreadLiveData<>();

    // 底部toolbar相关状态，为了方便复用，故收集到MediaToolBarBottomNoBindStatus类中
    public CheckThreadLiveData<MediaToolBarBottomNoBindStatus> status = new CheckThreadLiveData<>();

    public CheckThreadLiveData<Boolean> needFinish = new CheckThreadLiveData<>();

    public CheckThreadLiveData<Boolean> followLiveData = new CheckThreadLiveData<>();

    public ImageTxtAudioViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(String mediaId) {
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        repository.loadMedia(mediaId).subscribe(new GVideoResponseObserver<VideoModel>() {
            @Override
            protected void onSuccess(@NonNull VideoModel netData) {
                if (videoModel != null) {
                    StatFromModel stat = new StatFromModel();
                    stat.fromPid = videoModel.getPid();
                    netData.setStatFromModel(stat);
                    netData.tabName = videoModel.tabName;
                    netData.bundle = videoModel.bundle;
                }
                videoModel = netData;
                videoModelLiveData.setValue(true);
                updatePlaceholderLayoutType(PlaceholderType.NONE);
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                super.onFailed(throwable);
                updatePlaceholderLayoutType(PlaceholderType.NONE);
                showToast(throwable.getMessage());
                needFinish.setValue(true);
            }
        });
    }

    private StatFromModel mStatFromModel;

    public void setStatFromModel(StatFromModel statFromModel) {
        mStatFromModel = statFromModel;
    }

    /**
     * 关注
     */
    public void onFollowClicked(View view) {
        if (videoModel == null) {
            return;
        }
        Context context = view.getContext();
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(context)) {
            return;
        }
        AuthorModel author = videoModel.getAuthor();
        PluginManager.get(AccountPlugin.class).getFollowRepository()
                .followAuthor(author.getId(),
                        author.getType(),
                        author.getName(),
                        !author.isFollow()).subscribe(new GVideoResponseObserver<Boolean>() {
            @Override
            protected void onSuccess(@NonNull Boolean result) {
                author.setFollow(result);
                followLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(), author.getId(),
                        author.getName(), author.getType(), throwable.getMessage());
            }
        });
    }

    public void onEnterCircleClick(View view) {
        Object object = view.getTag();
        if (object == null) {
            return;
        }
        PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                view.getContext(), new Circle((GroupInfo) object), null
        );
    }

    public void onTopicClick(View view, String pid) {
        if (videoModel == null) {
            return;
        }
        PluginManager.get(CirclePlugin.class).startTopicDetail(
                view,
                new GroupInfoPid(videoModel.getGroupInfo(), pid)
        );
    }

    /**
     * 点击用户头像
     */
    public void onAvatarClicked(View v) {
        if (videoModel == null) {
            return;
        }
        PluginManager.get(AccountPlugin.class).startPgcActivity(
                v,
                videoModel.getAuthor()
        );
    }

    /**
     * 喜欢
     */
    public void onFavorClicked(View view) {
        Context context = view.getContext();
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(context)) {
            return;
        }
        if (videoModel == null) {
            return;
        }
        MediaToolBarBottomNoBindStatus statusData = status.getValue();
        if (statusData == null) {
            statusData = new MediaToolBarBottomNoBindStatus(videoModel);
            status.setValue(statusData);
        }
        boolean target = !statusData.isFlavor;
        statFavorite(videoModel, target);
        String mediaId = videoModel.getId();
        MediaToolBarBottomNoBindStatus finalStatusData = statusData;
        PluginManager
                .get(FavoritePlugin.class)
                .getFavoriteRepository()
                .favoriteMedia(mediaId, target)
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean result) {
                        GVideoSensorDataManager.getInstance().favoriteContent(videoModel, mStatFromModel.pid,
                                !finalStatusData.isFlavor, null);
                        MediaToolBarBottomNoBindStatus statusData = status.getValue();
                        if (statusData == null) {
                            return;
                        }
                        statusData.isFlavor = target;
                        status.setValue(statusData);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().favoriteContent(videoModel, mStatFromModel.pid,
                                !finalStatusData.isFlavor, throwable.getMessage());
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException ||
                                throwable instanceof UnknownHostException) {
                            showToast(R.string.all_network_not_available_action_tip);
                            return;
                        }
                        ToastUtils.showShort(throwable.getMessage());
                    }
                });
    }

    private void statFavorite(VideoModel videoModel, boolean favorite) {
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStatFromModel);
        ds.addProperty(StatConstants.DS_KEY_FAVORITE, favorite ? "1" : "0");
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withEv(StatConstants.EV_FAVORITE)
                .withDs(ds.toString())
                .withPid(mStatFromModel != null ? mStatFromModel.pid : "")
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    public void updateToolBarStatus(VideoModel videoModel) {
        if (videoModel == null) {
            return;
        }
        MediaToolBarBottomNoBindStatus statusData = status.getValue();
        if (statusData == null) {
            statusData = new MediaToolBarBottomNoBindStatus(videoModel);
            status.setValue(statusData);
        }
        statusData.isFlavor = videoModel.getIsFavor() > 0;
        statusData.topicName = videoModel.getTopicName();
        statusData.commentCount = videoModel.getReviews();
        status.setValue(statusData);
    }

    /**
     * 分享按钮点击
     */
    public void onMoreClick(View view) {
        if (videoModel == null) {
            return;
        }
        MediaToolBarBottomNoBindStatus statusData = status.getValue();
        if (statusData == null) {
            statusData = new MediaToolBarBottomNoBindStatus(videoModel);
            status.setValue(statusData);
        }
        ObservableBoolean followObservable = videoModel.getAuthor().getObservable().getIsFollow();
        String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + videoModel.getId();

        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setVideoModel(videoModel)
                .setShowShare(videoModel.mediaStatus != null && videoModel.mediaStatus == 3)
                .setReportUrl(reportUrl)
                .setFavorite(statusData.isFlavor)
                .setFollow(followObservable.get())
                .setShowDelete(videoModel.getAuthor().isSelf())
                .setShowCreateBill(true)
                .setExtraData("确认删除动态?")
                .build();
        PluginManager.get(SharePlugin.class)
                .showShareDialog(view.getContext(), false, shareDataModel, mStatFromModel);
    }

}
