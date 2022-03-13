package com.jxntv.base.recycler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.R;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.base.view.DefaultRefreshFooterView;
import com.jxntv.base.view.DefaultRefreshHeadView;
import com.jxntv.base.view.GVideoSmartRefreshLayout;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewModel;
import com.jxntv.base.view.recyclerview.interf.IBaseRecyclerView;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.ResourcesUtils;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * 持有recycler基类实现
 *
 *
 * @since 2020.1.17
 */
public abstract class BaseRecyclerFragment<MODEL, T extends ViewDataBinding>  extends BaseFragment<T>
        implements OnRefreshLoadMoreListener, IBaseRecyclerView {

    /** 基础刷新布局 */
    protected GVideoSmartRefreshLayout mRefreshLayout;
    /** 内置recyclerView */
    protected RecyclerView mRecyclerView;
    /** recyclerView -- adapter */
    protected BaseRecyclerAdapter<MODEL, ?> mAdapter;
    /** 空页面，用于各类错误信息显示 */
    protected ViewGroup mPlaceHolderContainer;
    /** 当前持有的recyclerView id */
    private int mRecyclerViewId;
    /** 刷新页面对应的id */
    private int mRefreshViewId;
    /** 占位页面布局对应的id */
    private int mPlaceHolderViewId;

    /**
     * 获取layoutManager
     *
     * @return 默认为线性manager
     */
    @NonNull
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    /**
     * 创建Item decoration
     *
     * @return 默认为null
     */
    @Nullable
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return null;
    }

    /**
     * 创建recyclerView对应的adapter，子类实现
     */
    @NonNull
    protected abstract BaseRecyclerAdapter<MODEL, ?> createAdapter();

    /**
     * 当前是否为固定大小
     *
     * @return  默认为固定大小
     */
    protected boolean hasFixedSize() {
        return false;
    }

    /**
     * 是否支持刷新，默认支持
     *
     * @return 是否支持刷新
     */
    protected boolean enableRefresh() {
        return true;
    }

    /**
     * 是否支持加载更多，默认支持
     */
    protected boolean enableLoadMore() {
        return true;
    }

    /**
     * 设置是否在没有更多数据之后 Footer 跟随内容
     */
    protected boolean enableFooterFollowWhenNoMoreData() {
        return true;
    }

    /**
     * 创建刷新footerView，子类实现
     */
    protected RefreshFooter createRefreshFooterView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new DefaultRefreshFooterView(activity);
    }

    /**
     * 创建刷新HeaderView，子类实现
     */
    protected RefreshHeader createRefreshHeaderView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new DefaultRefreshHeadView(activity);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override protected void initView() {
        View view = mBinding.getRoot();
        initId();
        initRecycleView(view);
        initRefreshView(view);
        initPlaceHolderView(view);
    }

  @Override
  protected <VM extends BaseViewModel> VM bingViewModel(@NonNull Class<VM> viewModelClass) {
    VM vm = super.bingViewModel(viewModelClass);

    if (vm instanceof BaseRecyclerViewModel) {
      ((BaseRecyclerViewModel) vm).init(mAdapter, this);
      ((BaseRecyclerViewModel) vm).getFinishRefreshLiveData().observe(this, new Observer<Boolean>() {
        @Override public void onChanged(Boolean scrollToTop) {
          finishRefresh();
          if (scrollToTop) {
            mRecyclerView.scrollToPosition(0);
          }
        }
      });

      ((BaseRecyclerViewModel) vm).getHasMoreDataLiveData().observe(this, new Observer<Boolean>() {
          @Override public void onChanged(Boolean hasMoreData) {
              if (mRefreshLayout!=null) {
                  mRefreshLayout.finishRefresh();
                  mRefreshLayout.finishLoadMore();
                  mRefreshLayout.post(()-> mRefreshLayout.setNoMoreData(!hasMoreData));
                  mRefreshLayout.setEnableLoadMore(hasMoreData);
              }
          }
      });
    }

    return vm;
  }

  /**
     * 初始化对应的viewId
     */
    private void initId() {
        mRecyclerViewId = initRecyclerViewId();
        mPlaceHolderViewId = initPlaceHolderId();
        mRefreshViewId = initRefreshViewId();
    }

    /**
     * 初始化recyclerView id,子类实现
     */
    protected abstract int initRecyclerViewId();

    /**
     * 初始化占位视图容器 id,子类实现
     */
    protected abstract int initPlaceHolderId();

    /**
     * 初始化刷新视图 id,子类实现 {@link GVideoSmartRefreshLayout}
     */
    protected abstract int initRefreshViewId();

    /**
     * 初始化内部recyclerView
     *
     * @param view fragment页面
     */
    private void initRecycleView(@NonNull View view) {
        mRecyclerView = view.findViewById(mRecyclerViewId);
        mRecyclerView.setLayoutManager(createLayoutManager());
        mRecyclerView.setHasFixedSize(hasFixedSize());
        RecyclerView.ItemDecoration itemDecoration = createItemDecoration();
        if (itemDecoration != null) {
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mAdapter = createAdapter();
        mRecyclerView.setAdapter(mAdapter);

    }

    /**
     * 初始化内部刷新view
     *
     * @param view fragment页面
     */
    private void initRefreshView(@NonNull View view) {
        mRefreshLayout = view.findViewById(mRefreshViewId);
        if (mRefreshLayout == null) {
            return;
        }
        boolean enableRefresh = enableRefresh();
        mRefreshLayout.setEnableRefresh(enableRefresh);
        // 绑定刷新数据
        if (enableRefresh) {
            RefreshHeader refreshHeader = createRefreshHeaderView();
            if (refreshHeader != null) {
                mRefreshLayout.setRefreshHeader(refreshHeader);
            }
            mRefreshLayout.setOnRefreshListener(this);
        } else {
            mRefreshLayout.setEnableRefresh(false);
        }
        boolean enableLoadMore = enableLoadMore();
        mRefreshLayout.setEnableLoadMore(enableLoadMore);
        // 绑定获取更多数据数据
        if (enableLoadMore) {
            RefreshFooter refreshFooter = createRefreshFooterView();
            if (refreshFooter != null) {
                mRefreshLayout.setRefreshFooter(refreshFooter);
            }
            mRefreshLayout.setOnLoadMoreListener(this);
        }
        mRefreshLayout.setEnableFooterFollowWhenNoMoreData(enableFooterFollowWhenNoMoreData());
    }

    /**
     * 初始化占位页面
     *
     * @param view fragment页面
     */
    private void initPlaceHolderView(@NonNull View view) {
        mPlaceHolderContainer = view.findViewById(mPlaceHolderViewId);
        if (mPlaceHolderContainer == null) {
            return;
        }
        setupPlaceholderLayout(mPlaceHolderViewId);
    }

    @Override
    protected void updatePlaceholderLayoutType(@PlaceholderType int type) {
      updatePlaceholderLayoutType(type, getPlaceHolderPaddingTop(), needShowPlaceHolderOnTop());
    }

    @Override protected void updatePlaceholderLayoutType(@PlaceholderType int type, int paddingTop, boolean needShowTop) {
        if (mPlaceHolderContainer == null) return;
        super.updatePlaceholderLayoutType(type, paddingTop, needShowTop);
        if (type == PlaceholderType.NONE) {
            mPlaceHolderContainer.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mPlaceHolderContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    public boolean isCurrentHolderStatusNone(){
        return mPlaceHolderContainer.getVisibility()==View.GONE;
    }

    /**
     * 结束刷新
     */
    protected void finishRefresh() {
        if (mRefreshLayout == null) {
            return;
        }
        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onReload(@NonNull View view) {
      loadData();
    }

    @Override public void onLogin(@NonNull View view) {
        AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
        if (plugin != null) {
            plugin.startLoginActivity(view.getContext());
            PluginManager.get(StatPlugin.class).enterRegister(
                    getPid(),
                    ResourcesUtils.getString(R.string.follow)
            );
        }
    }

    /**
     * 获取默认顶部距离
     *
     * @return 顶部距离，默认为0
     */
    protected int getPlaceHolderPaddingTop() {
      return 0;
    }

    /**
     * 是否需要在顶部显示placeHolder
     *
     * @return 是否需要在顶部显示placeHolder， 默认false
     */
    protected boolean needShowPlaceHolderOnTop() {
      return false;
    }
}
