package com.hzlz.aviation.feature.record.recorder.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.record.databinding.DialogSelectCommunityBinding;
import com.hzlz.aviation.feature.record.recorder.adapter.SelectCommunityAdapter;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoLimitWHBottomSheetDialog;
import com.hzlz.aviation.feature.record.R;

import java.util.ArrayList;
import java.util.List;

public class SelectCommunityDialog extends GVideoLimitWHBottomSheetDialog {

    // 布局Binding
    private DialogSelectCommunityBinding binding;

    // CirclePlugin
    private final CirclePlugin circlePlugin;

    // 页码
    public int pageNum = Constant.PAGE_DEFAULT.FIRST_PAGE;

    // 每页的数量
    public int pageSize = 10;

    // 向上层传递选择数据的回调
    private OnSelectListener onSelectListener;

    // 社区列表的适配器
    private SelectCommunityAdapter selectCommunityAdapter;

    // 当前已经选中的社区信息
    private Circle selectedCircle;

    public SelectCommunityDialog(Context context) {
        super(context);
        setUp(context);
        circlePlugin = PluginManager.get(CirclePlugin.class);
    }

    public void setUp(Context context) {
        View rootView = onCreateView(LayoutInflater.from(context), null, null);
        setContentView(rootView);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        mExpectWindowHeight = (int) (ScreenUtils.getScreenHeight(context) * 0.8f);
    }

    private View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_select_community,
                container,
                false
        );

        binding.pagePlaceHolder.init(
                v -> {
                },
                v -> refreshDataList()
        );

        binding.refreshLayout.setGVideoOnRefreshListener(view -> refreshDataList());
        binding.refreshLayout.setGVideoOnLoadMoreListener(view -> loadMoreDataList());
        selectCommunityAdapter = new SelectCommunityAdapter(getContext());
        selectCommunityAdapter.setOnSelectListener((view, circle) -> {
            if (onSelectListener == null) {
                dismiss();
                return;
            }
            onSelectListener.result((circle == null || circle.groupId < 0) ? null : circle);
            dismiss();
        });
        binding.recyclerView.setAdapter(selectCommunityAdapter);
        return binding.getRoot();
    }

    public void refreshDataList() {
        if (binding == null) {
            return;
        }
        if (selectCommunityAdapter.isDataSourceEmpty()) {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        circlePlugin.getRecommendCommunity(
                Constant.PAGE_DEFAULT.FIRST_PAGE,
                pageSize,
                selectedCircle == null ? null : selectedCircle.groupId,
                new BaseResponseObserver<ListWithPage<Circle>>() {
                    @Override
                    protected void onRequestData(ListWithPage<Circle> circleListWithPage) {
                        if (binding == null) {
                            return;
                        }
                        pageNum = 1;
                        binding.refreshLayout.finishGVideoRefresh();

                        List<Circle> dataList = new ArrayList<>();
                        dataList.add(new Circle(-1));
                        dataList.addAll(circleListWithPage.getList());

                        selectCommunityAdapter.replaceData(dataList);
                        binding.refreshLayout.setNoMoreData(!circleListWithPage.getPage().hasNextPage());

                        updateSuccessPagePlaceHolder();
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        if (binding == null) {
                            return;
                        }
                        binding.refreshLayout.finishGVideoRefresh();
                        updateFailedPagePlaceHolder();
                    }

                });
    }

    private void updateSuccessPagePlaceHolder() {
        if (selectCommunityAdapter.isDataSourceEmpty()) {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.EMPTY);
        } else {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.NONE);
        }
    }

    private void updateFailedPagePlaceHolder() {
        if (selectCommunityAdapter.isDataSourceEmpty()) {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.ERROR);
        } else {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.NONE);
        }
    }

    public void loadMoreDataList() {
        if (binding == null) {
            return;
        }
        if (selectCommunityAdapter.isDataSourceEmpty()) {
            binding.pagePlaceHolder.updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        circlePlugin.getRecommendCommunity(
                pageNum + 1,
                pageSize,
                selectedCircle == null ? null : selectedCircle.groupId,
                new BaseResponseObserver<ListWithPage<Circle>>() {
                    @Override
                    protected void onRequestData(ListWithPage<Circle> circleListWithPage) {
                        if (binding == null) {
                            return;
                        }
                        pageNum++;
                        binding.refreshLayout.finishGVideoLoadMore();
                        selectCommunityAdapter.addData(circleListWithPage.getList());
                        binding.refreshLayout.setNoMoreData(!circleListWithPage.getPage().hasNextPage());

                        updateSuccessPagePlaceHolder();
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        if (binding == null) {
                            return;
                        }
                        binding.refreshLayout.finishGVideoLoadMore();

                        updateFailedPagePlaceHolder();
                    }
                });
    }

    public void updateCircle(Circle circle) {
        selectedCircle = circle;
        if (selectCommunityAdapter == null) {
            return;
        }
        selectCommunityAdapter.updateCircle(circle);
    }

    public interface OnSelectListener {
        void result(Circle circle);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }
}
