package com.hzlz.aviation.feature.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.search.SearchRuntime;
import com.hzlz.aviation.feature.search.databinding.SearchResultDataBinding;
import com.hzlz.aviation.feature.search.databinding.SearchShortProductionBinding;
import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.template.SearchBaseTemplate;
import com.hzlz.aviation.feature.search.BR;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索短剧模板
 */
public class SearchShortProductionTemplate  extends SearchBaseTemplate  {

    /** 搜索短剧集binding */
    private SearchShortProductionBinding mBinding;
    /** 搜索结果 data binding */
    private SearchResultDataBinding mDataBinding;

    /**
     * 构造函数
     */
    public SearchShortProductionTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_short_production,
                parent, false);
    }

    @Override
    public void update(ISearchModel searchModel) {
        if (!(searchModel instanceof SearchDetailModel)) {
            return;
        }
        SearchDetailModel detailModel = (SearchDetailModel) searchModel;

        if (mDataBinding == null) {
            mDataBinding = new SearchResultDataBinding();
        }
        mBinding.setVariable(BR.detailBind, mDataBinding);
        mBinding.setVariable(BR.detailModel, detailModel);
        mBinding.executePendingBindings();


        super.update(searchModel);
    }

    @Override
    protected TextView getTitleTextView() {
        return null;
    }

    @Override
    protected TextView getTagTextView() {
        return mBinding.shortProductionTag;
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    protected ViewGroup getRootLayout() {
        return null;
    }

    @Override
    protected int getTitleColor() {
        return SearchRuntime.getAppContext().getResources().getColor(R.color.color_ffffff);
    }

    @Override
    public void mute(boolean value) {

    }
}
