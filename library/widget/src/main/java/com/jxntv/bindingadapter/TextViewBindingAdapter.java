package com.jxntv.bindingadapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * TextView 自定义 Binding Adapter
 *
 *
 * @since 2020-01-19 10:42
 */
public final class TextViewBindingAdapter {
  //<editor-fold desc="ForegroundColorSpan">

  @BindingAdapter(value = {
      // 单个
      "foregroundColorSpanColor",
      "foregroundColorSpanStart",
      "foregroundColorSpanEnd",
      // 多个
      "foregroundColorSpanColors",
      "foregroundColorSpanStarts",
      "foregroundColorSpanEnds"
  }, requireAll = false)
  public static void setTextViewForegroundColorSpan(
      @NonNull TextView textView,
      @Nullable Integer foregroundColorSpanColor,
      @Nullable Integer foregroundColorSpanStart,
      @Nullable Integer foregroundColorSpanEnd,
      @Nullable int[] foregroundColorSpanColors,
      @Nullable int[] foregroundColorSpanStarts,
      @Nullable int[] foregroundColorSpanEnds) {
    @Nullable CharSequence text = textView.getText();
    if (text == null) {
      return;
    }
    int length = text.length();
    // 判断参数个数
    int parametersCount = 0;
    // 判断参数是否错误
    // 单个
    boolean processSingle = foregroundColorSpanColor != null
        && foregroundColorSpanStart != null
        && foregroundColorSpanEnd != null;
    if (processSingle) {
      parametersCount++;
    } else if (foregroundColorSpanColor != null) {
      throw new IllegalArgumentException();
    } else if (foregroundColorSpanStart != null) {
      throw new IllegalArgumentException();
    } else if (foregroundColorSpanEnd != null) {
      throw new IllegalArgumentException();
    }
    // 数组
    boolean processArray = foregroundColorSpanColors != null
        && foregroundColorSpanStarts != null
        && foregroundColorSpanEnds != null
        && foregroundColorSpanColors.length == foregroundColorSpanStarts.length
        && foregroundColorSpanStarts.length == foregroundColorSpanEnds.length;
    if (processArray) {
      parametersCount += foregroundColorSpanColors.length;
    } else if (foregroundColorSpanColors != null) {
      throw new IllegalArgumentException();
    } else if (foregroundColorSpanStarts != null) {
      throw new IllegalArgumentException();
    } else if (foregroundColorSpanEnds != null) {
      throw new IllegalArgumentException();
    }
    if (parametersCount == 0) {
      return;
    }
    // 计算最终的参数
    List<Integer> colorList = new ArrayList<>(parametersCount);
    List<Integer> startList = new ArrayList<>(parametersCount);
    List<Integer> endList = new ArrayList<>(parametersCount);
    // 单个
    if (processSingle) {
      if (foregroundColorSpanStart < 0 || foregroundColorSpanStart > foregroundColorSpanEnd) {
        throw new IllegalArgumentException("invalidate foregroundColorSpanStart");
      }
      if (foregroundColorSpanEnd >= length) {
        throw new IllegalArgumentException("invalidate foregroundColorSpanCount");
      }
      // 添加参数
      colorList.add(foregroundColorSpanColor);
      startList.add(foregroundColorSpanStart);
      endList.add(foregroundColorSpanEnd);
    }
    // 数组
    if (processArray) {
      for (int i = 0; i < foregroundColorSpanColors.length; i++) {
        int start = foregroundColorSpanStarts[i];
        int end = foregroundColorSpanEnds[i];
        if (start < 0 || start > end) {
          throw new IllegalArgumentException("invalidate foregroundColorSpanStarts at index " + i);
        }
        if (end >= length) {
          throw new IllegalArgumentException("invalidate foregroundColorSpanCounts at index " + i);
        }
        // 添加参数
        colorList.add(foregroundColorSpanColors[i]);
        startList.add(start);
        endList.add(end);
      }
    }
    // 设置文本颜色
    SpannableString spannableString;
    if (text instanceof SpannableString) {
      spannableString = (SpannableString) text;
    } else {
      spannableString = new SpannableString(text);
    }
    for (int i = 0; i < colorList.size(); i++) {
      spannableString.setSpan(
          new ForegroundColorSpan(colorList.get(i)),
          startList.get(i),
          endList.get(i),
          Spannable.SPAN_INCLUSIVE_EXCLUSIVE
      );
    }
    textView.setText(spannableString);
  }
  //</editor-fold>

