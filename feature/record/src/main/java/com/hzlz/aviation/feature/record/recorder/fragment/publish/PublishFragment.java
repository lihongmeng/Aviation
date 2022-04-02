package com.hzlz.aviation.feature.record.recorder.fragment.publish;

import static com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.hzlz.aviation.feature.record.R;
import com.hzlz.aviation.feature.record.databinding.LayoutPublishBinding;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

public class PublishFragment extends BaseFragment<LayoutPublishBinding> {


    // PublishViewModel
    private PublishViewModel viewModel;

    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_publish;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {

    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(PublishViewModel.class);
        mBinding.setViewModel(viewModel);
    }

    @Override
    protected void loadData() {

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

    @Override
    public String getPid() {
        return StatPid.PUBLISH_COMPOSITION;
    }

    @Override
    protected String getSourcePage() {
        return super.getSourcePage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VideoChooseHelper.IMAGE_NUM_LIMIT = DEFAULT_MAX_SELECT_IMAGE_NUM;
    }


}
