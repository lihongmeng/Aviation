package com.hzlz.aviation.feature.live.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.callback.OnCommentAddListener;
import com.hzlz.aviation.feature.live.databinding.LiveCommentInputWhiteLayoutBinding;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.base.utils.StringUtils;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.live.R;

/**
 * 评论弹窗
 */
public class CommentInputWhiteDialog extends GVideoBottomSheetDialog {

    private LiveCommentInputWhiteLayoutBinding mLayoutBinding;

    private OnCommentAddListener mCommentAddListener;

    public CommentInputWhiteDialog(Context context) {
        super(context);
    }

    public void setUp(OnCommentAddListener listener) {
        mCommentAddListener = listener;
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
                LayoutInflater.from(getContext()),
                R.layout.live_comment_input_white_layout,
                container,
                false
        );
        View mInputSendButton = mLayoutBinding.send;
        EditText mInputEdit = mLayoutBinding.input;
        final SpannableString prefix;
        prefix = null;

        mInputEdit.addTextChangedListener(new TextWatcher() {
            int maxLen = 150;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mInputEdit.getText();
                int len = editable.length();

                if (len > maxLen) {
                    Toast.makeText(mInputEdit.getContext(), R.string.live_comment_input_max_toast,
                            Toast.LENGTH_SHORT).show();

                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    // 截取新字符串
                    String newStr = str.substring(0, maxLen);
                    mInputEdit.setText(newStr);
                    editable = mInputEdit.getText();

                    // 新字符串的长度
                    int newLen = editable.length();
                    // 旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    // 设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().trim().length();
                int prefixLength = prefix != null ? prefix.length() : 0;
                if (length > prefixLength) {
                    mInputSendButton.setSelected(true);
                } else {
                    mInputSendButton.setSelected(false);
                }

                if (prefix != null) {
                    if (!s.toString().startsWith(prefix.toString())) {
                        if (length > prefixLength) {
                            s.replace(0, prefixLength, prefix);
                        } else {
                            mInputEdit.setText(prefix);
                            mInputEdit.setSelection(prefix.length());
                        }
                    }
                }
            }
        });

        mInputSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkTipUtils.checkNetworkOrTip(getContext())) {
                    return;
                }
                String text = mInputEdit.getText().toString().trim();
                if (prefix != null) {
                    if (text.startsWith(prefix.toString())) {
                        int start = prefix.length();
                        text = text.substring(start);
                    }
                }
                text = StringUtils.filterWhiteSpace(text);
                if (!TextUtils.isEmpty(text)) {
                    if (mCommentAddListener != null) {
                        mCommentAddListener.onCommentAdd(text);
                    }

                    SoftInputUtils.hideSoftInput(mInputEdit.getWindowToken(), getContext());
                    dismiss();
                }
            }
        });
        return mLayoutBinding.getRoot();
    }

    @Override
    public void dismiss() {
        //隐藏软件盘
        SoftInputUtils.hideSoftInput(getCurrentFocus().getWindowToken(), getContext());
        super.dismiss();
    }

    @Override
    public void show() {
        if (!LiveManager.getInstance().checkOrLogin()) {
            return;
        }

        super.show();
        SoftInputUtils.showSoftInput(mLayoutBinding.input, getContext(), 0);
    }



}
