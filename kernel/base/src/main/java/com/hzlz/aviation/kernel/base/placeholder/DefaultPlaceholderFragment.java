package com.hzlz.aviation.kernel.base.placeholder;

import android.view.View;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.DefaultEmptyLayoutBinding;

public class DefaultPlaceholderFragment extends BaseFragment<DefaultEmptyLayoutBinding> {


    private PlaceholderListener mListener;
    public DefaultPlaceholderFragment(PlaceholderListener listener) {
        super();
        mListener = listener;
    }



    @Override
    protected int getLayoutId() {
        return R.layout.default_empty_layout;
    }

    @Override
    protected void initView() {
        mBinding.functionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onReload(v);
                }
            }
        });

    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }
}
