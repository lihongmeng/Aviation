package com.hzlz.aviation.feature.account.ui.follow;

import static com.hzlz.aviation.feature.account.ui.follow.HomeFollowContentListViewModel.LOAD_DATA_TIME_LIMIT;
import static com.hzlz.aviation.kernel.media.MediaConstants.DEFAULT_PAGE_COUNT;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.AuthorRepository;
import com.hzlz.aviation.feature.account.repository.InteractionRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFollowViewModel extends BaseViewModel {

    public CheckThreadLiveData<Integer> followCount = new CheckThreadLiveData<>();
    public CheckThreadLiveData<Boolean> showRecommend = new CheckThreadLiveData<>();

    // 外层需要根据 followCount 的值以及这个变量，来判断界面该怎样显示
    public boolean forceShowNoData = true;

    public HomeFollowViewModel(@NonNull Application application) {
        super(application);
    }

    public void initData() {
        if (forceShowNoData) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        if (!NetworkUtils.isNetworkConnected()) {
            forceShowNoData = true;
            followCount.setValue(0);
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            return;
        }
        if (!UserManager.hasLoggedIn()) {
            forceShowNoData = false;
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            followCount.setValue(0);
            return;
        }
        new AuthorRepository()
                .getMyFollowList(1, 10)
                .subscribe(new BaseViewModel.GVideoResponseObserver<ListWithPage<UgcAuthorModel>>() {

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<UgcAuthorModel> listWithPage) {
                        forceShowNoData = false;
                        List<UgcAuthorModel> authorModelList = listWithPage.getList();
                        if (authorModelList == null) {
                            loadAlreadyFollowDataList();
                        } else {
                            int size = authorModelList.size();
                            if (size == 0) {
                                loadAlreadyFollowDataList();
                            } else {
                                followCount.setValue(size);
                                updatePlaceholderLayoutType(PlaceholderType.NONE);
                            }
                        }
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        if (forceShowNoData) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        }
                    }
                });
    }

    /**
     * 如果拉取下来的已关注人数组为空，这个时候需要在获取已关注人发布的内容数据
     * 因为此时可能需要展示用户自己发布的内容
     */
    private void loadAlreadyFollowDataList() {
        new InteractionRepository().getAlreadyFollowContentList(1, DEFAULT_PAGE_COUNT)
                .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {
                        List<MediaModel> mediaModelList = mediaListWithPage.getList();
                        if (mediaModelList == null || mediaModelList.isEmpty()) {
                            followCount.setValue(0);
                        } else {
                            followCount.setValue(-1);
                        }
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        followCount.setValue(0);
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    }
                });
    }

}
