package com.hzlz.aviation.kernel.base.view;

import static com.hzlz.aviation.kernel.base.view.ViewPagerTab.ContentGravity.CENTER;
import static com.hzlz.aviation.kernel.base.view.ViewPagerTab.ContentGravity.CENTER_VERTICAL_LEFT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ViewPagerCenterTabCenterBinding;
import com.hzlz.aviation.kernel.base.databinding.ViewPagerCenterTabLeftBinding;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoRelativeLayout;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerTab extends GVideoRelativeLayout {

    interface ContentGravity {
        int CENTER = 0;
        int CENTER_VERTICAL_LEFT = 1;
    }

    /**
     * 当前显示的索引
     */
    private int currentIndex = 0;

    /**
     * 最多显示多少数量
     * 在构造方法中根据屏幕尺寸计算
     */
    private int maxShowCount = 4;


    /**
     * 每个View固定的宽度
     * 单位 DP
     */
    private int viewWidthDP = 90;

    /**
     * 选中字体大小
     */
    private float selectSize = 19;

    /**
     * 非选中字体大小
     */
    private float unSelectSize = 15;

    /**
     * 数据源
     */
    private final List<String> dataSource = new ArrayList<>();

    private int firstTabDrawablePadding;

    private Context context;
    private LayoutInflater layoutInflater;

    private int gravity;

    private OnTabClickListener onClickListener;

    private AviationLinearLayout viewLayout;

    private boolean mayShowRightArrow = false;

    private int selectedTextColor;
    private int unSelectedTextColor;
    private Drawable selectedIndicatorDrawable;

    public ViewPagerTab(Context context) {
        this(context, null);
    }

    public ViewPagerTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerTab);
        gravity = typedArray.getInteger(R.styleable.ViewPagerTab_content_gravity, 0);
        mayShowRightArrow = typedArray.getBoolean(R.styleable.ViewPagerTab_may_show_right_arrow, false);
        typedArray.recycle();
        initVars(context);
    }

    private void initVars(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        maxShowCount = calculateMaxShowCount();

        selectedTextColor = ContextCompat.getColor(getContext(), R.color.color_333333);
        unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.color_7f7f7f);
        selectedIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shape_soild_e4344e_corners_4dp);

        switch (gravity) {
            case CENTER:
                ViewPagerCenterTabCenterBinding centerBinding = DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.view_pager_center_tab_center,
                        this,
                        true
                );
                viewLayout = centerBinding.viewLayout;
                break;
            case CENTER_VERTICAL_LEFT:
                ViewPagerCenterTabLeftBinding leftBinding = DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.view_pager_center_tab_left,
                        this,
                        true
                );
                viewLayout = leftBinding.viewLayout;
                break;
        }

    }

    private int calculateMaxShowCount() {
        if (context == null) {
            return 0;
        }
        int appScreenWidth = ScreenUtils.getAppScreenWidth(context);
        return Math.max(appScreenWidth / SizeUtils.dp2px(viewWidthDP), 0);
    }

    public void setSelectSize(float selectSize) {
        this.selectSize = selectSize;
    }

    public void setUnSelectSize(float unSelectSize) {
        this.unSelectSize = unSelectSize;
    }

    @SuppressLint("RtlHardcoded")
    public void updateDataSourceWithStringList(List<String> dataList) {
        dataSource.clear();
        viewLayout.removeAllViews();

        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataSource.addAll(dataList);
        currentIndex = 0;

        // 如果添加的数据源size，超过屏幕可显示的最大数量
        // binding.viewLayout需要改变Gravity
        // 扩展成横向滑动，横向滑动后期有需求再加
        // int length = dataSource.size();
        // if (length <= maxShowCount) {
        //     binding.viewLayout.setGravity(CENTER);
        // } else {
        //     binding.viewLayout.setGravity(Gravity.LEFT);
        // }

        for (int index = 0; index < dataSource.size(); index++) {
            GVideoRelativeLayout itemView = (GVideoRelativeLayout) layoutInflater.inflate(
                    R.layout.view_pager_center_tab_item,
                    null,
                    false
            );
            updateContentGvtv(itemView, index);

            itemView.setTag(index);
            itemView.setOnClickListener(v -> {
                Object tag = v.getTag();
                if (tag == null || onClickListener == null) {
                    return;
                }
                onClickListener.onTabClick((Integer) tag);
            });

            viewLayout.addView(
                    itemView,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
        }
    }

    public void updateDataSourceWithTabList(List<TabItemInfo> tabItemInfoList) {
        List<String> result = new ArrayList<>();
        if (tabItemInfoList == null || tabItemInfoList.isEmpty()) {
            updateDataSourceWithStringList(result);
            return;
        }
        for (TabItemInfo tabItemInfo : tabItemInfoList) {
            if (tabItemInfo == null || tabItemInfo.tabName == null) {
                continue;
            }
            result.add(tabItemInfo.tabName);
        }
        updateDataSourceWithStringList(result);
    }


    // 当前数据源不变，使用当前数据源刷新View
    public void updateDataSourceWithStringList() {
        for (int index = 0; index < viewLayout.getChildCount(); index++) {
            updateContentGvtv(viewLayout.getChildAt(index), index);
        }
    }

    private void updateContentGvtv(View parentView, int index) {
        AviationTextView contentGvtv = parentView.findViewById(R.id.gvtv_view_pager_tab_layout_item);
        View markView = parentView.findViewById(R.id.v_view_pager_tab_layout_item);
        contentGvtv.setText(dataSource.get(index));
        markView.setBackground(selectedIndicatorDrawable);

        if (index == currentIndex) {
            contentGvtv.setTextSize(selectSize);
            contentGvtv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            contentGvtv.setTextColor(selectedTextColor);
            markView.setVisibility(VISIBLE);
        } else {
            contentGvtv.setTextSize(unSelectSize);
            contentGvtv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            contentGvtv.setTextColor(unSelectedTextColor);
            markView.setVisibility(GONE);
        }

        if (index == 0 && mayShowRightArrow) {
            if (currentIndex != 0) {
                contentGvtv.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                );
            } else {
                contentGvtv.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(getContext(), R.drawable.icon_arrow_down),
                        null
                );
                contentGvtv.setCompoundDrawablePadding(firstTabDrawablePadding);
            }
        }
    }

    public void updateLeftItemDrawable(
            int index,
            Drawable left,
            Drawable top,
            Drawable right,
            Drawable bottom,
            int drawablePadding
    ) {
        AviationLinearLayout linearLayout = findViewById(R.id.view_layout);
        if (index >= linearLayout.getChildCount()) {
            return;
        }
        AviationTextView textView = (linearLayout.getChildAt(index)).findViewById(R.id.gvtv_view_pager_tab_layout_item);
        textView.setCompoundDrawablesWithIntrinsicBounds(
                left,
                top,
                right,
                bottom
        );
        firstTabDrawablePadding = drawablePadding;
        textView.setCompoundDrawablePadding(drawablePadding);
    }

    public void updateLeftItemText(
            int index,
            String text
    ) {
        AviationLinearLayout linearLayout = findViewById(R.id.view_layout);
        if (index >= linearLayout.getChildCount()) {
            return;
        }
        dataSource.set(index, text);
        updateDataSourceWithStringList();
    }

    public void updateIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        updateDataSourceWithStringList();
    }

    public void setOnTabClickListener(OnTabClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    public void updateToNormal() {
        setBackground(ContextCompat.getDrawable(context,R.color.color_ffffff));
        selectedTextColor = ContextCompat.getColor(getContext(), R.color.color_333333);
        unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.color_7f7f7f);
        selectedIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shape_soild_e4344e_corners_4dp);
        updateDataSourceWithStringList();
    }

    public void updateToRed() {
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_tab_chiness_red_live_community));
        selectedTextColor = ContextCompat.getColor(getContext(), R.color.color_ffe4a8);
        unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.color_ffffff);
        selectedIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shape_soild_ffe4a8_corners_4dp);
        updateDataSourceWithStringList();
    }

    public void updateToSpringFestival() {
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_tab_spring_festival_live_community));
        selectedTextColor = ContextCompat.getColor(getContext(), R.color.color_ffe4a8);
        unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.color_ffffff);
        selectedIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shape_soild_ffe4a8_corners_4dp);
        updateDataSourceWithStringList();
    }
}
