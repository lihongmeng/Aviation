package com.jxntv.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.R;
import com.jxntv.base.databinding.LayoutHomeMessageBinding;
import com.jxntv.base.observable.TopMessageObservable;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.event.entity.DrawerLayoutData;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoRTextView;

/**
 * 主页消息、设置布局
 */
public class HomeMessageLayout extends ConstraintLayout {

    private boolean isShowWhiteIcon = false;
    private String pageName;

    private LayoutHomeMessageBinding binding;

    public HomeMessageLayout(Context context) {
        this(context, null);
    }

    public HomeMessageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HomeMessageLayout);
            isShowWhiteIcon = array.getBoolean(R.styleable.HomeMessageLayout_is_white_icon, false);
        }
        init(context);
    }

    public String getPageName() {
        return TextUtils.isEmpty(pageName) ? "" : pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    private void init(Context context) {
        binding= DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_home_message,
                this,
                true
        );


       // setPadding(dp10, 0, dp10, 0);
       // message = new GVideoImageView(context);
       // ViewGroup.LayoutParams messageParams = new ViewGroup.LayoutParams(dp27, dp27);
       // message.setLayoutParams(messageParams);
       // addView(message);
       //
       // setting = new GVideoImageView(context);
       // LinearLayout.LayoutParams settingParams = new LinearLayout.LayoutParams(dp27, dp27);
       // settingParams.setMargins(dp37, 0, 0, 0);
       // setting.setLayoutParams(settingParams);
       // addView(setting);
       //
       // LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // View view = inflaters.inflate(R.layout.layout_home_message, null);
       // unRead = view.findViewById(R.id.unread);
       // int dp17 = context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_17DP);
       // ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp12);
       // layoutParams.setMargins(dp17, 0, 0, 0);
       // unRead.setLayoutParams(layoutParams);
       // unRead.setVisibility(GONE);
       // addView(unRead);
       // TypefaceUtils.setNumberTypeface(unRead);

        setIcon(isShowWhiteIcon);
        setClickListener();

    }

    public void updateDataObservable(TopMessageObservable topMessageObservable){
        if(binding==null||topMessageObservable==null){
            return;
        }
        binding.setDataObservable(topMessageObservable);
    }

    public void setIcon(boolean isWhiteIcon) {
        if (!isWhiteIcon) {
            binding.message.setImageResource(R.drawable.feed_icon_message_black);
            binding.setting.setImageResource(R.drawable.feed_icon_setting_black);
        } else {
            binding.message.setImageResource(R.drawable.feed_icon_message_white);
            binding.setting.setImageResource(R.drawable.feed_icon_setting_white);
        }
    }

    /**
     * 设置未读消息数量
     *
     * @param unreadCount
     */
    public void setUnreadCount(int unreadCount) {
        if (unreadCount > 0) {
            binding.layoutCount.setVisibility(VISIBLE);
            binding.unread.setVisibility(VISIBLE);
            binding.unread.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
        } else {
            binding.layoutCount.setVisibility(GONE);
        }
    }

    private void setClickListener() {
        binding.message.setOnClickListener(
                view -> GVideoEventBus.get(HomePlugin.EVENT_HOME_MESSAGE).post(getPageName())
        );
        binding.setting.setOnClickListener(
                view -> GVideoEventBus.get(HomePlugin.EVENT_HOME_DRAWER).post(
                        new DrawerLayoutData(true,getPageName())
                )
        );
    }

    public void updateToNormal(){
        binding.message.setImageResource(R.drawable.feed_icon_message_black);
        binding.setting.setImageResource(R.drawable.feed_icon_setting_black);
        binding.unread.setTextColor(ContextCompat.getColor(getContext(),R.color.color_ffffff));
        binding.layoutCount.getHelper().setBackgroundColorNormal(ContextCompat.getColor(getContext(),R.color.color_e4344e));
    }

    public void updateToRed(){
        binding.message.setImageResource(R.drawable.feed_icon_message_white);
        binding.setting.setImageResource(R.drawable.feed_icon_setting_white);
        binding.unread.setTextColor(ContextCompat.getColor(getContext(),R.color.color_e4344e));
        binding.layoutCount.getHelper().setBackgroundColorNormal(ContextCompat.getColor(getContext(),R.color.color_ffe4a8));
    }

    public void updateToSpringFestival(){
        binding.message.setImageResource(R.drawable.feed_icon_message_white);
        binding.setting.setImageResource(R.drawable.feed_icon_setting_white);
        binding.unread.setTextColor(ContextCompat.getColor(getContext(),R.color.color_e4344e));
        binding.layoutCount.getHelper().setBackgroundColorNormal(ContextCompat.getColor(getContext(),R.color.color_ffe4a8));
    }
}
