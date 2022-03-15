package com.hzlz.aviation.feature.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.search.databinding.SearchOneImgBinding;
import com.hzlz.aviation.feature.search.databinding.SearchResultDataBinding;
import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.template.SearchBaseTemplate;
import com.hzlz.aviation.feature.search.BR;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索单图模板类型
 */
public class SearchOneImgTemplate extends SearchBaseTemplate {

    /** 搜索单图binding */
    private SearchOneImgBinding mBinding;
    /** 搜索结果data binding */
    private SearchResultDataBinding mDataBinding;
    /** 是否显示source */
    private boolean needShowSource;

    /**
     * 构造函数
     */
    public SearchOneImgTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_one_img,
            parent, false);
//        needShowSource = !TextUtils.equals(tag, SearchApiConstants.SEARCH_TYPE_MOVIES);
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
        mBinding.imgTag.setVisibility(needShowSource ? View.VISIBLE : View.GONE);
        mBinding.oneImgSource.setVisibility(needShowSource ? View.VISIBLE : View.GONE);
        super.update(searchModel);

    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.oneImgTitle;
    }

    @Override
    protected TextView getTagTextView() {
        return mBinding.imgTag;
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    @Override
    public void mute(boolean value) {

    }
}
