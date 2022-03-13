package com.jxntv.circle.fragment;

import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_SELECT_CIRCLE;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.FragmentCircleSelectBinding;
import com.jxntv.circle.viewmodel.CircleSelectViewModel;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.stat.StatPid;

public class CircleSelectFragment extends BaseFragment<FragmentCircleSelectBinding> {

    // CircleSelectViewModel
    public CircleSelectViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_select;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.finish();
    }

    @Override
    protected void initView() {
        setToolbarTitle(getString(R.string.circle_select));
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(CircleSelectViewModel.class);
        mBinding.setViewModel(viewModel);

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(viewModel.selectCircleAdapter);
        viewModel.selectCircleAdapter.setOnItemClickListener(
                circle -> {
                    if (circle == null) {
                        return;
                    }
                    if (circle.isJoin()) {
                        sendCircleAndFinish(circle);
                    } else {
                        viewModel.joinCircle(getActivity(), circle);
                    }
                });

        viewModel.joinCircleSuccess.observe(this, this::sendCircleAndFinish);

        viewModel.showTip.observe(this, showTip -> mBinding.tip.setVisibility(showTip ? View.VISIBLE : View.GONE));
    }

    private void sendCircleAndFinish(Circle circle) {
        GVideoEventBus.get(EVENT_BUS_SELECT_CIRCLE, Circle.class).post(circle);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.finish();
    }

    @Override
    protected void loadData() {
        viewModel.loadMyCircle(mBinding.refreshLayout);
    }

    @Override
    public String getPid() {
        return StatPid.CIRCLE_SELECT;
    }

}
