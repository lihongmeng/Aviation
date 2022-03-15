package com.hzlz.aviation.feature.live.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.live.bean.AudienceOperation;
import com.hzlz.aviation.feature.live.databinding.DialogAudienceOperationBinding;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

public class AudienceOperationDialog extends GVideoBottomSheetDialog {

    private String name;
    private final List<AudienceOperation> audienceOperationList = new ArrayList<>();
    private DialogAudienceOperationBinding mLayoutBinding;

    public AudienceOperationDialog(Context context) {
        super(context);
    }

    public void update(String name, List<AudienceOperation> audienceOperationLis) {
        this.name = TextUtils.isEmpty(name) ? "" : name;
        this.audienceOperationList.clear();
        if (audienceOperationLis != null && !audienceOperationLis.isEmpty()) {
            this.audienceOperationList.addAll(audienceOperationLis);
        }

        View rootView = onCreateView(LayoutInflater.from(getContext()), null, null);
        setContentView(rootView);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        mLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_audience_operation,
                container,
                false
        );

        String result = "对 " + name + " 的操作";
        int headerIndex = result.indexOf("对 ");
        int endIndex = result.lastIndexOf(" 的");
        SpannableString spannableString = new SpannableString(result);
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_79bcea)),
                headerIndex,
                endIndex,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        mLayoutBinding.title.setText(spannableString);

        mLayoutBinding.cancel.setOnClickListener((v) -> dismiss());

        mLayoutBinding.operation.removeAllViews();
        for (AudienceOperation audienceOperation : audienceOperationList) {
            if (audienceOperation == null) {
                continue;
            }
            GVideoTextView itemView = (GVideoTextView) inflater.inflate(
                    R.layout.item_audience_operation, container, false);
            itemView.setText(TextUtils.isEmpty(audienceOperation.operation) ? "" : audienceOperation.operation);
            itemView.setOnClickListener(audienceOperation.clickListener);
            mLayoutBinding.operation.addView(itemView,new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SizeUtils.dp2px(58)
            ));
        }

        return mLayoutBinding.getRoot();


    }

}
