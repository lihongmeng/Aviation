package com.jxntv.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.jxntv.search.BR;
import com.jxntv.search.R;
import com.jxntv.search.databinding.SearchAuthorBinding;
import com.jxntv.search.databinding.SearchResultDataBinding;
import com.jxntv.search.model.ISearchModel;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.template.SearchBaseTemplate;

/**
 * 搜索关注类模板
 */
public class SearchAuthorTemplate extends SearchBaseTemplate {

    /** 搜索关注类模板binding */
    private SearchAuthorBinding mBinding;
    /** 搜索关注类模板通用处理data binding */
    private SearchResultDataBinding mDataBinding;

    /**
     * 构造函数
     *
     * @param context   上下文环境
     * @param parent    父容器
     */
    public SearchAuthorTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_author,
                parent, false);
    }

    @Override
    public void update(ISearchModel searchModel) {
        if (!(searchModel instanceof SearchDetailModel)) {
            return;
        }
        SearchDetailModel model = (SearchDetailModel) searchModel;

        if (mDataBinding == null) {
            mDataBinding = new SearchResultDataBinding();
        }
        mBinding.setVariable(BR.authorBind, mDataBinding);
        mBinding.setVariable(BR.model, model);
        mBinding.executePendingBindings();
        super.update(searchModel);
    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.textViewName;
    }

    @Override
    protected TextView getTagTextView() {
        return null;
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
