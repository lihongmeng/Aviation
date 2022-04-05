package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzlz.aviation.library.widget.R;

/**
 * @author huangwei
 * date : 2021/4/30
 * desc :
 **/
public class FoldTextView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView contentText;
    private TextView textPlus;
    private int showLines;
    private ExpandStatusListener expandStatusListener;
    private boolean isExpand;
    private boolean isCanClickable;
    private float contentSize;
    private int contentColor, tipColor;
    private float contentSpacing;
    private String foldTxt = "收起";
    private String expandTxt = "全文";
    private boolean forcePack = false;


    public TextView getContentText() {
        return contentText;
    }

    public FoldTextView(Context context) {
        super(context);
        initView();
    }

    public FoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public FoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FoldTextView, 0, 0);
        try {
            showLines = typedArray.getInt(R.styleable.FoldTextView_showMaxLine, DEFAULT_MAX_LINES);
            isCanClickable = typedArray.getBoolean(R.styleable.FoldTextView_tipClickable, true);
            forcePack = typedArray.getBoolean(R.styleable.FoldTextView_force_pack, false);
            contentSize = typedArray.getDimension(R.styleable.FoldTextView_contentTextSize, 20);
            contentColor = typedArray.getColor(R.styleable.FoldTextView_contentTextColor, getResources().getColor(R.color.color_212229));
            contentSpacing = typedArray.getFloat(R.styleable.FoldTextView_contentSpacing, 1.2f);
            tipColor = typedArray.getColor(R.styleable.FoldTextView_tipColor, getResources().getColor(R.color.color_212229));
        } finally {
            typedArray.recycle();
        }
    }

    private void initView() {

        setOrientation(VERTICAL);
        contentText = new AviationTextView(getContext());

        contentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);
        contentText.setTextColor(contentColor);
        contentText.setLineSpacing(0, contentSpacing);
        contentText.setMaxLines(showLines);
        addView(contentText);

        textPlus = new AviationTextView(getContext());
        textPlus.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);
        textPlus.setTextColor(tipColor);
        contentText.setLineSpacing(0, contentSpacing);
        addView(textPlus);

        if (showLines == 0) {
            contentText.setMaxLines(showLines);
        }

        if (isCanClickable) {
            textPlus.setOnClickListener(view -> {
                String textStr = textPlus.getText().toString().trim();
                if (expandTxt.equals(textStr)) {
                    if(!forcePack){
                        contentText.setMaxLines(Integer.MAX_VALUE);
                    }
                    textPlus.setText(foldTxt);
                    setExpand(true);
                } else {
                    if(!forcePack){
                        contentText.setMaxLines(showLines);
                    }
                    textPlus.setText(expandTxt);
                    setExpand(false);
                }
                if (expandStatusListener != null) {
                    expandStatusListener.statusChange(isExpand());
                }
            });
        }
    }

    public void setContentSpacing(float contentSpacing) {
        this.contentSpacing = contentSpacing;
        contentText.setLineSpacing(0, contentSpacing);
    }

    public void setText(final CharSequence content) {

//        contentText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                String a = contentText.getText().toString();
//                contentText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int linCount = contentText.getLineCount();
//                if (linCount > showLines){
//                    if (isExpand) {
//                        contentText.setMaxLines(Integer.MAX_VALUE);
//                        textPlus.setText(foldTxt);
//                    } else {
//                        contentText.setMaxLines(showLines);
//                        textPlus.setText(expandTxt);
//                    }
//                    textPlus.setVisibility(View.VISIBLE);
//                }else{
//                    textPlus.setVisibility(View.GONE);
//                }
//            }
//        });

//        contentText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                String a = contentText.getText().toString();
//                contentText.getViewTreeObserver().removeOnPreDrawListener(this);
//                int linCount = contentText.getLineCount();
//                if (linCount > showLines){
//                    if (isExpand) {
//                        contentText.setMaxLines(Integer.MAX_VALUE);
//                        textPlus.setText(foldTxt);
//                    } else {
//                        contentText.setMaxLines(showLines);
//                        textPlus.setText(expandTxt);
//                    }
//                    textPlus.setVisibility(View.VISIBLE);
//                }else{
//                    textPlus.setVisibility(View.GONE);
//                }
//                return true;
//            }
//        });

        contentText.post(() -> {

            int linCount = contentText.getLineCount();
            if (linCount > showLines){
                if (isExpand) {
                    if(!forcePack){
                        contentText.setMaxLines(Integer.MAX_VALUE);
                    }
                    textPlus.setText(foldTxt);
                } else {
                    if(!forcePack){
                        contentText.setMaxLines(showLines);
                    }
                    textPlus.setText(expandTxt);
                }
                textPlus.setVisibility(View.VISIBLE);
            } else {
                textPlus.setVisibility(View.GONE);
            }
        });

        contentText.setText(content);
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener) {
        this.expandStatusListener = listener;
    }

    public interface ExpandStatusListener {
        void statusChange(boolean isExpand);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return textPlus.dispatchTouchEvent(event);
    }

    public void setContentTextSize(float contentSize){
        this.contentSize=contentSize;
        contentText.setTextSize(TypedValue.COMPLEX_UNIT_PX,contentSize);
        textPlus.setTextSize(TypedValue.COMPLEX_UNIT_PX,contentSize);
    }
}
