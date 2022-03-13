package com.jxntv.circle.viewmodel;

import static com.jxntv.media.MediaConstants.DEFAULT_PAGE_COUNT;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.circle.MyCircle;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.adapter.SelectCircleAdapter;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class CircleSelectViewModel extends BaseRefreshLoadMoreViewModel {

    // CircleRepository
    private final CircleRepository circleRepository;

    // 圈子信息列表适配器
    public SelectCircleAdapter selectCircleAdapter;

    // 最终结果
    public List<Circle> result;

    // 通知UI层社区加入成功
    public CheckThreadLiveData<Circle> joinCircleSuccess = new CheckThreadLiveData<>();

    // 是否需要拉取
    private boolean needGetOtherCircle;

    public CheckThreadLiveData<Boolean> showTip = new CheckThreadLiveData<>();

    public CircleSelectViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
        selectCircleAdapter = new SelectCircleAdapter();
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return selectCircleAdapter;
    }

    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        loadCircleInfo(view);
    }

    public void loadMyCircle(@NonNull IGVideoRefreshLoadMoreView view) {
        showNetworkDialog("正在获取社区信息...");
        circleRepository.myCircle()
                .timeout(CircleRepository.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<MyCircle>() {

                               @Override
                               protected void onSuccess(@NonNull MyCircle netData) {
                                   hideNetworkDialog();
                                   if (netData.groupList == null || netData.groupList.isEmpty()) {
                                       needGetOtherCircle = true;
                                       loadCircleInfo(view);
                                   } else {
                                       showTip.setValue(false);
                                       result = netData.groupList;
                                       selectCircleAdapter.replaceData(result);
                                       updatePlaceholderLayoutType(PlaceholderType.NONE);
                                   }
                               }

                               @Override
                               public void onFailed(Throwable throwable) {
                                   showTip.setValue(false);
                                   updatePlaceholderLayoutType(PlaceholderType.NONE);
                               }
                           }
                );
    }

    public void loadCircleInfo(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        if (!needGetOtherCircle) {
            view.finishGVideoLoadMore();
            return;
        }
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
                        hideNetworkDialog();

                        if (selectCircleAdapter.getItemCount() <= 0) {
                            showTip.setValue(false);
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            showTip.setValue(true);
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        view.finishGVideoLoadMore();
                        hideNetworkDialog();

                        if (selectCircleAdapter.getItemCount() <= 0) {
                            showTip.setValue(false);
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        } else {
                            showTip.setValue(true);
                            updatePlaceholderLayoutType(PlaceholderType.NONE);
                        }
                    }
                });

    }

    public void joinCircle(Activity activity, Circle circle) {
        showNetworkDialog("正在处理...");
        circleRepository.joinCircle(circle.groupId, "")
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
                               @Override
                               protected void onRequestData(@NonNull Object result) {
                                   hideNetworkDialog();
                                   if (activity == null) {
                                       return;
                                   }
                                   GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class).post(null);
                                   GVideoSensorDataManager.getInstance().followCommunity(
                                           circle.groupId,
                                           circle.name,
                                           circle.tenantId,
                                           circle.tenantName,
                                           circle.labels,
                                           StatPid.getPageName(StatPid.CIRCLE_FIND),
                                           null
                                   );
                                   circle.setJoin(true);
                                   joinCircleSuccess.setValue(circle);
                               }

                               @Override
                               protected void onRequestError(Throwable throwable) {
                                   hideNetworkDialog();
                                   super.onRequestError(throwable);
                                   showToast("选择社区失败，请稍后重试");
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

}
