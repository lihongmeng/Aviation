package com.hzlz.aviation.feature.community.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.community.adapter.ViewPagerLeftTabAdapter;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ViewPagerLeftTabBinding;
import com.hzlz.aviation.kernel.base.model.circle.CircleTag;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerLeftTab extends AviationLinearLayout {

    /**
     * 数据源
     */
    private final List<CircleTag> dataSource = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;
    private ViewPagerLeftTabBinding binding;
    private ViewPagerLeftTabAdapter adapter;

    public ViewPagerLeftTab(Context context) {
        this(context, null);
    }

    public ViewPagerLeftTab(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerLeftTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
    }

    private void initVars(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.view_pager_left_tab,
                this,
                true
        );
        adapter = new ViewPagerLeftTabAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.list.setLayoutManager(linearLayoutManager);
        binding.list.setAdapter(adapter);
    }

    public void updateDataSource(List<CircleTag> dataList) {
        dataSource.clear();
        if (dataList != null) {
            dataSource.addAll(dataList);
        }
        adapter.refreshData(dataSource, 0);
    }

    public void updateIndex(int index){
        adapter.updateIndex(index);
    }

    @BindingAdapter("updateListResource")
    public static void updateListResource(ViewPagerLeftTab viewPagerLeftTab, List<CircleTag> imageData) {
        if (viewPagerLeftTab != null) {
            viewPagerLeftTab.updateDataSource(imageData);
        }
    }
}
