package com.hzlz.aviation.feature.community.fragment;

import static com.hzlz.aviation.kernel.base.plugin.RecordPlugin.EVENT_BUS_SELECT_CIRCLE;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentCircleSelectBinding;
import com.hzlz.aviation.feature.community.viewmodel.CircleSelectViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

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
