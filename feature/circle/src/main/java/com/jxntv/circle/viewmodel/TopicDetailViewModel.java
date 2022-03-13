package com.jxntv.circle.viewmodel;

import static com.jxntv.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.jxntv.base.Constant.BUNDLE_KEY.IS_QA;
import static com.jxntv.base.Constant.BUNDLE_KEY.TOPIC_DETAIL;
import static com.jxntv.stat.StatPid.PID_TOPIC_DETAIL;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.DefaultPageNumberUtil;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.R;
import com.jxntv.base.dialog.DefaultEnsureDialog;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;
import com.jxntv.network.NetworkUtils;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.AsyncUtils;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;

public class TopicDetailViewModel extends MediaPageViewModel {

    /**
     * 由上层传递进来的圈子相关信息
     * 在获取到话题详情信息后，使用话题详情构建一个新的Circle对象
     * 赋值给本变量
     */
    public Circle circle;

    // CircleRepository
    private final CircleRepository circleRepository = new CircleRepository();
    /**
     * 信息流在下拉的过程中,对页码的控制工具
     */
    private final DefaultPageNumberUtil defaultPageNumberUtil = new DefaultPageNumberUtil();

    // 话题详情数据
    public CheckThreadLiveData<TopicDetail> topicDetail = new CheckThreadLiveData<>();

    // 刷新标识，此值的变化代表需要整体进行刷新
    public final CheckThreadLiveData<Boolean> autoRefreshLiveData = new CheckThreadLiveData<>();

    public String key;

    // 提示发布要加社区的弹窗，仅仅提示一下
    private DefaultEnsureDialog needJoinCommunityDialog;

    public TopicDetailViewModel(@NonNull Application application) {
        super(application);
        autoRefreshLiveData.setValue(true);
        defaultPageNumberUtil.initSp(PID_TOPIC_DETAIL);
    }

