package com.hzlz.aviation.feature.account.ui.ugc;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.UGCMenuTabModel;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.feature.account.model.UserAuthor;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.AuthorRepository;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.List;


/**
 * @author huangwei
 * date : 2021/6/17
 * desc :
 **/
public class UgcBaseViewModel extends BaseViewModel {

    private String mAuthorId;

    @NonNull
    protected CheckThreadLiveData<UserAuthor> mAuthorLiveDta = new CheckThreadLiveData<>();
    @NonNull
    protected CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();
    @NonNull
    protected CheckThreadLiveData<List<UgcAuthorModel>> mMyCircleList = new CheckThreadLiveData<>();
    @NonNull
    protected CheckThreadLiveData<UGCMenuTabModel> mUGCMenuTabModelLiveData = new CheckThreadLiveData<>();
    @NonNull
    protected AuthorRepository mAuthorRepository = new AuthorRepository();
    @NonNull
    protected UserRepository mUserRepository = new UserRepository();
    @NonNull
    protected UgcDataBinding mUgcDataBinding = new UgcDataBinding();

    @NonNull
    public LiveData<UserAuthor> getAuthorLiveData() {
        return mAuthorLiveDta;
    }

    @NonNull
    public LiveData<Boolean> getAutoRefreshLiveData() {
        return mAutoRefreshLiveData;
    }

    @NonNull
    public LiveData<List<UgcAuthorModel>> getMyCircleList() {
        return mMyCircleList;
    }

    @NonNull
    public LiveData<UGCMenuTabModel> getUGCMenuTabModelLiveData() {
        return mUGCMenuTabModelLiveData;
    }

    public UgcBaseViewModel(@NonNull Application application) {
        super(application);
    }

    public void setPid(String pid){
        mUgcDataBinding.setPid(pid);
    }

    public void setAuthorId(String authorId) {
        mAuthorId = authorId;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
        } else if (!UserManager.hasLoggedIn()) {
            updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
            mAutoRefreshLiveData.setValue(true);
        }
    }

    public UgcDataBinding getDataBinding() {
        mUgcDataBinding.setEditVisible(true);
        return mUgcDataBinding;
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {

        Author author = new Author();
        author.setId(mAuthorId);
        author.setType(AuthorType.UGC);
        mAuthorRepository.getUserAuthorById(author).subscribe(new GVideoResponseObserver<UserAuthor>() {
            @Override
            protected void onSuccess(@NonNull UserAuthor author) {
                updatePlaceholderLayoutType(PlaceholderType.NONE);
                if (author.isSelf() && !TextUtils.isEmpty(UserManager.getCurrentUser().getAvatarUrl())){
                    author.setAvatar(UserManager.getCurrentUser().getAvatarUrl());
                }
                mUgcDataBinding.setFromAuthor(author);
                mAuthorLiveDta.setValue(author);
                view.finishGVideoRefresh();
                getMyCircleListMessage();
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                updatePlaceholderLayoutType(PlaceholderType.ERROR);
                view.finishGVideoRefresh();
            }
        });

        mAuthorRepository.getUGCMenuTab(mAuthorId).subscribe(new GVideoResponseObserver<UGCMenuTabModel>() {
            @Override
            protected void onSuccess(@NonNull UGCMenuTabModel ugcMenuTabModel) {
                mUGCMenuTabModelLiveData.setValue(ugcMenuTabModel);
            }
        });

        mAuthorRepository.getPlatformAccountData();

    }

    private void getMyCircleListMessage() {

        //我的圈子
        mAuthorRepository.getMyCircleFollowList(mAuthorLiveDta.getValue().getId(), 1, false)
                .subscribe(new GVideoResponseObserver<ListWithPage<UgcAuthorModel>>() {
                    @Override
                    protected void onSuccess(@NonNull ListWithPage<UgcAuthorModel> listWithPage) {
                        mMyCircleList.setValue(listWithPage.getList());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });

//        //我的关注
//        mAuthorRepository.getMyCircleFollowList(mAuthorLiveDta.getValue().getId(), 1, true)
//                .subscribe(new GVideoResponseObserver<ListWithPage<UgcAuthorModel>>() {
//                    @Override
//                    protected void onSuccess(@NonNull ListWithPage<UgcAuthorModel> listWithPage) {
//                        mMyFollowList.setValue(listWithPage.getList());
//                    }
//                });
    }

    public void onShareClicked(View v){
        if (mAuthorLiveDta.getValue()==null){
            return;
        }

        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setTitle(mAuthorLiveDta.getValue().getName())
                .setDescription(v.getContext().getString(R.string.share_default_description))
                .setImage(mAuthorLiveDta.getValue().getAvatar())
                .setUrl(mAuthorLiveDta.getValue().getShareUrl())
                .setShowCreateBill(false)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false,
                true, shareDataModel, null);
    }


}
