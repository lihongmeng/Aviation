package com.jxntv.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.jxntv.search.BR;
import com.jxntv.search.R;
import com.jxntv.search.databinding.SearchOneImgRadioBinding;
import com.jxntv.search.databinding.SearchResultDataBinding;
import com.jxntv.search.model.ISearchModel;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.template.SearchBaseTemplate;

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
        mBinding.setVariable(com.jxntv.search.BR.detailBind, mDataBinding);
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
