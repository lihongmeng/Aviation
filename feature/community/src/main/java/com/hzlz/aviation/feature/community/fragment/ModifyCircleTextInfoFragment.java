package com.hzlz.aviation.feature.community.fragment;

import static com.hzlz.aviation.feature.community.viewmodel.ModifyCircleTextInfoViewModel.ModifyType.CIRCLE_INTRODUCTION;
import static com.hzlz.aviation.feature.community.viewmodel.ModifyCircleTextInfoViewModel.ModifyType.CIRCLE_NAME;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_MODIFY_INFO;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_MODIFY_TYPE;
import static com.hzlz.aviation.kernel.base.plugin.CirclePlugin.MODIFY_CIRCLE_INFO;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentModifyCircleTextInfoBinding;
import com.hzlz.aviation.feature.community.model.ModifyCircleTextInfo;
import com.hzlz.aviation.feature.community.viewmodel.ModifyCircleTextInfoViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.util.ResourcesUtils;

public class ModifyCircleTextInfoFragment extends BaseFragment<FragmentModifyCircleTextInfoBinding> {

    private ModifyCircleTextInfoViewModel viewModel;

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        finishActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_circle_text_info;
    }

    @Override
    protected void initView() {
        showRightOperationTextView(true);
        openSoftKeyBoardDelay(mBinding.edit);

        mBinding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    viewModel.currentEditTextContent = "";
                    updateCounterAndRight();
                    return;
                }
                viewModel.currentEditTextContent = s.toString().trim();
                updateCounterAndRight();
            }
        });
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(ModifyCircleTextInfoViewModel.class);
        Bundle argument = getArguments();
        if (argument == null) {
            return;
        }
        viewModel.initContent = argument.getString(EXTRA_MODIFY_INFO);
        viewModel.modifyType = argument.getInt(EXTRA_MODIFY_TYPE, 0);

        if (TextUtils.isEmpty(viewModel.initContent)) {
            viewModel.currentEditTextContent = "";
        } else {
            viewModel.currentEditTextContent = viewModel.initContent.trim();
        }

        mBinding.edit.setText(viewModel.currentEditTextContent);

        switch (viewModel.modifyType) {
            case CIRCLE_NAME:
                showRightOperationTextView(true);
                setToolbarTitle(getString(R.string.modify_circle_name));
                setRightOperationTextViewText(R.string.complete);
                viewModel.setExitTextContentMaxLength(10);
                mBinding.edit.setHint(ResourcesUtils.getString(R.string.please_input_circle_name));
                break;
            case CIRCLE_INTRODUCTION:
                showRightOperationTextView(true);
                setToolbarTitle(getString(R.string.modify_circle_introduction));
                setRightOperationTextViewText(R.string.complete);
                viewModel.setExitTextContentMaxLength(50);
                mBinding.edit.setHint(ResourcesUtils.getString(R.string.please_input_circle_introduction));
                break;
            default:
                showRightOperationTextView(false);
        }
        updateCounterAndRight();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        super.onRightOperationPressed(view);

        GVideoEventBus.get(MODIFY_CIRCLE_INFO).post(
                new ModifyCircleTextInfo(
                        viewModel.modifyType,
                        (TextUtils.isEmpty(viewModel.currentEditTextContent) ? "" : viewModel.currentEditTextContent.trim())
                )
        );
        finishActivity();
    }

    /**
     * ????????????
     */
    @SuppressLint("SetTextI18n")
    private void updateCounterAndRight() {
        int length = viewModel.currentEditTextContent == null ? 0 : viewModel.currentEditTextContent.length();
        int exitTextContentMaxLength = viewModel.getExitTextContentMaxLength();
        mBinding.textViewCounter.setText(length + "/" + exitTextContentMaxLength);
        if (length == 0 || length >= exitTextContentMaxLength) {
            mBinding.textViewCounter.setTextColor(ResourcesUtils.getColor(R.color.color_fc284d));
        } else {
            mBinding.textViewCounter.setTextColor(ResourcesUtils.getColor(R.color.color_999999));
        }

        mBinding.textViewCounter.setText(length + "/" + exitTextContentMaxLength);
        enableRightOperationTextView(
                !TextUtils.isEmpty(viewModel.currentEditTextContent)
                        && !viewModel.initContent.equals(viewModel.currentEditTextContent)
                        && length <= exitTextContentMaxLength
        );
    }
}
