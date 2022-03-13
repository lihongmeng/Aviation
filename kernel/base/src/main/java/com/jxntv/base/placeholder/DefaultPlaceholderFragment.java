package com.jxntv.base.placeholder;

import android.view.View;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.R;
import com.jxntv.base.databinding.DefaultEmptyLayoutBinding;

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
