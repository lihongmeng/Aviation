package com.jxntv.android.video.ui.detail.comment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.jxntv.android.video.Me;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.VideoShortCommentInputLayoutBinding;
import com.jxntv.base.Constant;
import com.jxntv.base.model.comment.ReplyModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.utils.StringUtils;
import com.jxntv.dialog.GVideoBottomSheetDialog;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.SoftInputUtils;

/**
 * 纯文字评论输入弹窗
 */
public class CommentInputTxtDialog extends GVideoBottomSheetDialog {
  private VideoShortCommentInputLayoutBinding mLayoutBinding;
  private AuthorModel mReplyAuthor;
  private OnCommentAddListener mCommentAddListener;
  private String pid;
  public interface OnCommentAddListener {
    void onCommentAdd(ReplyModel comment);
  }

  public CommentInputTxtDialog(Context context,String pid) {
    super(context);
    this.pid=pid;
  }

  public void setUp(AuthorModel authorModel, OnCommentAddListener listener) {
    mReplyAuthor = authorModel;
    mCommentAddListener = listener;

    View rootView = onCreateView(LayoutInflater.from(getContext()), null, null);
    setContentView(rootView);
    mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
  }

  private View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
        R.layout.video_short_comment_input_layout, container,false);
    View mInputSendButton = mLayoutBinding.send;
    EditText mInputEdit = mLayoutBinding.input;
    final SpannableString prefix;
    if (mReplyAuthor != null) {
      String name = mReplyAuthor.getName();
      String hint = getContext().getResources().getString(R.string.video_reply_to_prefix, name);
      prefix = new SpannableString(hint);
      int color = getContext().getResources().getColor(R.color.color_a1a4b3);
      prefix.setSpan(new ForegroundColorSpan(color), 0, hint.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
      mInputEdit.setText(prefix);
      mInputEdit.setSelection(prefix.length());
    } else {
      prefix = null;
    }
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
          Toast.makeText(mInputEdit.getContext(), R.string.video_comment_input_max_toast,
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
      @Override public void onClick(View v) {
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
          ReplyModel.Builder builder = ReplyModel.Builder.aReplyModel()
              .withReplyContent(text);
          ReplyModel model = builder.build();
          if (mCommentAddListener != null) {
            mCommentAddListener.onCommentAdd(model);
          }

          SoftInputUtils.hideSoftInput(mInputEdit.getWindowToken(), getContext());
          dismiss();
        }
      }
    });

    return mLayoutBinding.getRoot();
  }

  @Override public void dismiss() {
    SoftInputUtils.hideSoftInput(getCurrentFocus().getWindowToken(), getContext());
    super.dismiss();
  }

  @Override public void show() {
    if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
      GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
      GVideoSensorDataManager.getInstance().enterRegister(
              StatPid.getPageName(pid),
              ResourcesUtils.getString(R.string.comment)
      );
      return;
    }
    super.show();
    SoftInputUtils.showSoftInput(mLayoutBinding.input, getContext(), 0);
  }

}
