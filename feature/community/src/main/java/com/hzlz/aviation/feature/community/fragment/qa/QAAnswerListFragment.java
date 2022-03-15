package com.hzlz.aviation.feature.community.fragment.qa;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.START_CIRCLE_DETAIL;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.adapter.qa.QAAnswerListAdapter;
import com.hzlz.aviation.feature.community.databinding.FragmentQaAnswerListBinding;
import com.hzlz.aviation.feature.community.databinding.LayoutQaAnswerListHeaderBinding;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerHeaderFooterAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * @author huangwei
 * date : 2022/1/5
 * desc : 问答 - 回答者选择界面
 **/

public class QAAnswerListFragment extends BaseRecyclerFragment<AuthorModel, FragmentQaAnswerListBinding> {

    private QAAnswerListViewModel viewModel;
    private Circle circle;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qa_answer_list;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        circle = bundle.getParcelable(CIRCLE);
    }

    @Override
    public String getPid() {
        return StatPid.QUESTION_ANSWER_LIST;
    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<AuthorModel, ?> createAdapter() {
        return new QAAnswerListAdapter(getContext(), getPageName());
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbarTitle("全部");
        ((BaseRecyclerHeaderFooterAdapter) mAdapter).addHeaderView(getHeadView());
        ((QAAnswerListAdapter) mAdapter).setCircle(circle);

    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected void bindViewModels() {
        if (circle == null) {
            onLeftBackPressed(mBinding.getRoot());
        }
        viewModel = bingViewModel(QAAnswerListViewModel.class);
        viewModel.setCircle(circle);
        viewModel.setPid(getPid());

        GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH).observe(
                this,
                o ->{
                    super.onLeftBackPressed(mBinding.root);
                    GVideoEventBus.get(START_CIRCLE_DETAIL).post(null);
                }
        );

    }

    @Override
    protected String getSourcePage() {
        return getStringDataFromBundle(Constant.EXTRA_FROM_PID);
    }

    @Override
    protected void loadData() {
        viewModel.loadRefreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        viewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        viewModel.loadRefreshData();
    }

    private View getHeadView() {
        LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View topView = inflaters.inflate(R.layout.layout_qa_answer_list_header, null);
        LayoutQaAnswerListHeaderBinding topBinding = DataBindingUtil.bind(topView);
        if (topBinding == null) {
            return topView;
        }
        topBinding.setViewModel(new QAAnswerMessageViewModel(circle, getPid()));
        return topView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

}
