package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import com.hzlz.aviation.library.widget.R;

/**
 * 从底部弹出的弹窗
 *
 * @since 2020-02-04 16:23
 */
public class GVideoLimitWHBottomSheetDialog extends GVideoDialog {

    protected int mExpectWindowWidth = 0;

    protected int mExpectWindowHeight = 0;

    public GVideoLimitWHBottomSheetDialog(Context context) {
        super(context);
        init();
    }

    public GVideoLimitWHBottomSheetDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
            window.getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
        }
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            if (mExpectWindowWidth == 0) {
                mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (mExpectWindowHeight == 0) {
                mExpectWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            window.setLayout(mExpectWindowWidth, mExpectWindowHeight);
        }
    }

}