    /**
     * 短视频详情页中加载更多逻辑
     */
    public Observable<ShortVideoListModel> loadMoreShortData() {
        return null;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else if (!UserManager.hasLoggedIn()) {
            updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            autoRefreshLiveData.setValue(true);
        }
    }


    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        loadRefreshData();
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        loadMoreData();
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return (page, loadListener) -> {

            loadTopicInfo();

            // 获取话题下信息流数据
            loadContentList(page);

        };
    }

    public void loadTopicInfo() {
        TopicDetail data = topicDetail.getValue();
        if (data == null) {
            return;
        }

        // 获取话题详情相关的数据
        circleRepository.topicDetail(data.id)
                .subscribe(new GVideoResponseObserver<TopicDetail>() {
                    @Override
                    protected void onSuccess(@NonNull TopicDetail netData) {
                        circle = new Circle(netData);
                        topicDetail.setValue(netData);
                        key = TOPIC_DETAIL + "_" + netData.id;

                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });

    }

    public void loadContentList(int page) {
        TopicDetail topicDetail = this.topicDetail.getValue();
        if (topicDetail == null) {
            return;
        }
        circleRepository.loadTopicDetailContentList(
                topicDetail.id,
                page,
                DEFAULT_PAGE_COUNT
        ).subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {
            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if (mMediaModelList.isEmpty()) {
                    updatePlaceholderLayoutType(PlaceholderType.LOADING);
                }
            }

            @Override
            protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {

                defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
                currPage = defaultPageNumberUtil.getDefaultPageNumber(key);

                List<MediaModel> list = mediaListWithPage.getList();
                List<MediaModel> modelList = new ArrayList<>();
                for (MediaModel media : list) {
                    if (media.isValid()) {
                        MediaModel model = new MediaModel(media);
                        model.tabId = mTabId;
                        model.setPid(getPid());
                        //model.playState = 0;
                        //model.viewPosition = 0;
                        model.correspondVideoModelAddress = media.getMemoryAddress();
                        modelList.add(model);
                    }
                }
                if (loadType == LOAD_DATA_TYPE_REFRESH && !mediaListWithPage.getPage().hasNextPage()) {
                    defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
                }
                if (modelList.size() <= 0) {
                    if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                        showToast(R.string.all_nor_more_data);
                        defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
                    }
                } else {
                    loadSuccess(modelList);
                }
                loadComplete();
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException) {
                    showToast(com.jxntv.media.R.string.all_network_not_available);
                    loadComplete();
                    return;
                }
                showToast(com.jxntv.media.R.string.all_nor_more_data);
                loadComplete();
            }
        });
    }

    public void onPublishClick(View view) {
        if (!UserManager.hasLoggedIn()) {
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (accountPlugin != null) {
                accountPlugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPid()),
                        ResourcesUtils.getString(R.string.publish)
                );
            }
            return;
        }
        if (circle == null) {
            return;
        }
        // if (!InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId).get()) {
        //     showCanPublishDialog(view);
        //     return;
        // }
        TopicDetail topicDetailData = topicDetail.getValue();
        if (topicDetailData == null) {
            return;
        }
        if (topicDetailData.join == null || !topicDetailData.join) {
            joinCircle(view);
            return;
        }
        showNetworkDialog("准备发布...");
        circleRepository.canPublish(circle.groupId, topicDetailData.id)
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        hideNetworkDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CIRCLE,circle);
                        bundle.putParcelable(TOPIC_DETAIL,topicDetailData);
                        bundle.putBoolean(IS_QA,false);
                        PluginManager.get(RecordPlugin.class)
                                .startPublishFragmentUseActivity(view.getContext(), bundle);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        hideNetworkDialog();
                        showToast(throwable.getMessage());
                    }
                });
    }

    private void showNeedJoinCommunityDialog(Context context) {
        if (needJoinCommunityDialog == null) {
            needJoinCommunityDialog = new DefaultEnsureDialog(context);
            needJoinCommunityDialog.init(
                    v -> {
                        needJoinCommunityDialog.dismiss();
                        onPublishClick(v);
                    },
                    ResourcesUtils.getString(R.string.publish_join_community_tip),
                    ResourcesUtils.getString(R.string.alright)
            );
            needJoinCommunityDialog.showJoinCommunityStyle();
        }
        needJoinCommunityDialog.show();
    }

    /**
     * 加入圈子
     */
    public void joinCircle(View v) {
        circleRepository.joinCircle(circle.groupId, "")
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        Context context = v.getContext();
                        if (context == null || circle == null) {
                            return;
                        }
                        circle.setJoin(true);

                        TopicDetail topicDetailValue = topicDetail.getValue();
                        if (topicDetailValue == null) {
                            return;
                        }
                        topicDetailValue.join = true;

                        // showToast(ResourcesUtils.getString(R.string.join_success));

                        showNeedJoinCommunityDialog(context);

                        GVideoEventBus.get(
                                Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE,
                                CircleJoinStatus.class
                        ).post(new CircleJoinStatus(circle.groupId, true));

                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.PID_TOPIC_DETAIL),
                                null
                        );
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.PID_TOPIC_DETAIL),
                                throwable.getMessage()
                        );
                        showToast(ResourcesUtils.getString(R.string.join_failed));
                    }
                });

    }

    @Override
    public void loadFailure(Throwable throwable) {
        if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
            // 加载失败后的提示
            if (currPage > 1) {
                //加载失败需要回到加载之前的页数
                currPage--;
                defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
            }
        }
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.loadFailure(throwable);
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initialData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_INITIAL;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        mModel.initialData(this);
    }

    /**
     * 刷新数据
     */
    public void loadRefreshData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_REFRESH;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        mModel.loadData(currPage, this);
    }

    /**
     * 上拉加载更多数据
     */
    public void loadMoreData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_LOAD_MORE;
        currPage++;
        mModel.loadData(currPage, this);
    }

    public void onShareClicked(View v) {
        if (topicDetail.getValue() == null) {
            return;
        }
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setTitle(topicDetail.getValue().content)
                .setDescription(v.getContext().getString(R.string.share_default_description))
                .setUrl(topicDetail.getValue().shareUrl)
                .setShowCreateBill(false)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false,
                true, shareDataModel, null);
    }
}
