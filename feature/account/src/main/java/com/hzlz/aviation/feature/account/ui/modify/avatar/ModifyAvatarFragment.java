package com.hzlz.aviation.feature.account.ui.modify.avatar;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentModifyAvatarBinding;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.decoration.GapItemDecoration;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.library.util.ResourcesUtils;

import java.util.ArrayList;

/**
 * 修改头像界面
 *
 * @since 2020-02-06 20:52
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ModifyAvatarFragment extends BaseFragment<FragmentModifyAvatarBinding> {

    // ModifyAvatarViewModel
    private ModifyAvatarViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_avatar;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.submit_avatar);
        GapItemDecoration decoration = new GapItemDecoration();
        decoration.setVerticalGap((int) ResourcesUtils.getDimens(R.dimen.margin_fragment_modify_avatar_default));
        decoration.setItemWidthForGridLayoutManager((int) ResourcesUtils.getDimens(R.dimen.size_adapter_avatar_container));
        mBinding.recyclerView.addItemDecoration(decoration);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(ModifyAvatarViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.userLiveData.observe(
                this,
                new NotNullObserver<User>() {
                    @Override
                    protected void onModelChanged(@NonNull User user) {
                        mBinding.setUser(user.getUserObservable());
                    }
                }
        );

        Bundle arguments = getArguments();
        if (arguments != null) {
            viewModel.avatarInfoList = arguments.getParcelableArrayList(AccountPlugin.DEFAULT_AVATAR_LIST);
            viewModel.avatarId = arguments.getString(Constant.AVATAR_URL);
            viewModel.isJustSelect = arguments.getBoolean(Constant.IS_JUST_SELECT);
        }
        if (viewModel.avatarInfoList == null) {
            viewModel.avatarInfoList = new ArrayList<>();
        }

        viewModel.loadData();
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        viewModel.loadCurrentUser();
        viewModel.loadAvatarList();
    }

}
