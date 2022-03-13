package com.jxntv.pptv.ui.channel;

import android.content.Context;
import android.util.AttributeSet;
import com.jxntv.widget.CirclePageIndicator;

public final class FakedCirclePageIndicator extends CirclePageIndicator {

  public FakedCirclePageIndicator(Context context) {
    super(context);
  }

  public FakedCirclePageIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public FakedCirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected boolean isHidePosition(int position, int total) {
    if (total > 1) {
      return position == 0 || position == total - 1;
    }
    return false;
  }
}
