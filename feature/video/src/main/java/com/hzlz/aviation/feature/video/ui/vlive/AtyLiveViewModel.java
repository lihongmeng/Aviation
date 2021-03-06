package com.hzlz.aviation.feature.video.ui.vlive;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hzlz.aviation.feature.video.Me;
import com.hzlz.aviation.feature.video.adapter.AtyLiveCommentAdapter;
import com.hzlz.aviation.feature.video.model.LiveCommentModel;
import com.hzlz.aviation.feature.video.model.LiveDetailModel;
import com.hzlz.aviation.feature.video.repository.LiveAtyRepository;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentInputTxtDialog;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * @author huangwei
 * date : 2021/2/8
 * desc : ????????????
 **/
public class AtyLiveViewModel extends BaseViewModel {

    // ????????????????????????
    private VideoPlayHelper playHelper;

    // LiveAtyRepository
    private final LiveAtyRepository liveRepository = new LiveAtyRepository();

    // ??????????????????
    public final CheckThreadLiveData<LiveCommentModel> mCommentListLiveData = new CheckThreadLiveData<>();

    // ??????????????????
    public LiveDetailModel liveDetailModel;

    // ?????????????????????????????????
    public final MutableLiveData<Boolean> liveDetailModelLiveData = new MutableLiveData<>();

    // ????????????
    public final ObservableBoolean isFollow = new ObservableBoolean();

    // ??????Id
    private String mediaId;

    private CommentInputTxtDialog commentInputTxtDialog;

    /**
     * ??????????????????????????????????????????
     * ?????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????
     */
    private String requestCommentTime;

    /**
     * ???????????????????????????Timer
     */
    private Disposable disposable;

    /**
     * ???????????????????????????
     * ?????????????????????????????????
     */
    private StatFromModel mStatFromModel;

    @NonNull
    public AtyLiveCommentAdapter adapter = new AtyLiveCommentAdapter();

    public AtyLiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel(String mediaId, StatFromModel mStatFromModel) {
        this.mStatFromModel = mStatFromModel;
        this.mediaId = mediaId;
//        requestCommentTime = System.currentTimeMillis() + "";
        requestCommentTime = "";
        updatePlaceholderLayoutType(PlaceholderType.NONE);
        loadData(false);
    }

    /**
     * ??????????????????
     */
    public void setGVideoView(GVideoView gVideoView, LiveDetailModel liveDetailModel) {
        playHelper = new VideoPlayHelper(gVideoView);
        playHelper.startPlay(liveDetailModel, mStatFromModel, this);
    }

    /**
     * ?????????????????????
     */
    public void resetGVideoView(LiveDetailModel liveDetailModel) {
        playHelper.startPlay(liveDetailModel, mStatFromModel, this);
    }

    /**
     * ????????????????????????????????????
     */
    private void reLoadLivingData() {
        loadData(true);
    }

    /**
     * ??????????????????
     *
     * @param isReload ????????????????????????
     */
    private void loadData(boolean isReload) {
        liveRepository.loadLiveDetail(mediaId)
                .subscribe(new GVideoResponseObserver<LiveDetailModel>() {
                    @Override
                    protected void onSuccess(@NonNull LiveDetailModel netData) {

                        LiveDetailModel.BroadcastDTOBean broadcastDTOBean = netData.getBroadcastDTO();
                        String liveId = broadcastDTOBean == null ? "" : broadcastDTOBean.getId() + "";
                        String liveTitle = broadcastDTOBean == null ? "" : broadcastDTOBean.getTitle();

                        LiveDetailModel.CerDTOBean cerDTOBean = netData.getCerDTO();
                        String pgcName = cerDTOBean == null ? "" : cerDTOBean.getName();

                        List<String> tenantNameList = new ArrayList<>();
                        if (cerDTOBean != null && cerDTOBean.tenantNameList != null) {
                            tenantNameList = cerDTOBean.tenantNameList;
                        }

                        if (liveDetailModel == null) {
                            GVideoSensorDataManager.getInstance().clickAtyTvLive(liveId, liveTitle, pgcName, tenantNameList);
                        }

                        liveDetailModel = netData;
                        liveDetailModelLiveData.postValue(true);
                        isFollow.set(netData.getCerDTO().getFollower() == 1);
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                        if (!isReload) {
                            loadCommentData(netData.getBroadcastDTO().getDefaultDesc());
                        }
                    }
                });
    }

