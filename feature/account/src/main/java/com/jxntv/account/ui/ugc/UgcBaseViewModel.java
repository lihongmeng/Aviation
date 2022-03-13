package com.jxntv.account.ui.ugc;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.UGCMenuTabModel;
import com.jxntv.account.model.UgcAuthorModel;
import com.jxntv.account.model.User;
import com.jxntv.account.model.UserAuthor;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

import java.util.List;
import java.util.Objects;


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
