package com.hzlz.aviation.feature.community.fragment.fxa;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentFxaBinding;
import com.hzlz.aviation.feature.community.dialog.FXAMessageConfirmDialog;
import com.hzlz.aviation.feature.community.model.FXAModel;
import com.hzlz.aviation.feature.community.model.FXAType;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;

/**
 * @author huangwei
 * date : 2021/7/29
 * desc : 放心爱详情
 **/
public class FXAFragment extends BaseFragment<FragmentFxaBinding> {

    private FXAMessageConfirmDialog confirmDialog;
    private FXAViewModel viewModel;
    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fxa;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected void initView() {
        confirmDialog = new FXAMessageConfirmDialog(getContext());
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        id = bundle.getString(CirclePlugin.INTENT_CIRCLE_ID);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(FXAViewModel.class);
        viewModel.initData(id);
        SharedPrefsWrapper wrapper = new SharedPrefsWrapper(this.getClass().getName());
        viewModel.dataLiveData.observe(this, new Observer<FXAModel>() {
            @Override
            public void onChanged(FXAModel model) {
                setToolbarTitle(model.getName());
                mBinding.setModel(model);
                mBinding.setViewModel(viewModel);

                String key = model.getMember().getId() + "_" + model.getId();
                if (!wrapper.getBoolean(key, false)) {
                    confirmDialog.setData(model, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wrapper.putBoolean(key, true);
                        }
                    });
                    confirmDialog.show();
                }

                if (model.getMember().getCurrentPart().getPartType() == FXAType.PAIR) {
                    mBinding.input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            boolean f = viewModel.canInput.getValue() && editable.toString().length() > 0;
                            mBinding.signBtn.setEnabled(f);
                            mBinding.signBtn.getHelper().setBackgroundColorNormal(ContextCompat.getColor(getContext(),
                                    f ? R.color.color_e4344e : R.color.color_d8d8d8));

                        }
                    });
                } else {
                    mBinding.signBtn.setEnabled(model.isCanClick());
                    mBinding.signBtn.getHelper().setBackgroundColorNormal(ContextCompat.getColor(getContext(),
                            model.isCanClick() ? R.color.color_e4344e : R.color.color_d8d8d8));
                }
            }
        });

        viewModel.canInput.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                int dp20 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_20DP);
                if (!aBoolean) {
                    int size = 0;
                    if (!TextUtils.isEmpty(viewModel.inputNumber.get())) {
                        size = 3 - viewModel.inputNumber.get().length();
                    }
                    mBinding.input.setPadding(dp20 * size, dp20, 0, 0);
                }
            }
        });


    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        getActivity().finish();
    }
}