  //<editor-fold desc="ClickableSpan">
  @BindingAdapter(value = {
      // 单个
      "clickableSpanStart",
      "clickableSpanEnd",
      "clickableSpanColor",
      // 多个
      "clickableSpanStarts",
      "clickableSpanEnds",
      "clickableSpanColors",
      // 点击事件
      "clickableSpanListener",
  }, requireAll = false)
  public static void setTextViewClickableSpan(
      @NonNull TextView textView,
      @Nullable Integer clickableSpanStart,
      @Nullable Integer clickableSpanEnd,
      @Nullable Integer clickableSpanColor,
      @Nullable int[] clickableSpanStarts,
      @Nullable int[] clickableSpanEnds,
      @Nullable int[] clickableSpanColors,
      @Nullable OnClickableSpanListener listener) {
    if (listener == null) {
      return;
    }
    CharSequence text = textView.getText();
    if (text == null) {
      return;
    }
    int length = text.length();
    // 参数校验
    int parametersCount = 0;
    // 单个
    boolean processSingle =
        clickableSpanStart != null && clickableSpanEnd != null && clickableSpanColor != null;
    if (processSingle) {
      parametersCount++;
    } else if (clickableSpanStart != null) {
      throw new IllegalArgumentException("invalidate clickableSpanStart");
    } else if (clickableSpanEnd != null) {
      throw new IllegalArgumentException("invalidate clickableSpanEnd");
    } else if (clickableSpanColor != null) {
      throw new IllegalArgumentException("invalidate clickableSpanColor");
    }
    // 数组
    boolean processArray = clickableSpanStarts != null
        && clickableSpanEnds != null
        && clickableSpanColors != null
        && clickableSpanStarts.length == clickableSpanEnds.length
        && clickableSpanEnds.length == clickableSpanColors.length;
    if (processArray) {
      parametersCount++;
    } else if (clickableSpanStarts != null) {
      throw new IllegalArgumentException("invalidate clickableSpanStarts");
    } else if (clickableSpanEnds != null) {
      throw new IllegalArgumentException("invalidate clickableSpanEnds");
    } else if (clickableSpanColors != null) {
      throw new IllegalArgumentException("invalidate clickableSpanColors");
    }
    // 处理参数
    if (parametersCount == 0) {
      return;
    }
    List<Integer> startList = new ArrayList<>(parametersCount);
    List<Integer> endList = new ArrayList<>(parametersCount);
    List<Integer> colorList = new ArrayList<>(parametersCount);
    // 单个
    if (processSingle) {
      if (clickableSpanStart < 0 || clickableSpanStart > clickableSpanEnd) {
        throw new IllegalArgumentException("invalidate clickableSpanStart");
      }
      if (clickableSpanEnd >= length) {
        throw new IllegalArgumentException("invalidate clickableSpanEnd");
      }
      // 添加参数
      startList.add(clickableSpanStart);
      endList.add(clickableSpanEnd);
      colorList.add(clickableSpanColor);
    }
    // 数组
    if (processArray) {
      for (int i = 0; i < clickableSpanStarts.length; i++) {
        int start = clickableSpanStarts[i];
        int end = clickableSpanEnds[i];
        if (start < 0 || start > end) {
          throw new IllegalArgumentException("invalidate clickableSpanStarts");
        }
        if (end >= length) {
          throw new IllegalArgumentException("invalidate clickableSpanEnds");
        }
        // 添加参数
        startList.add(start);
        endList.add(end);
        colorList.add(clickableSpanColors[i]);
      }
    }
    // 设置文本
    SpannableString spannableString;
    if (text instanceof SpannableString) {
      spannableString = (SpannableString) text;
    } else {
      spannableString = new SpannableString(text);
    }
    for (int i = 0; i < startList.size(); i++) {
      int start = startList.get(i);
      int end = endList.get(i);
      // 设置点击
      spannableString.setSpan(
          new CustomClickableSpan(start, end, listener),
          start,
          end,
          Spannable.SPAN_INCLUSIVE_EXCLUSIVE
      );
      // 设置颜色
      spannableString.setSpan(
          new ForegroundColorSpan(colorList.get(i)),
          start,
          end,
          Spannable.SPAN_INCLUSIVE_EXCLUSIVE
      );
    }
    textView.setText(spannableString);
    textView.setMovementMethod(LinkMovementMethod.getInstance());
  }

  /**
   * 自定义 Clickable Span
   */
  private static final class CustomClickableSpan extends ClickableSpan {
    //<editor-fold desc="属性">
    private int mStart;
    private int mEnd;
    @NonNull
    private OnClickableSpanListener mListener;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    CustomClickableSpan(
        int start,
        int end,
        @NonNull OnClickableSpanListener listener) {
      mStart = start;
      mEnd = end;
      mListener = listener;
    }

    //</editor-fold>

    //<editor-fold desc="方法实现">

    @Override
    public void onClick(@NonNull View widget) {
      mListener.onClick(widget, mStart, mEnd);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
      super.updateDrawState(ds);
      ds.setUnderlineText(false);
    }

    //</editor-fold>
  }

  /**
   * Clickable Span 监听器
   */
  public interface OnClickableSpanListener {
    /**
     * 当被点击时回调
     *
     * @param view 被点击的控件
     * @param start 开始位置
     * @param end 结束位置
     */
    void onClick(@NonNull View view, int start, int end);
  }
  //</editor-fold>
}
