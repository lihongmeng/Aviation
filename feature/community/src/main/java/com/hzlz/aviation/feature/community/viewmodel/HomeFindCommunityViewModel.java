package com.hzlz.aviation.feature.community.viewmodel;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.media.MediaConstants.DEFAULT_PAGE_COUNT;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.activity.CircleActivity;
import com.hzlz.aviation.feature.community.adapter.FindCircleItemAdapter;
import com.hzlz.aviation.feature.community.databinding.FindCircleItemObservable;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.model.circle.CircleTag;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;


public class HomeFindCommunityViewModel extends BaseRefreshLoadMoreViewModel {

    private final CircleRepository circleRepository;
    public CircleTag circleTag;
    public FindCircleItemObservable findCircleItemObservable;
    public FindCircleItemAdapter findCircleItemAdapter;
    public CheckThreadLiveData<Boolean> refreshData = new CheckThreadLiveData<>();

    public HomeFindCommunityViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
        findCircleItemObservable = new FindCircleItemObservable();
        findCircleItemAdapter = new FindCircleItemAdapter();
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return findCircleItemAdapter;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            findCircleItemAdapter.replaceData(null);
            findCircleItemAdapter.showNetworkNotAvailablePlaceholder();
        } else {
            refreshData.setValue(true);
            if (findCircleItemAdapter.getItemCount() <= 0) {
                updatePlaceholderLayoutType(PlaceholderType.LOADING);
            } else {
                updatePlaceholderLayoutType(PlaceholderType.NONE);
            }
        }
    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        circleRepository.findCircleContentList(
                null,
                1,
                DEFAULT_PAGE_COUNT
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoRefreshObserver<Circle>(view) {
                    @Override
                    protected void onSuccess(@Nullable List<Circle> dataList) {
                        super.onSuccess(dataList);
                        view.finishGVideoRefresh();

                        if (findCircleItemAdapter.getItemCount() <= 0) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        view.finishGVideoRefresh();
                        if (findCircleItemAdapter.getItemCount() <= 0) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }
                });
    }

    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        circleRepository.findCircleContentList(
                null,
                mLocalPage.getPageNumber(),
                DEFAULT_PAGE_COUNT
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoLoadMoreObserver<Circle>(view) {
                    @Override
                    protected void onSuccess(@Nullable List<Circle> dataList) {
                        super.onSuccess(dataList);
                        view.finishGVideoLoadMore();

                        if (findCircleItemAdapter.getItemCount() <= 0) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        view.finishGVideoLoadMore();
                        if (findCircleItemAdapter.getItemCount() <= 0) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }
                });
    }

    public void joinCircle(Context context, @NonNull Circle circle, View view) {
        new CircleRepository()
                .joinCircle(circle.groupId, "")
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
                               @Override
                               protected void onRequestData(@NonNull Object result) {
                                   if (context == null || view == null) {
                                       return;
                                   }
                                   circle.setJoin(true);

                                   view.setBackgroundResource(R.drawable.icon_enter_circle_short);

                                   GVideoSensorDataManager.getInstance().followCommunity(
                                           circle.groupId,
                                           circle.name,
                                           circle.tenantId,
                                           circle.tenantName,
                                           circle.labels,
                                           StatPid.getPageName(StatPid.CIRCLE_FIND),
                                           null
                                   );
                                   showToast(ResourcesUtils.getString(R.string.join_success));
                                   GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class).post(null);

                                   Intent intent = new Intent(context, CircleActivity.class);
                                   intent.putExtra(CIRCLE, circle);
                                   intent.putExtra(CirclePluginImpl.INTENT_CIRCLE_TYPE, R.id.circleDetailFragment);
                                   context.startActivity(intent);
                               }

                               @Override
                               protected void onRequestError(Throwable throwable) {
                                   super.onRequestError(throwable);
                                   GVideoSensorDataManager.getInstance().followCommunity(
                                           circle.groupId,
                                           circle.name,
                                           circle.tenantId,
                                           circle.tenantName,
                                           circle.labels,
                                           StatPid.getPageName(StatPid.CIRCLE_FIND),
                                           throwable.getMessage()
                                   );
                               }
                           }
                );
    }

    public int getCurrentPageNumber() {
        return mLocalPage.getPageNumber();
    }

    @Override
    public void onReload(@NonNull View view) {
        checkNetworkAndLoginStatus();
    }
}
