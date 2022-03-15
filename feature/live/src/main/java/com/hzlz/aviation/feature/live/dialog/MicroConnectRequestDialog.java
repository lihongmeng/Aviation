package com.hzlz.aviation.feature.live.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.live.callback.OnMicroConnectRequestListener;
import com.hzlz.aviation.feature.live.databinding.DialogMicroConnectRequestBinding;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.live.R;

/**
 * 观众端连麦弹窗
 */
public class MicroConnectRequestDialog extends GVideoBottomSheetDialog {

    private DialogMicroConnectRequestBinding mLayoutBinding;

    private OnMicroConnectRequestListener onMicroConnectRequestListener;

    private boolean hasFillReason;

    public MicroConnectRequestDialog(Context context) {
        super(context);
    }

    public void setUp(boolean hasFillReason, OnMicroConnectRequestListener onMicroConnectRequestListener) {
        this.hasFillReason = hasFillReason;
        this.onMicroConnectRequestListener = onMicroConnectRequestListener;
        View rootView = onCreateView(LayoutInflater.from(getContext()), null, null);
        setContentView(rootView);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        mLayoutBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_micro_connect_request,
                container,
                false
        );

        if (hasFillReason) {
            showWaitOnly();
        } else {
            showWaitAndCanAddReason();
        }

        // 显示当前输入的字数
        mLayoutBinding.reason.addTextChangedListener(new TextWatcher() {

            int maxLength = 200;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mLayoutBinding.reasonTextCount.setText("0/200");
                    mLayoutBinding.submit.setEnabled(false);
                    mLayoutBinding.submit.setTextColor(ResourcesUtils.getColor(R.color.color_f7f7f7));
                } else {
                    int length = s.toString().length();
                    mLayoutBinding.reasonTextCount.setText(length + "/200");
                    if (length < 5) {
                        mLayoutBinding.submit.setEnabled(false);
                        mLayoutBinding.submit.setTextColor(ResourcesUtils.getColor(R.color.color_f7f7f7));
                    } else {
                        mLayoutBinding.submit.setEnabled(true);
                        mLayoutBinding.submit.setTextColor(ResourcesUtils.getColor(R.color.color_1c88d2));

                    }

                }
            }
        });

        mLayoutBinding.addJoinReason.setOnClickListener(
                v -> showWaitAndAddReason()
        );

        mLayoutBinding.cancelRequest.setOnClickListener(v -> {
            if (onMicroConnectRequestListener == null) {
                return;
            }
            onMicroConnectRequestListener.onCancelRequestClick();
        });

        mLayoutBinding.cancel.setOnClickListener(v -> {
            dismiss();
        });

        mLayoutBinding.submit.setOnClickListener(v -> {
            if (onMicroConnectRequestListener == null) {
                return;
            }
            Editable editable = mLayoutBinding.reason.getText();
            onMicroConnectRequestListener.onSubmitClick(TextUtils.isEmpty(editable) ? "" : editable.toString());
        });

        return mLayoutBinding.getRoot();
    }

    private void showWaitAndCanAddReason() {
        mLayoutBinding.addJoinReason.setVisibility(View.VISIBLE);
        mLayoutBinding.addJoinReasonTip.setVisibility(View.VISIBLE);

        mLayoutBinding.reason.setVisibility(View.GONE);
        mLayoutBinding.reasonTextCount.setVisibility(View.GONE);
        mLayoutBinding.dividerHorizontal.setVisibility(View.GONE);
        mLayoutBinding.dividerVertical.setVisibility(View.GONE);
        mLayoutBinding.cancel.setVisibility(View.GONE);
        mLayoutBinding.submit.setVisibility(View.GONE);

        mLayoutBinding.statusWait.setVisibility(View.GONE);
    }

    private void showWaitAndAddReason() {
        mLayoutBinding.addJoinReason.setVisibility(View.GONE);
        mLayoutBinding.addJoinReasonTip.setVisibility(View.GONE);

        mLayoutBinding.reason.setVisibility(View.VISIBLE);
        mLayoutBinding.reasonTextCount.setVisibility(View.VISIBLE);
        mLayoutBinding.dividerHorizontal.setVisibility(View.VISIBLE);
        mLayoutBinding.dividerVertical.setVisibility(View.VISIBLE);
        mLayoutBinding.cancel.setVisibility(View.VISIBLE);
        mLayoutBinding.submit.setVisibility(View.VISIBLE);

        mLayoutBinding.statusWait.setVisibility(View.GONE);
    }

    private void showWaitOnly() {
        mLayoutBinding.addJoinReason.setVisibility(View.GONE);
        mLayoutBinding.addJoinReasonTip.setVisibility(View.GONE);

        mLayoutBinding.reason.setVisibility(View.GONE);
        mLayoutBinding.reasonTextCount.setVisibility(View.GONE);
        mLayoutBinding.dividerHorizontal.setVisibility(View.GONE);
        mLayoutBinding.dividerVertical.setVisibility(View.GONE);
        mLayoutBinding.cancel.setVisibility(View.GONE);
        mLayoutBinding.submit.setVisibility(View.GONE);

        mLayoutBinding.statusWait.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            //隐藏软件盘
            SoftInputUtils.hideSoftInput(currentFocus.getWindowToken(), getContext());
        }
        super.dismiss();
    }

}
