package com.hzlz.aviation.feature.account.ui.modify.nickname;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentModifyNicknameBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.utils.StringUtils;

/**
 * 修改昵称界面
 *
 * @since 2020-01-21 16:48
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ModifyNicknameFragment extends BaseFragment<FragmentModifyNicknameBinding> {
    //<editor-fold desc="属性">
    private ModifyNicknameViewModel viewModel;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_nickname;
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.fragment_modify_nickname_title);
        setRightOperationTextViewText(R.string.fragment_modify_nickname_save);
        showRightOperationTextView(true);
        openSoftKeyBoardDelay(mBinding.editTextNickname);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(ModifyNicknameViewModel.class);
        mBinding.setViewModel(viewModel);
        mBinding.setBinding(viewModel.dataBinding);
        viewModel.getModifyNicknameLivaData()
                .observe(this, new NotNullObserver<Boolean>() {
                    @Override
                    protected void onModelChanged(@NonNull Boolean enable) {
                        enableRightOperationTextView(enable);
                    }
                });
    }

    @Override
    protected void loadData() {
        // 读取参数
        Bundle arguments = getArguments();
        if (arguments != null) {
            String nickName = ModifyNicknameFragmentArgs.fromBundle(getArguments()).getNickname();
            nickName = (nickName == null || TextUtils.isEmpty(nickName)) ? "" : nickName;

            viewModel.setNickname(nickName);

        }
    }
    //</editor-fold>

    //<editor-fold desc="控件事件监听">
    @Override
    public void onRightOperationPressed(@NonNull View view) {
        super.onRightOperationPressed(view);

        String nickName = viewModel.dataBinding.nickname.get();
        nickName = (nickName == null || TextUtils.isEmpty(nickName)) ? "" : nickName;

        if(TextUtils.isEmpty(nickName.trim())){
            showToast("昵称不能全为空格");
            return;
        }

        if (StringUtils.isNumeric(nickName)) {
            showToast("昵称不能为纯数字");
            return;
        }

        // if (!TextUtils.equals(nickName, nickName.trim())) {
        //     showToast("昵称首尾不能为空格");
        //     return;
        // }

        viewModel.modifyNickname(view);
    }
    //</editor-fold>

    //<editor-fold desc="生命周期">
    @Override
    public void onDestroyView() {
        closeSoftKeyboard();
        super.onDestroyView();
    }
    //</editor-fold>
}
