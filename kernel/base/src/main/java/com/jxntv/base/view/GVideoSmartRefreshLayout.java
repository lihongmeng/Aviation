package com.jxntv.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

/**
 * 继承刷新layout的布局，供端内统一使用
 *
 *
 * @since 2020.1.17
 */
public class GVideoSmartRefreshLayout extends SmartRefreshLayout implements
    IGVideoRefreshLoadMoreView {
  //<editor-fold desc="属性">
  @Nullable
  private IGVideoRefreshLoadMoreView.OnRefreshListener mGVideoOnRefreshListener;
  @Nullable
  private IGVideoRefreshLoadMoreView.OnLoadMoreListener mGVideoOnLoadMoreListener;
  //</editor-fold>

  /**
   * 构造方法
   */
  public GVideoSmartRefreshLayout(Context context) {
    super(context);
  }

  /**
   * 构造方法
   */
  public GVideoSmartRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void triggerRefresh() {
    if (mRefreshListener != null) {
      mRefreshListener.onRefresh(this);
    }
  }

  //<editor-fold desc="IGVideoRefreshLoadMoreView 接口方法实现">

  // 刷新接口实现
  private final com.scwang.smartrefresh.layout.listener.OnRefreshListener mSmartOnRefreshListener =
      new com.scwang.smartrefresh.layout.listener.OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
          if (mGVideoOnRefreshListener != null
              && refreshLayout instanceof GVideoSmartRefreshLayout) {
            mGVideoOnRefreshListener.onRefresh((IGVideoRefreshLoadMoreView) refreshLayout);
          }
        }
      };

  @Override
  public void enableGVideoRefresh(boolean enable) {
    setEnableRefresh(enable);
  }

  @Override
  public void setGVideoOnRefreshListener(@Nullable OnRefreshListener listener) {
    mGVideoOnRefreshListener = listener;
    setOnRefreshListener(mSmartOnRefreshListener);
  }

  @Override
  public void finishGVideoRefresh() {
    finishRefresh();
  }

  // 加载更多接口实现
  private final com.scwang.smartrefresh.layout.listener.OnLoadMoreListener mSmartLoadMoreListener =
      new com.scwang.smartrefresh.layout.listener.OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
          if (mGVideoOnLoadMoreListener != null
              && refreshLayout instanceof GVideoSmartRefreshLayout) {
            mGVideoOnLoadMoreListener.onLoadMore((IGVideoRefreshLoadMoreView) refreshLayout);
          }
        }
      };

  @Override
  public void enableGVideoLoadMore(boolean enable) {
    setEnableLoadMore(enable);
  }

  @Override
  public void setGVideoOnLoadMoreListener(@Nullable OnLoadMoreListener listener) {
    mGVideoOnLoadMoreListener = listener;
    setOnLoadMoreListener(mSmartLoadMoreListener);
  }

  /**
   * 黏性移动 spinner
   * @param spinner 偏移量
   */
  protected void moveSpinnerInfinitely(float spinner) {
    final View thisView = this;
    if (mNestedInProgress && !mEnableLoadMoreWhenContentNotFull && spinner < 0) {
      if (!mRefreshContent.canLoadMore()) {
        /*
         * 2019-1-22 修复 嵌套滚动模式下 mEnableLoadMoreWhenContentNotFull=false 无效的bug
         */
        spinner = 0;
      }
    }

    if (mState == RefreshState.TwoLevel && spinner > 0) {
      mKernel.moveSpinner(Math.min((int) spinner, thisView.getMeasuredHeight()), true);
    } else if (mState == RefreshState.Refreshing && spinner >= 0) {
      if (spinner < mHeaderHeight) {
        mKernel.moveSpinner((int) spinner, true);
      } else {
        final double M = (mHeaderMaxDragRate - 1) * mHeaderHeight;
        final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mHeaderHeight;
        final double x = Math.max(0, (spinner - mHeaderHeight) * mDragRate);
        final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
        mKernel.moveSpinner((int) y + mHeaderHeight, true);
      }
    } else if (spinner < 0 && (mState == RefreshState.Loading
        || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(mEnableLoadMore))
        || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore)))) {
      if (spinner > -mFooterHeight) {
        mKernel.moveSpinner((int) spinner, true);
      } else {
        final double M = (mFooterMaxDragRate - 1) * mFooterHeight;
        final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mFooterHeight;
        final double x = -Math.min(0, (spinner + mFooterHeight) * mDragRate);
        final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
        mKernel.moveSpinner((int) y - mFooterHeight, true);
      }
    } else if (spinner >= 0) {
      final double M = mHeaderMaxDragRate * mHeaderHeight;
      final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
      final double x = Math.max(0, spinner * mDragRate);
      final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
      mKernel.moveSpinner((int) y, true);
    } else {
      final double M = mFooterMaxDragRate * mFooterHeight;
      final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
      final double x = -Math.min(0, spinner * mDragRate);
      final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
      mKernel.moveSpinner((int) y, true);
    }
    if (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore) && spinner < 0
        && mState != RefreshState.Refreshing
        && mState != RefreshState.Loading
        && mState != RefreshState.LoadFinish) {
      if (mDisableContentWhenLoading) {
        animationRunnable = null;
        mKernel.animSpinner(-mFooterHeight);
      }
      setStateDirectLoading(false);
      /*
       * 自动加载模式时，延迟触发 onLoadMore ，mReboundDuration 保证动画能顺利执行
       */
      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (mLoadMoreListener != null) {
            mLoadMoreListener.onLoadMore(GVideoSmartRefreshLayout.this);
          } else if (mOnMultiPurposeListener == null) {
            finishLoadMore(2000);//如果没有任何加载监听器，两秒之后自动关闭
          }
          final com.scwang.smartrefresh.layout.listener.OnLoadMoreListener listener = mOnMultiPurposeListener;
          if (listener != null) {
            listener.onLoadMore(GVideoSmartRefreshLayout.this);
          }
        }
      }, mReboundDuration);
    }
  }

  @Override
  public void finishGVideoLoadMore() {
    finishLoadMore();
  }
  //</editor-fold>
}