    /**
     * ??????????????????
     */
    private void loadCommentData() {
        liveRepository.loadCommentData(mediaId, requestCommentTime)
                .subscribe(new GVideoResponseObserver<LiveCommentModel>() {
                    @Override
                    protected void onSuccess(@NonNull LiveCommentModel liveCommentModel) {
                        if (liveDetailModel == null) {
                            return;
                        }
                        LiveDetailModel.BroadcastDTOBean broadcastDTOBean = liveDetailModel.getBroadcastDTO();
                        if (broadcastDTOBean == null) {
                            return;
                        }
                        requestCommentTime = liveCommentModel.getLastAuditTime();
                        String liveUrl = liveCommentModel.getLiveUrl();

                        //??????????????????????????????
                        if (liveCommentModel.getStatus() != broadcastDTOBean.getStatus()) {
                            reLoadLivingData();
                        }
                        //??????????????????????????????
                        if (!TextUtils.equals(liveUrl, broadcastDTOBean.getLiveUrl())) {
                            broadcastDTOBean.setLiveUrl(liveUrl);
                            playHelper.changePlayUrl(liveUrl);
                        }
                        mCommentListLiveData.setValue(liveCommentModel);
                    }
                });
    }

    private void loadCommentData(String tips) {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        //?????????????????????
        List<LiveCommentModel.CommentLstBean> list = new ArrayList<>();
        LiveCommentModel.CommentLstBean commentModel = new LiveCommentModel.CommentLstBean();
        commentModel.setMessage(tips);
        list.add(commentModel);
        adapter.addData(list);
        //????????????
        disposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> loadCommentData());
    }

    public void finishActivity(View v) {
        Activity activity = getActivityByView(v);
        if (activity == null) {
            return;
        }
        activity.finish();
    }

    /**
     * ??????
     */
    public void onFollowClicked(View v) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
            return;
        }
        if (liveDetailModel == null) {
            return;
        }
        LiveDetailModel.CerDTOBean cerDTO = liveDetailModel.getCerDTO();
        if (cerDTO == null) {
            return;
        }
        String id = cerDTO.getId();
        int authorType = cerDTO.getAuthorType();
        String name = cerDTO.getName();
        boolean followStatus = !isFollow.get();

        PluginManager.get(AccountPlugin.class)
                .getFollowRepository()
                .followAuthor(id, authorType, name, followStatus)
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean result) {
                        isFollow.set(result);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().followAccount(
                                followStatus,
                                id,
                                name,
                                authorType,
                                throwable.getMessage()
                        );
                    }
                });
    }


    /**
     * ??????
     */
    public void onCommentClicked(View view) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.comment)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
            return;
        }
        if (commentInputTxtDialog == null) {
            commentInputTxtDialog = new CommentInputTxtDialog(view.getContext(),getPid());
            commentInputTxtDialog.setUp(
                    null,
                    comment -> liveRepository.commentLive(mediaId, comment.replyContent)
                            .subscribe(new GVideoResponseObserver<Object>() {
                                           @Override
                                           protected void onSuccess(@NonNull Object o) {
                                               List<LiveCommentModel.CommentLstBean> list = new ArrayList<>();
                                               LiveCommentModel.CommentLstBean commentModel = new LiveCommentModel.CommentLstBean();
                                               commentModel.setMessage(comment.replyContent);
                                               commentModel.setName(Me.getMeInfo().getName());
                                               list.add(commentModel);
                                               adapter.addData(list);
                                               commentInputTxtDialog = null;

                                               if (liveDetailModel == null) {
                                                   return;
                                               }
                                               LiveDetailModel.BroadcastDTOBean broadcastDTOBean = liveDetailModel.getBroadcastDTO();
                                               String liveId = broadcastDTOBean == null ? "" : broadcastDTOBean.getId() + "";
                                               String liveTitle = broadcastDTOBean == null ? "" : broadcastDTOBean.getTitle();

                                               LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
                                               boolean isCerDTOBeanEmpty = cerDTOBean == null;
                                               String pgcName = isCerDTOBeanEmpty ? "" : cerDTOBean.getName();

                                               List<String> tenantNameList = new ArrayList<>();
                                               if (cerDTOBean != null && cerDTOBean.tenantNameList != null) {
                                                   tenantNameList = cerDTOBean.tenantNameList;
                                               }

                                               GVideoSensorDataManager.getInstance().activityLiveSendComment(
                                                       liveId,
                                                       liveTitle,
                                                       pgcName,
                                                       tenantNameList,
                                                       isFollow.get()
                                               );

                                           }

                                           @Override
                                           public void onError(Throwable throwable) {
                                               showToast(throwable.getMessage());
                                           }
                                       }
                            )
            );
        }
        commentInputTxtDialog.show();
    }

    // ??????
    public void onShareClicked(View v) {
        if (liveDetailModel == null) {
            return;
        }
        LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
        if (cerDTOBean == null) {
            return;
        }
        LiveDetailModel.BroadcastDTOBean broadcastDTO = liveDetailModel.getBroadcastDTO();
        if (broadcastDTO == null) {
            return;
        }
        String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + cerDTOBean.getId();
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setUrl(broadcastDTO.getShareUrl())
                .setTitle(broadcastDTO.getTitle())
                .setMediaId(mediaId)
                .setAuthorId(cerDTOBean.getId())
                .setAuthorType(cerDTOBean.getAuthorType())
                .setAuthorName(cerDTOBean.getName())
                .setImage(broadcastDTO.getThumbUrl())
                .setShowFavorite(false)
                .setShowReport(true)
                .setShowFollow(false)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), true, shareDataModel, mStatFromModel);
    }

    /**
     * ??????
     */
    public void onPraiseClicked() {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        liveRepository.likeLive(mediaId)
                .subscribe(new GVideoResponseObserver<Object>() {
                    @Override
                    protected void onSuccess(@NonNull Object o) {
                        showToast(R.string.like_success);
                        LogUtils.e(new Gson().toJson(o));

                        if (liveDetailModel == null) {
                            return;
                        }
                        LiveDetailModel.BroadcastDTOBean broadcastDTOBean = liveDetailModel.getBroadcastDTO();
                        String liveId = broadcastDTOBean == null ? "" : broadcastDTOBean.getId() + "";
                        String liveTitle = broadcastDTOBean == null ? "" : broadcastDTOBean.getTitle();

                        LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
                        String pgcName = cerDTOBean == null ? "" : cerDTOBean.getName();

                        GVideoSensorDataManager.getInstance().activityLiveLike(
                                liveId,
                                liveTitle,
                                pgcName
                        );
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        showToast(throwable.getMessage());
                    }
                });
    }


    public void onResume() {
        if (playHelper != null) {
            playHelper.resume();
        }
    }

    public void onPause() {
        if (playHelper != null) {
            playHelper.pause();
        }
    }

    public void onDestroy() {
        if (playHelper != null) {
            playHelper.destroy();
        }
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    public boolean onBackPress() {
        if (playHelper != null) {
            return playHelper.onBackPress();
        }
        return false;
    }


    private FragmentActivity getActivityByView(@NonNull final View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof FragmentActivity) {
                return (FragmentActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
