package com.jxntv.account.ui.ugc;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.dialog.FollowCancelDialog;
import com.jxntv.account.dialog.UnfollowChatTipDialog;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.UserAuthor;
import com.jxntv.account.model.UserAuthorObservable;
import com.jxntv.account.model.annotation.RelationType;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.account.ui.relation.RelationFragmentArgs;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.InteractDataObservable;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

public class UgcDataBinding {

    @NonNull
    private final InteractionRepository mInteractionRepository = new InteractionRepository();

    public CheckThreadLiveData<Boolean> isFollowLiveData =new CheckThreadLiveData<>();

    private UserAuthor mFromAuthor;

    private String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setFromAuthor(UserAuthor author) {
        mFromAuthor = author;
    }

    private boolean mEditVisible = true;

    public void setEditVisible(boolean editVisible) {
        mEditVisible = editVisible;
    }

    public boolean editVisible() {
        return mEditVisible;
    }

    public void follow(
            @NonNull View view,
            @NonNull UserAuthorObservable author,
            @NonNull UserAuthor userAuthor
    ) {
        if (mFromAuthor == null) {
            return;
        }
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (TextUtils.isEmpty(accountPlugin.getToken())) {
            accountPlugin.startLoginActivity(view.getContext());
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(pid),
                    ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
            return;
        }

        if (author.self.get()) {
            onEditClick(view);
            return;
        }

        //如果已关注，那么关注按钮是私信功能，进入私信界面
        if (author.isFollowed.get()) {
            checkPermission(userAuthor.getId(), author.getName().get());
        } else {
            follow(author);
        }
    }

    public void onChatCheckClicked(View view, @NonNull UserAuthorObservable author, @NonNull UserAuthor userAuthor) {
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (TextUtils.isEmpty(accountPlugin.getToken())) {
            accountPlugin.startLoginActivity(view.getContext());
            GVideoSensorDataManager.getInstance().enterRegister(
                            StatPid.getPageName(pid),
                            ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (author.isFollowed.get()) {
            new FollowCancelDialog(view.getContext(), view1 -> {
                follow(author);
            }).show();
        } else {
            new UnfollowChatTipDialog(view.getContext(), view1 -> {
                mInteractionRepository
                        .followAuthor(mFromAuthor.getId(), mFromAuthor.getType(), mFromAuthor.getName(), !author.isFollowed.get())
                        .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                            @Override
                            protected void onRequestData(Boolean isFollow) {
                                author.isFollowed.set(isFollow);
                                isFollowLiveData.setValue(isFollow);
                                follow(view, author, userAuthor);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                GVideoSensorDataManager.getInstance().followAccount(!mFromAuthor.isFollow(), mFromAuthor.getId(),
                                        mFromAuthor.getName(), mFromAuthor.getType(), throwable.getMessage());
                            }
                        });
            }).show();
        }
    }

    private void follow(@NonNull UserAuthorObservable author) {
        mInteractionRepository
                .followAuthor(mFromAuthor.getId(), mFromAuthor.getType(), mFromAuthor.getName(), !author.isFollowed.get())
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onRequestData(Boolean isFollow) {
                        author.isFollowed.set(isFollow);
                        isFollowLiveData.setValue(isFollow);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().followAccount(!mFromAuthor.isFollow(), mFromAuthor.getId(),
                                mFromAuthor.getName(), mFromAuthor.getType(), throwable.getMessage());
                    }
                });
    }

    private void checkPermission(String toUserId, String toUserName) {
        mInteractionRepository.checkIMChatPermission(UserManager.getCurrentUser().getId())
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        PluginManager.get(ChatIMPlugin.class).startC2CChatActivity(toUserId, toUserName);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        ToastUtils.showShort(throwable.getMessage());
                    }
                });
    }

    public void onFollowCountClick(@NonNull View view) {
        if (mFromAuthor == null) return;
        Author author = Author.fromAuthorModel(mFromAuthor);
        RelationFragmentArgs
                args = new RelationFragmentArgs.Builder(author).setStart(RelationType.START_FOLLOW).build();
        Navigation.findNavController(view).navigate(R.id.relation_nav_graph, args.toBundle());
    }

    public void onFansCountClick(@NonNull View view) {
        if (mFromAuthor == null) return;
        Author author = Author.fromAuthorModel(mFromAuthor);
        RelationFragmentArgs args = new RelationFragmentArgs.Builder(author).setStart(RelationType.START_FANS).build();
        Navigation.findNavController(view).navigate(R.id.relation_nav_graph, args.toBundle());
    }

    public void onEditClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.profile_nav_graph);
    }

    public void onAvatarClick(@NonNull View view) {
        if (mFromAuthor.isSelf() && !TextUtils.isEmpty(UserManager.getCurrentUser().getAvatarUrl())) {
            mFromAuthor.setAvatar(UserManager.getCurrentUser().getAvatarUrl());
        }
        PluginManager.get(AccountPlugin.class).startAvatarPreviewActivity(view, mFromAuthor);
    }

    //提问
    public void onAskQuestionClick(View view) {
        try {
            Circle circle = new Circle();
            circle.groupId = Long.parseLong(mFromAuthor.getMentor().getGroupId());
            circle.name = mFromAuthor.getMentor().getGroupName();
            if (InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId).get()) {
                PluginManager.get(RecordPlugin.class).startQAPublishActivity(view.getContext(), circle, mFromAuthor, getPid());
            }else {
                PluginManager.get(CirclePlugin.class).joinCircle(circle,getPid(),true).subscribe(aBoolean -> {
                    if (aBoolean) {
                        PluginManager.get(RecordPlugin.class).startQAPublishActivity(view.getContext(), circle,
                                        mFromAuthor, getPid());
                    }
                });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}