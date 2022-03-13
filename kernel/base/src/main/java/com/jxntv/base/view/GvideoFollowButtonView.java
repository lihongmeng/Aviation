package com.jxntv.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import com.jxntv.base.R;
import com.jxntv.widget.GVideoRLinearLayout;
import com.jxntv.widget.GVideoTextView;

/**
 * @author huangwei
 * date : 2021/10/8
 * desc : 关注按钮
 **/
public class GvideoFollowButtonView extends GVideoRLinearLayout {

    private String followText,unFollowText;
    private GVideoTextView content;

    public GvideoFollowButtonView(Context context) {
        this(context,null);
    }

    public GvideoFollowButtonView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GvideoFollowButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FollowButtonView);
            followText = array.getString(R.styleable.FollowButtonView_followText);
            unFollowText = array.getString(R.styleable.FollowButtonView_unfollowText);
            array.recycle();
        }
        init();
    }

    public void setText(@StringRes int followText,@StringRes int unFollowText){
        setText(getResources().getString(followText),getResources().getString(unFollowText));
    }

    public void setText(String followText,String unFollowText){
        this.followText = followText;
        this.unFollowText = unFollowText;
    }

    private void init(){
        setGravity(Gravity.CENTER);
        int borderWidth = getResources().getDimensionPixelOffset(R.dimen.DIMEN_D5P);
        int radius = getResources().getDimensionPixelOffset(R.dimen.DIMEN_13DP);
        getHelper().setCornerRadius(radius);
        getHelper().setBorderWidthNormal(borderWidth);

        content = new GVideoTextView(getContext());
        addView(content);

        content.setTextSize(12);

    }

    public void setFollowButton(boolean isFollow){
        int followColor = ContextCompat.getColor(getContext(), R.color.color_e4344e);
        int normalColor = ContextCompat.getColor(getContext(),R.color.color_7f7f7f);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_add_red);
        int dp10 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_10DP);
        drawable.setBounds(0,0,dp10,dp10);
        if (isFollow){
            getHelper().setBorderColorNormal(normalColor);
            content.setTextColor(normalColor);
            content.setText(followText);
            content.setCompoundDrawables(null,null,null,null);
        }else {
            getHelper().setBorderColorNormal(followColor);
            content.setTextColor(followColor);
            content.setText(unFollowText);
            content.setCompoundDrawables(drawable,null,null,null);
        }
    }
}
