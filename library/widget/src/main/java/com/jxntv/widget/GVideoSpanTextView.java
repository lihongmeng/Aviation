package com.jxntv.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author huangwei
 * date : 2021/2/23
 * desc :
 **/
public class GVideoSpanTextView extends GVideoTextView{

    private HashMap<String,Integer> contentColor;
    private HashMap<String,ClickableSpan> contentClick;
    private List<String> contentList;
    private StringBuilder contentAll;

    public GVideoSpanTextView(Context context) {
        super(context);
    }

    public GVideoSpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        contentColor = new HashMap<>();
        contentClick = new HashMap<>();
        contentList = new ArrayList<>();
        contentAll = new StringBuilder();
    }

    public GVideoSpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void clear(){
        contentColor.clear();
        contentClick.clear();
        contentList.clear();
        contentAll.delete(0,contentAll.length());
    }

    public GVideoSpanTextView addText(String text, @ColorRes int textColor,ClickableSpan clickableSpan){
        if (!TextUtils.isEmpty(text)) {
            contentAll.append(text);
            contentColor.put(text, textColor);
            contentList.add(text);
            if (clickableSpan != null) {
                contentClick.put(text, clickableSpan);
            }
        }
        return this;
    }

    public void showText(){
        StringBuilder currentText = new StringBuilder();
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(contentAll);
        for (int i=0;i<contentList.size();i++){
            String str = contentList.get(i);
            int color = contentColor.get(str);
            int length = currentText.length() + str.length();

            if (contentClick.get(str)!=null){
                spannableBuilder.setSpan(contentClick.get(str), currentText.length(), length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //设置下划线
                UnderlineSpan underlineSpan = new UnderlineSpan();
                spannableBuilder.setSpan(underlineSpan, currentText.length(), length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //单独设置字体颜色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(
                    "#"+Integer.toHexString(ContextCompat.getColor(getContext(), color))));
            spannableBuilder.setSpan(colorSpan, currentText.length(), length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            currentText.append(str);
        }

        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spannableBuilder);
        //去掉点击后文字的背景色
//        setHighlightColor(Color.parseColor("#00000000"));

    }
}
