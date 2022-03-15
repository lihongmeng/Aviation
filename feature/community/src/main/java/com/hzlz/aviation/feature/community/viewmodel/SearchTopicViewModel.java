package com.hzlz.aviation.feature.community.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.adapter.SearchTopicAdapter;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class SearchTopicViewModel extends BaseRefreshLoadMoreViewModel {

    /**
     * 搜索词长度限制
     */
    private static final int SEARCH_WORD_LIMIT = 30;

    public Circle circle;
    public TopicDetail topicDetail;
    public SearchTopicAdapter searchTopicAdapter = new SearchTopicAdapter();
    /**
     * 搜索词
     */
    public CheckThreadLiveData<String> searchWord = new CheckThreadLiveData<>();
    ;
    private final CircleRepository circleRepository = new CircleRepository();

    public SearchTopicViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return searchTopicAdapter;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            searchTopicAdapter.replaceData(null);
            searchTopicAdapter.showNetworkNotAvailablePlaceholder();
        } else if (!UserManager.hasLoggedIn()) {
            searchTopicAdapter.replaceData(null);
            searchTopicAdapter.showUnLoginPlaceholder();
        } else {
            searchTopicAdapter.hidePlaceholder();
        }
    }

    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        String searchValue = searchWord.getValue();
        if (circle == null) {
            return;
        }
        if (searchValue != null && searchValue.length() > SEARCH_WORD_LIMIT) {
            showToast(R.string.search_history_text_limit);
            return;
        }
        circleRepository.searchTopic(
                (TextUtils.isEmpty(searchValue) ? "" : searchValue),
                circle.groupId,
                mLocalPage.getPageNumber(),
                20
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoLoadMoreObserver<TopicDetail>(view) {

                    @Override
                    protected void onSuccess(@Nullable List<TopicDetail> dataList) {
                        super.onSuccess(dataList);
                        view.finishGVideoLoadMore();
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        view.finishGVideoLoadMore();
                    }
                });

    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        String searchValue = searchWord.getValue();
        if (circle == null) {
            return;
        }
        if (searchValue != null && searchValue.length() > SEARCH_WORD_LIMIT) {
            showToast(R.string.search_history_text_limit);
            return;
        }
        circleRepository.searchTopic(
                searchValue,
                circle.groupId,
                1,
                20
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoRefreshObserver<>(view));
    }


}