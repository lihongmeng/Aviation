package com.jxntv.account.ui.notification;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentSysytemMessageBinding;
import com.jxntv.base.BaseFragment;

/**
 * @author huangwei
 * date : 2021/10/22
 * desc : 系统消息、系统通知
 **/
public class SystemMessageFragment extends BaseFragment<FragmentSysytemMessageBinding> {
    //<editor-fold desc="属性">
    private SystemMessageViewModel viewModel;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sysytem_message;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(SystemMessageViewModel.class);
        mBinding.setViewModel(viewModel);
    }

    @Override
    protected void loadData() {
        viewModel.onRefresh(mBinding.refreshLayout);
    }
    //</editor-fold>
}

