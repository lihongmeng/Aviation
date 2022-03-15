package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.LayoutLinkInfoBinding;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

public class PublishLinkView extends ConstraintLayout {

    // Context
    private Context context;

    // 布局文件
    private LayoutLinkInfoBinding binding;

    // 是否需要展示删除按钮
    private boolean isShowDelete;

    // 标题
    public String linkTitle;

    // 地址
    public String linkValue;

    // View的背景图
    private Drawable backgroundDrawable;

    // 因为icon图标是正方形，但是这个自定义View的宽高不固定
    // 所以需要特殊指定该值
    private int iconWidth;
    private int iconHeight;

    // 中间文字的尺寸
    private int contentTextSizeResId;

    // 中间文字的颜色
    private int contentTextColorResId;

    // 中间文字的最大行数
    private int contentTextMaxLine;

    public PublishLinkView(@NonNull Context context) {
        this(context, null);
    }

    public PublishLinkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishLinkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PublishLinkView);
            isShowDelete = array.getBoolean(R.styleable.PublishLinkView_show_delete, false);
            backgroundDrawable = array.getDrawable(R.styleable.PublishLinkView_left_bg_drawable);
            iconWidth = (int) array.getDimension(R.styleable.PublishLinkView_left_icon_width, -1);
            iconHeight = (int) array.getDimension(R.styleable.PublishLinkView_left_icon_height, -1);
            contentTextMaxLine = array.getInt(R.styleable.PublishLinkView_middle_content_max_line, 2);
            array.recycle();
        }
        initViews();
    }

    private void initViews() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_link_info,
                this,
                true
        );

        setOnClickListener(v -> PluginManager.get(WebViewPlugin.class).startWebViewActivity(context, linkValue, linkTitle));
        binding.delete.setVisibility(isShowDelete ? VISIBLE : GONE);
        if (backgroundDrawable != null) {
            binding.root.setBackground(backgroundDrawable);
        }
        binding.title.setMaxLines(contentTextMaxLine > 0 ? contentTextMaxLine : 2);
        updateIconWidth();
        updateIconHeight();
    }

    public void setDeleteClickListener(OnClickListener onClickListener) {
        binding.delete.setOnClickListener(onClickListener);
    }

    public void updateLinkTitle(String linkTitle) {
        this.linkTitle = TextUtils.isEmpty(linkTitle) ? "" : linkTitle;
        binding.title.setText(this.linkTitle);
    }

    public void updateLinkValue(String linkValue) {
        this.linkValue = TextUtils.isEmpty(linkValue) ? "" : linkValue;
    }

    private void updateIconWidth() {
        if (iconWidth <= 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = binding.icon.getLayoutParams();
        layoutParams.width = iconWidth;
        binding.icon.setLayoutParams(layoutParams);
    }

    private void updateIconHeight() {
        if (iconHeight <= 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = binding.icon.getLayoutParams();
        layoutParams.height = iconHeight;
        binding.icon.setLayoutParams(layoutParams);
    }

    public void setContentTextSize(@DimenRes int contentTextSizeResId) {
        this.contentTextSizeResId = contentTextSizeResId;
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(contentTextSizeResId));
    }

    public void setContentTextColor(@ColorRes int contentTextColorResId) {
        this.contentTextColorResId = contentTextColorResId;
        binding.title.setTextColor(getResources().getColor(contentTextColorResId));
    }

    // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
    // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
    // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
    @Override
    public void setVisibility(int visibility) {
        if(TextUtils.isEmpty(linkTitle)){
            super.setVisibility(GONE);
            return;
        }
        super.setVisibility(visibility);
    }

}
