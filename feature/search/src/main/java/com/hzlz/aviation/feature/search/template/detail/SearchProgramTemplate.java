package com.hzlz.aviation.feature.search.template.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.search.adapter.program.ProgramListAdapter;
import com.hzlz.aviation.feature.search.databinding.SearchProgramBinding;
import com.hzlz.aviation.feature.search.databinding.SearchResultDataBinding;
import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.template.SearchBaseTemplate;
import com.hzlz.aviation.kernel.base.decoration.GapItemDecoration;
import com.hzlz.aviation.feature.search.BR;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索单图模板类型
 */
public class SearchProgramTemplate extends SearchBaseTemplate {

    /** 搜索单图binding */
    private SearchProgramBinding mBinding;
    /** 搜索结果data binding */
    private SearchResultDataBinding mDataBinding;
    /** 是否显示source */
    private boolean needShowSource;

    /**
     * 构造函数
     */
    public SearchProgramTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_program,
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

        Context context = mBinding.programList.getContext();
        ProgramListAdapter adapter = new ProgramListAdapter(context);
        mBinding.programList.setLayoutManager(new LinearLayoutManager(context));
        GapItemDecoration decoration = new GapItemDecoration();
        decoration.setVerticalGap(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_3DP));
        mBinding.programList.addItemDecoration(decoration);
        mBinding.programList.setAdapter(adapter);
        adapter.addMoreData(detailModel.recentPrograms);
        adapter.setSearchDetailModel(detailModel);

        super.update(searchModel);

    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.title;
    }

    @Override
    protected TextView getTagTextView() {
        return mBinding.contentTag;
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
