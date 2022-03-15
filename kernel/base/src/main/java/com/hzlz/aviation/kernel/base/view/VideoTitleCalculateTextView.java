package com.hzlz.aviation.kernel.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Display;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

public class VideoTitleCalculateTextView extends GVideoTextView {

    private Context context;
    private int laseSpaceCount;
    public int subLine;

    public boolean needShowSpreadButton;

    public VideoTitleCalculateTextView(Context context) {
        this(context, null);
    }

    public VideoTitleCalculateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTitleCalculateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VideoTitleCalculateTextView);
            laseSpaceCount = array.getInt(R.styleable.VideoTitleCalculateTextView_last_space_count, 0);
            subLine = array.getInt(R.styleable.VideoTitleCalculateTextView_sub_line, 1);
            array.recycle();
        }
        if (subLine > 0) {
            setMaxLines(subLine);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        boolean isPortrait = display.getWidth() > display.getHeight();
        if(isPortrait){
            return;
        }

        needShowSpreadButton = false;

        if (subLine > 0 && getLineCount() > subLine) {

            CharSequence text = getText();
            int index = getCharNum();
            if(index<=0){
                return;
            }
            while (laseSpaceCount > 0) {
                char value = text.charAt(index - 1);
                if (value >= 0X4e00 && value <= 0X9fbb) {
                    laseSpaceCount -= 2;
                } else {
                    laseSpaceCount--;
                }
                if (index<=1){
                    break;
                }
                index--;
            }
            needShowSpreadButton = true;
            setText(text.subSequence(0, index) + "...");
        }
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        if (layout == null) {
            return super.getLineCount();
        }
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine) + 1;
    }

}
