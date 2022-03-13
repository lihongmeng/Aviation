package com.jxntv.circle.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.model.CircleTopicModel;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.circle.R;
import com.jxntv.circle.adapter.SearchTopicAdapter;
import com.jxntv.circle.databinding.FragmentSearchTopicBinding;
import com.jxntv.circle.viewmodel.SearchTopicViewModel;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.SoftInputUtils;
import com.jxntv.widget.GVideoEditText;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jxntv.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_SELECT_CIRCLE_TOPIC;
import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_SELECT_TOPIC;

public class SearchTopicFragment extends BaseFragment<FragmentSearchTopicBinding> {

    public SearchTopicViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_topic;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        updatePlaceholderLayoutType(PlaceholderType.NONE);

        GVideoEditText editText = mBinding.root.findViewById(R.id.edit);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return startSearch();
            }
            return false;
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    mBinding.root.findViewById(R.id.clear).setVisibility(GONE);
                } else {
                    mBinding.root.findViewById(R.id.clear).setVisibility(VISIBLE);
                }
            }
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // mBinding.recyclerView.setVisibility(GONE);
            }
        });
        mBinding.root.findViewById(R.id.clear).setOnClickListener(v -> {
            viewModel.searchWord.setValue("");
            SoftInputUtils.showSoftInput(editText, v.getContext(), 0);
        });

        mBinding.root.findViewById(R.id.search).setOnClickListener(v -> {
            startSearch();
        });

        mBinding.root.findViewById(R.id.top_back).setOnClickListener(v -> {
            SoftInputUtils.hideSoftInput(getActivity());
            activity.finish();
        });
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(SearchTopicViewModel.class);
        mBinding.setViewModel(viewModel);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        viewModel.circle = bundle.getParcelable(CIRCLE);

        viewModel.searchWord.observe(
                this,
                searchWord -> viewModel.onRefresh(mBinding.refreshLayout)
        );

        viewModel.searchTopicAdapter.setListener(listener);

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.SEARCH_TOPIC;
    }

    @Override
    protected void loadData() {

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();

        viewModel.searchWord.setValue("");
    }

    public boolean startSearch() {
        GVideoEditText editText = mBinding.root.findViewById(R.id.edit);
        Editable text = editText.getText();
        if (text == null) {
            return false;
        }
        String searchWord = text.toString();
        if (TextUtils.isEmpty(searchWord)) {
            return false;
        }
        hideSoftInputNoToken();
        viewModel.searchWord.setValue(searchWord);
        return true;
    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.onRefresh(mBinding.refreshLayout);
        }
    }

    private final SearchTopicAdapter.Listener listener = new SearchTopicAdapter.Listener() {
        @Override
        public void onItemClick(TopicDetail topicDetail) {
            GVideoEventBus.get(EVENT_BUS_SELECT_TOPIC, TopicDetail.class).post(topicDetail);
            finishActivity();
        }
    };

}
