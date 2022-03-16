package com.hzlz.aviation.feature.record.recorder.dialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
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

import com.hzlz.aviation.feature.record.databinding.DialogPublishFillLinkBinding;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.record.R;

public class LinkAnalysisDialog extends GVideoBottomSheetDialog {

    // 布局文件
    private DialogPublishFillLinkBinding mLayoutBinding;

    // // 这个值意义在于代表用户想不想用粘贴板
    // // EditText一旦有内容输入，就说明用户不再想看到粘贴的提示气泡，将此值置为false
    // // 所以监听输入框的内容变化，有内容变化就将此值置为false
    // private boolean needShowPaste = true;
    //
    // // 气泡中需要展示的内容
    // private String pasteContent = "";

    private ConfirmClickListener confirmClickListener;

    public LinkAnalysisDialog(Context context, ConfirmClickListener onClickListener) {
        super(context);
        setUp(onClickListener);
    }

    public void setUp(ConfirmClickListener onClickListener) {
        this.confirmClickListener = onClickListener;
        View rootView = onCreateView(LayoutInflater.from(getContext()), null, null);
        setContentView(rootView);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        initPasteBubble();
    }

    public void initPasteBubble() {
        String currentValue = "";
        Editable editable = mLayoutBinding.edit.getText();
        if (!TextUtils.isEmpty(editable)) {
            currentValue = editable.toString();
        }

        // 获取粘贴板里的内容
        try {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence text = clipData.getItemAt(0).getText();
                String clipContent =  text.toString();

                // 如果粘贴板的内容不为空，并且和EditText的内容不同，就展示气泡
                if (!TextUtils.equals(currentValue, clipContent) && !TextUtils.isEmpty(clipContent)) {
                    mLayoutBinding.pasteBubbleBgText.setText(ResourcesUtils.getString(R.string.already_copy_link) +clipContent);
                    mLayoutBinding.pasteBubbleBg.setTag(clipContent);
                    mLayoutBinding.pasteBubbleBg.setVisibility(VISIBLE);
                    mLayoutBinding.pasteBubbleBgText.setVisibility(VISIBLE);
                } else {
                    mLayoutBinding.pasteBubbleBgText.setText("");
                    mLayoutBinding.pasteBubbleBg.setTag("");
                    mLayoutBinding.pasteBubbleBg.setVisibility(GONE);
                    mLayoutBinding.pasteBubbleBgText.setVisibility(GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        mLayoutBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_publish_fill_link,
                container,
                false
        );

        mLayoutBinding.confirmAdd.setOnClickListener(v -> {
            if (confirmClickListener == null) {
                return;
            }
            Editable editable = mLayoutBinding.edit.getText();
            if (TextUtils.isEmpty(editable)) {
                confirmClickListener.onClick("");
            } else {
                confirmClickListener.onClick(editable.toString());
            }
        });

        mLayoutBinding.cancel.setOnClickListener(v -> dismiss());

        mLayoutBinding.clear.setOnClickListener(v -> mLayoutBinding.edit.setText(""));

        mLayoutBinding.pasteBubbleBg.setOnClickListener(v -> {
            Object object = v.getTag();
            if (object == null) {
                mLayoutBinding.edit.setText("");
            } else {
                mLayoutBinding.edit.setText(String.valueOf(object));
            }
            mLayoutBinding.pasteBubbleBg.setVisibility(GONE);
            mLayoutBinding.pasteBubbleBgText.setVisibility(GONE);
        });

        mLayoutBinding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLayoutBinding.clear.setVisibility(TextUtils.isEmpty(s) ? GONE : VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return mLayoutBinding.getRoot();
    }

    public interface ConfirmClickListener {
        void onClick(String result);
    }
}
