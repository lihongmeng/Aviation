package com.jxntv.account.ui.follow;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.account.R;
import com.jxntv.account.adapter.HomeFollowAdapter;
import com.jxntv.account.model.Author;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.ListPageUtil;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.network.NetworkUtils;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;


public class HomeFollowListViewModel extends BaseRefreshLoadMoreViewModel {

    private final InteractionRepository interactionRepository;
    public HomeFollowAdapter adapter;
    public CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();
    public CheckThreadLiveData<Boolean> hasFollowAtLeastOne = new CheckThreadLiveData<>(false);

    private final ListPageUtil<Author> util = new ListPageUtil<>();

    public HomeFollowListViewModel(@NonNull Application application) {
        super(application);
        interactionRepository = new InteractionRepository();
        adapter = new HomeFollowAdapter();
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return adapter;
    }


    public void follow(final Author author) {
        interactionRepository.followAuthor(
                author.getId(),
                author.getType(),
                author.getName(),
                !author.isFollow()
        ).subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
            @Override
            protected void onRequestData(@NonNull Object result) {
                adapter.updateSingle(author, (Boolean) result);
                hasFollowAtLeastOne.setValue(adapter.hasFollowAtLeastOne());
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                showToast(throwable.getMessage());
                GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(),author.getId(),
                        author.getName(),author.getType(),throwable.getMessage());
            }
        });
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            adapter.replaceData(null);
            adapter.showNetworkNotAvailablePlaceholder();
        } else {
            adapter.hidePlaceholder();
            mAutoRefreshLiveData.setValue(true);
        }
    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        Boolean temp = hasFollowAtLeastOne.getValue();
        if (temp != null && temp) {
            GVideoEventBus.get(AccountPlugin.EVENT_SHOW_FOLLOW_CONTENT_LIST).post(null);
        }else{

            // 如果到最后一页，下拉刷新，就回到第一页
            if(!util.hasNextPage()){
                util.updatePageNumber(0);
                List<Author> authorList = util.getCurrentPageData();
                HomeFollowAdapter adapter = (HomeFollowAdapter) getAdapter();
                adapter.replaceData(authorList);
            }
        }
        view.finishGVideoRefresh();
    }

    public void updateRecommendFollowList() {
        interactionRepository.getRecommendFollowList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoResponseObserver<List<Author>>() {

                    @Override
                    protected void onSuccess(@NonNull List<Author> authors) {
                        util.updateDataSource(Author.filterValidData(authors));
                        List<Author> authorList = util.getCurrentPageData();
                        if (authorList == null || authorList.isEmpty()) {
                            authorList = new ArrayList<>();
                        }
                        HomeFollowAdapter adapter = (HomeFollowAdapter) getAdapter();
                        // 添加数据
                        adapter.replaceData(authorList);
                    }

                });
    }

    public void onBatchReplaceClick(@NonNull IGVideoRefreshLoadMoreView view) {
        List<Author> authorList = util.getNextPageAndAdd();
        if (authorList == null || authorList.isEmpty()) {
            util.updatePageNumber(0);
            authorList = util.getCurrentPageData();
        }
        HomeFollowAdapter adapter = (HomeFollowAdapter) getAdapter();
        // 添加数据
        adapter.replaceData(authorList);
    }

    public void onKeyFollow() {
        if (adapter == null) {
            return;
        }
        List<Author> authorList = util.getCurrentPageData();
        interactionRepository.oneKeyFollowRequest(authorList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoResponseObserver<Object>() {
                    @Override
                    protected void onSuccess(@NonNull Object o) {
                        showToast(ResourcesUtils.getString(R.string.follow_success));
                        GVideoEventBus.get(AccountPlugin.EVENT_SHOW_FOLLOW_CONTENT_LIST).post(null);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        showToast(throwable.getMessage());
                    }
                });
    }

}