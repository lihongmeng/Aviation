package com.jxntv.account.ui.modify.avatar;

import static com.jxntv.base.Constant.SELECT_AVATAR;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.adapter.AvatarAdapter;
import com.jxntv.account.model.AvatarInfo;
import com.jxntv.account.model.User;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.placeholder.PlaceholderListener;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;

import java.util.List;

/**
 * 修改头像 ViewModel
 *
 * @since 2020-02-06 20:57
 */
public final class ModifyAvatarViewModel extends BaseViewModel
        implements AvatarAdapter.Listener, PlaceholderListener {

    // 图片适配器
    public AvatarAdapter adapter = new AvatarAdapter();

    // UserRepository
    private final UserRepository mUserRepository = new UserRepository();

    // 获取到的默认头像列表
    public List<AvatarInfo> avatarInfoList;

    // 由上层传递进来的头像地址，用于恢复页面的选中状态
    public String avatarId;

    // 提交按钮的可用性
    public ObservableBoolean enableSubmit = new ObservableBoolean(false);

    // 当前用户信息
    public CheckThreadLiveData<User> userLiveData = new CheckThreadLiveData<>();

    // 标记这次启动选界面是否仅仅是选择一个图片便返回
    // 并不需要在点击确定的时候执行头像上传
    public boolean isJustSelect;

    // 当前选中的位置
    public int currentSelectPosition;

    public ModifyAvatarViewModel(@NonNull Application application) {
        super(application);
        adapter.setListener(this);
        adapter.setPlaceHolderListener(this);
    }

    public void loadData() {
        loadCurrentUser();
        loadAvatarList();
    }

    public void loadCurrentUser() {
        mUserRepository.getCurrentUser(false).subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected void onSuccess(@NonNull User user) {
                userLiveData.setValue(user);
            }

            @Override
            protected boolean isShowPlaceholderLayout() {
                return true;
            }

        });
    }

    private void addData() {
        adapter.addData(avatarInfoList);

        int size = avatarInfoList.size();

        if (avatarId == null || TextUtils.isEmpty(avatarId)) {
            currentSelectPosition = 0;

            for (int index = 0; index < size; index++) {
                if (index == 0) {
                    avatarInfoList.get(index)
                            .getAvatarInfoObservable()
                            .checkVisibility.set(View.VISIBLE);
                } else {
                    avatarInfoList.get(index)
                            .getAvatarInfoObservable()
                            .checkVisibility.set(View.GONE);
                }
            }
            return;
        }

        for (int index = 0; index < size; index++) {
            AvatarInfo avatarInfo = avatarInfoList.get(index);
            if (avatarInfo == null) {
                continue;
            }
            if (TextUtils.equals(avatarInfo.getId(), avatarId)) {
                currentSelectPosition = index;
                avatarInfoList.get(index)
                        .getAvatarInfoObservable()
                        .checkVisibility.set(View.VISIBLE);
            } else {
                avatarInfoList.get(index)
                        .getAvatarInfoObservable()
                        .checkVisibility.set(View.GONE);
            }
        }

    }

    public void loadAvatarList() {
        if (!avatarInfoList.isEmpty()) {
            addData();
            adapter.setCurrentSelectPosition(currentSelectPosition);
            return;
        }
        mUserRepository.getAvatarList().subscribe(
                new GVideoResponseObserver<List<AvatarInfo>>() {

                    @Override
                    protected void onSuccess(@NonNull List<AvatarInfo> netData) {
                        avatarInfoList = netData;
                        addData();
                        adapter.setCurrentSelectPosition(currentSelectPosition);
                    }

                    @Override
                    protected void onAPIError(@NonNull Throwable throwable) {
                        super.onAPIError(throwable);
                        adapter.showErrorPlaceholder();
                    }

                    @Override
                    protected void onNetworkNotAvailableError(@NonNull Throwable throwable) {
                        super.onNetworkNotAvailableError(throwable);
                        adapter.showNetworkNotAvailablePlaceholder();
                    }

                }
        );
    }


    public void modifyAvatar(@NonNull View view) {
        if (currentSelectPosition < 0) {
            return;
        }
        List<AvatarInfo> avatarInfoList = adapter.getData();
        if (avatarInfoList == null || avatarInfoList.isEmpty()) {
            return;
        }
        AvatarInfo avatarInfo = avatarInfoList.get(currentSelectPosition);
        if (avatarInfo == null) {
            return;
        }
        String avatarId = avatarInfo.getId();
        String avatarUrl = avatarInfo.getUrl();
        if (avatarId == null
                || avatarUrl == null
                || TextUtils.isEmpty(avatarId)
                || TextUtils.isEmpty(avatarUrl)
        ) {
            return;
        }
        if (isJustSelect) {
            GVideoEventBus.get(SELECT_AVATAR).post(new AvatarInfo(avatarId, avatarUrl));
            Navigation.findNavController(view).popBackStack();
            return;
        }
        mUserRepository.modifyUserAvatar(avatarId)
                .subscribe(new GVideoResponseObserver<User>() {
                    @Override
                    protected void onSuccess(@NonNull User user) {
                        Navigation.findNavController(view).popBackStack();
                        GVideoEventBus.get(AccountPlugin.EVENT_AVATAR_UPDATE).post(null);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        showToast(R.string.all_network_not_available_action_tip);
                    }
                });
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull AvatarAdapter adapter, int position) {
        enableSubmit.set(true);
        currentSelectPosition = position;
    }

    @Override
    public void onReload(@NonNull View view) {
        loadAvatarList();
    }

    @Override
    public void onLogin(@NonNull View view) {
    }

}
