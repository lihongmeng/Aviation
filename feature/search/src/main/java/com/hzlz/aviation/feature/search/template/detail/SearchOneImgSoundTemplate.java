package com.hzlz.aviation.feature.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.search.databinding.SearchOneImgRadioBinding;
import com.hzlz.aviation.feature.search.databinding.SearchResultDataBinding;
import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.template.SearchBaseTemplate;
import com.hzlz.aviation.feature.search.BR;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索单图音频模板类型
 */
public class SearchOneImgSoundTemplate  extends SearchBaseTemplate {

    /** 搜索单图音频binding */
    private SearchOneImgRadioBinding mBinding;
    /** 搜索data binding */
    private SearchResultDataBinding mDataBinding;

    /**
     * 构造函数
     */
    public SearchOneImgSoundTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_one_img_radio,
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
        mBinding.setVariable(com.hzlz.aviation.feature.search.BR.detailBind, mDataBinding);
        mBinding.setVariable(BR.detailModel, detailModel);
        mBinding.executePendingBindings();

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
        return mBinding.normalRecommendLayout;
    }

    @Override
    public void mute(boolean value) {

    }
}
