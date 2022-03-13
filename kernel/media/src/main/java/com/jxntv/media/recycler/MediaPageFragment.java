package com.jxntv.media.recycler;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.android.liteav.GVideoView;
import com.jxntv.base.BackPressHandler;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.recycler.BaseRecyclerFragment;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewModel;
import com.jxntv.base.view.recyclerview.RecyclerViewVideoOnScrollListener;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.MediaConstants;
import com.jxntv.media.MediaFragmentManager;
import com.jxntv.media.R;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.player.MediaPlayManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ScreenUtils;
import com.jxntv.utils.SizeUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import io.reactivex.rxjava3.core.Observable;

/**
 * media page页面fragment
 */
public abstract class MediaPageFragment<T extends ViewDataBinding> extends BaseRecyclerFragment<MediaModel, T> {

    /**
     * recyclerView -- 数据模型
     */
    public MediaPageViewModel mViewModel;

    /**
     * 如果是后端配置的信息流，需要使用tabId获取数据
     */
    protected String mTabItemId;

    /**
     * 神策数据统计需要记录页面的tab名称
     */
    protected String tabName;

    /**
     * 是否为黑暗模式
     */
    protected boolean mIsDarkMode;
    /**
     * recycler view inch 滑动时间
     */
    private static final float DURATION_INCH = 0.05f;
    /**
     * 是否应该滑动
     */
    private boolean mShouldScroll = false;
    /**
     * 当前播放的位置
     */
    private int mToPosition = -1;
    /**
     * 是否第一次列表滑动
     */
    private boolean isFirstScroll = true;

    private boolean isFirstFinishRefresh = true;


    /**
     * tab切换后自动播放延时Runnable，在onInvisible时移除
     */
    private Runnable mPlayRunnable;
    /**
     * 自动播放下一个Runnable，在onInvisible时移除
     */
    private Runnable mPlayNextRunnable;

    //当前显示的位置
    private int mFistVisible = -1, mLastVisible = -1;

    private class PlayRunnable implements Runnable {
        private int mNextPosition;

        public PlayRunnable(int nextPosition) {
            mNextPosition = nextPosition;
        }

        @Override
        public void run() {
            // 全屏模式后，此处不播放
            Context context = getContext();
            if (!isResumed() || !isUIVisible() || context == null || ScreenUtils.isLandscape(requireContext()))
                return;
            if (mNextPosition < 0) {
                if (mTabItemId != null) {
                    boolean isCanPlay = true;
                    if (mAdapter instanceof MediaRecyclerAdapter) {
                        isCanPlay = ((MediaRecyclerAdapter) mAdapter).canPlay(0);
                    }
                    if (isCanPlay && NetworkUtils.isNetworkConnected()) {
                        MediaPlayManager.getInstance().tryStartPlay(mTabItemId);
                    }
                }
            } else {
                MediaPlayManager.getInstance().play(mTabItemId, mNextPosition);
            }
        }
    }

    public String getChannelId() {
        return mTabItemId;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 初始化
     *
     * @param itemInfo   tab信息
     * @param isDarkMode 是否为暗黑模式
     */
    public void init(String itemInfo, boolean isDarkMode) {
        mTabItemId = itemInfo;
        // StaticParams.currentTabId = mTabItemId;
        if (mTabItemId != null) {
            MediaPlayManager.getInstance().registerFragment(mTabItemId, this);
        }
        mIsDarkMode = isDarkMode;
        if (itemInfo != null) {
            if (mAdapter instanceof MediaRecyclerAdapter) {
                ((MediaRecyclerAdapter) mAdapter).setTabId(itemInfo);
            }
            if (mViewModel instanceof MediaPageViewModel) {
                mViewModel.updateTabId(itemInfo);
            }
        }
    }

    /**
     * 初始化
     *
     * @param itemInfo   tab信息
     * @param isDarkMode 是否为暗黑模式
     */
    public void init(String itemInfo, String tabName, boolean isDarkMode) {
        this.tabName = tabName;
        init(itemInfo, isDarkMode);
    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<MediaModel, MediaRecyclerVH> createAdapter() {
        MediaRecyclerAdapter adapter = new MediaRecyclerAdapter(getContext());
        adapter.setIsDarkMode(mIsDarkMode);
        adapter.setViewGroup(findFullScreenVideoLayout());
        if (mTabItemId != null) {
            adapter.setTabId(mTabItemId);
        }
        return adapter;
    }

    @Override
    protected <VM extends BaseViewModel> VM bingViewModel(@NonNull Class<VM> viewModelClass) {
        VM vm = super.bingViewModel(viewModelClass);
        if (vm instanceof MediaPageViewModel) {
            mViewModel = (MediaPageViewModel) vm;
        }
        return vm;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.media_recycler_layout;
    }

    @Override
    protected void initView() {
        super.initView();

        MediaFragmentManager.getInstance().addFragmentRef(this);
        if (mAdapter instanceof MediaRecyclerAdapter) {
            ((MediaRecyclerAdapter) mAdapter).setFragmentId(mId);
        }
        //TODO 待完成
        mRecyclerView.addOnScrollListener(new RecyclerViewVideoOnScrollListener(mRecyclerView,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {
                        //滑动过程中pgc页面不检查自动播放
//                        if (!getPid().equals(StatPid.PGC_CONTENT)) {
                        checkAutoPlay(fistVisible, lastVisible);
//                        }
                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {
                        mFistVisible = fistVisible;
                        mLastVisible = lastVisible;
                        isFirstScroll = false;
                        //停止滑动pgc页面检查自动播放
//                        if (getPid().equals(StatPid.PGC_CONTENT)) {
//                            checkAutoPlay(fistVisible,lastVisible);
//                        }

                    }

                    @Override
                    public void onItemEnter(int position) {
                        postExposure(position, position, true);
                    }

                    @Override
                    public void onItemExit(int position) {
                        postExposure(position, position, false);
                    }
                }));

    }

    private void checkAutoPlay(int fistVisible, int lastVisible) {
        GVideoEventBus.get(Constant.EVENT_MSG.COLLAPSE_AUDIO_FLOAT_VIEW).post(null);
        if (!isFirstShowCanAutoPlay()) {
            isFirstScroll = false;
            return;
        }
        if (mShouldScroll) {
            mShouldScroll = false;
            smoothMoveToPosition(mToPosition);
        } else {
            // 全屏模式后，此处不播放
            if (ScreenUtils.isLandscape(requireContext()) || !isUIVisible()) return;
            if (mTabItemId != null) {
                //最后一个item不检查
                if (!isLastPosition) {
//                    int position = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
                    if (mAdapter instanceof MediaRecyclerAdapter) {
//                        int headCount = mAdapter.getHeaderViewCount();
//                        if (!((MediaRecyclerAdapter) mAdapter).canPlay(position - headCount)) {
//                            return;
//                        }
//                        if (lastVisible > mAdapter.getItemCount() - mAdapter.getHeaderViewCount() - 1) {
//                            isLastPosition = true;
//                            return;
//                        }
                        MediaPlayManager.getInstance().tryStartPlay(mTabItemId);
                    }
                }
                isLastPosition = false;
            }
        }
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {
        listenEvent();
        handleLoadData(false);
    }

    /**
     * 处理加载数据
     *
     * @param isInitial 是否未初始化
     */
    protected void handleLoadData(boolean isInitial) {
        if (isInitial) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
            mViewModel.initialData();
        } else {
            mViewModel.loadRefreshData();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.loadRefreshData();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    protected void listenEvent() {
        // 登录
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
                this, new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        checkLoginStatus();
                    }
                }
        );
        // 登出
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
                this, new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        checkLoginStatus();
                    }
                }
        );
        // 删除数据
        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(
                this, o -> {
                    if (mAdapter != null && mAdapter.getItemCount() > 0 && mAdapter instanceof MediaRecyclerAdapter) {
                        int size = ((MediaRecyclerAdapter) mAdapter).remove((String) o);
                        if (size == 0) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        }
                    } else {
                        checkLoginStatus();
                    }
                }
        );

        GVideoEventBus.get(Constant.EVENT_MSG.BACK_TOP).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (isUIVisible()) {
                    mRecyclerView.scrollToPosition(0);
                    mRefreshLayout.autoRefresh();
                }
            }
        });
    }

    protected void checkLoginStatus() {

    }

    protected void dealAutoRefresh() {
        if (mRecyclerView!=null
                && System.currentTimeMillis() - leaveTime > Constant.CONFIG.AUTO_REFRESH_TIME) {
            leaveTime = System.currentTimeMillis();
            // StaticParams.timeToLateNeedUpdate = false;
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.autoRefresh();
        }
    }

    /**
     * 获取video全屏父布局
     *
     * @return video全屏父布局
     */
    public ViewGroup findFullScreenVideoLayout() {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            if (window != null) {
                return (ViewGroup) window.getDecorView();
            }
        }
        return null;
    }

    /**
     * 获取tabId
     *
     * @return tabId
     */
    protected String getTabId() {
        return mTabItemId;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MediaPlayManager.getInstance().muteSound(getTabId(),true);
        dealAutoRefresh();
    }


    /**
     * 不可见回调
     */
    protected void onInVisible() {
        super.onInVisible();
        if (mTabItemId != null) {
            MediaPlayManager.getInstance().stop(mTabItemId);
        }
        mHandler.removeCallbacks(mPlayNextRunnable);
        mHandler.removeCallbacks(mPlayRunnable);

        showLogDebug("onInVisible", "Fragment名称 -->> " + getClass().getName());

        leaveTime = System.currentTimeMillis();
        // GVideoEventBus.get(NOTIFICATION_HOME_START_FRESH_TASK).post(null);

        MediaPlayManager.getInstance().muteSound(getTabId(),false);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        // StaticParams.currentTabId = mTabItemId;
        postExposure(mFistVisible, mLastVisible, true);
        if (isUIVisible()) {
            if (mTabItemId != null) {
                MediaPlayManager.getInstance().onBackFeed(mTabItemId);
            }
            ((BaseActivity) requireActivity()).registerBackPressHandler(new BackPressHandler() {
                @Override
                public boolean onBackPressed() {
                    ViewGroup viewGroup = findFullScreenVideoLayout();
                    if (viewGroup != null) {
                        View view = viewGroup.findViewWithTag(MediaConstants.playViewTag);
                        if (view instanceof GVideoView) {
                            ((GVideoView) view).handleBackPressed();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        dealAutoRefresh();

    }


    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        postExposure(mFistVisible, mLastVisible, false);
        onInVisible();
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        postExposure(mFistVisible, mLastVisible, true);
    }

//    /**
//     * 父容器不可见回调
//     */
//    public void onParentInVisible() {
//        LogUtils.e(mTabItemId+": onParentInVisible");
//        onInVisible();
//    }

//    /**
//     * 父容器可见回调
//     */
//    public void onParentVisible() {
//        LogUtils.e(mTabItemId+": onParentVisible");
//        onVisible();
//    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        postExposure(mFistVisible, mLastVisible, false);
        onInVisible();
    }

    /**
     * 尝试播放下一个事件
     *
     * @param position 待播放的position
     */
    public void tryAutoPlayNext(int position) {
        mViewModel.tryAutoPlayNext(position);
    }

    /**
     * 自动播放是否是最后一个item
     */
    public boolean isLastPosition = false;

    /**
     * 播放下一个
     *
     * @param nextPosition 待播放的position
     */
    public void playNext(int nextPosition) {
        if (mTabItemId == null) {
            return;
        }
        smoothMoveToPosition(nextPosition + mAdapter.getHeaderViewCount());
        if (!MediaPlayManager.getInstance().play(mTabItemId, nextPosition)) {
            mHandler.removeCallbacks(mPlayNextRunnable);
            mPlayNextRunnable = new PlayRunnable(nextPosition);
            mHandler.postDelayed(mPlayNextRunnable, getScrollToTopDuration());
        }
    }

    /**
     * 移到指定位置播放
     *
     * @param position
     * @return 是否移动
     */
    public boolean moveToPositionPlay(int position) {
        int lastPosition = mAdapter.getItemCount() - 1;
        if (position == lastPosition || position == mToPosition) {
            return false;
        } else {
            smoothMoveToPosition(position);
        }
        return true;
    }

    /**
     * 使指定的项平滑到顶部
     *
     * @param position 待指定的项
     */
    private void smoothMoveToPosition(int position) {
        if (mRecyclerView == null || position < 0) {
            return;
        }
        int firstPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        int lastPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        mToPosition = position;
        if (position < firstPosition) {
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastPosition) {
            int movePosition = position - firstPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                isLastPosition = lastPosition == position;
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);//dx>0===>向左  dy>0====>向上
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 获取滚动整个屏幕高度需要的时间
     *
     * @return 需要的毫秒数
     */
    private long getScrollToTopDuration() {
        int screenHeight = SizeUtils.getDensityHeight();
        return (long) (DURATION_INCH * SizeUtils.getDensity() * screenHeight) + 500;
    }

    @Override
    protected void finishRefresh() {
        super.finishRefresh();
        if (mViewModel != null && mViewModel.getLoadType() != BaseRecyclerViewModel.LOAD_DATA_TYPE_LOAD_MORE) {
            mHandler.removeCallbacks(mPlayRunnable);

            if (isFirstShowCanAutoPlay()) {
                //首页推荐会有2次回调要特殊处理
                if (isFirstFinishRefresh && TextUtils.equals(getPid(), StatPid.HOME_RECOMMEND)) {
                    isFirstFinishRefresh = false;
                } else {
                    isFirstFinishRefresh = false;
                    mPlayRunnable = new PlayRunnable(-1);
                    mHandler.postDelayed(mPlayRunnable, getScrollToTopDuration());
                }
            }

//            if (!TextUtils.equals(getPid(),StatPid.HOME_RECOMMEND) || !isFirstScroll) {
//                if (isFirstFinishRefresh){
//                    isFirstFinishRefresh = false;
//                }else {
//                    mPlayRunnable = new PlayRunnable(-1);
//                    mHandler.postDelayed(mPlayRunnable, getScrollToTopDuration());
//                }
//            }
        }
    }

    /**
     * 加载更多短视频/音频数据
     *
     * @param refresh 进入详情页时首次加载数据需要清空历史cursor
     * @return 对应的observable
     */
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return PluginManager.get(FeedPlugin.class).loadMoreFeedData(refresh, mTabItemId);
    }

    /**
     * 获取当前数据流中短视频列表
     *
     * @return
     */
    public abstract ShortVideoListModel getShortListModel(MediaModel mediaModel);

    @Override
    protected boolean isDarkMode() {
        return mIsDarkMode;
    }


    /**
     * 内容曝光处处理
     *
     * @param start  起始位置
     * @param end    结束位置
     * @param isShow 是否显示
     */
    private void postExposure(int start, int end, boolean isShow) {
        //如果都为-1 ，说明没有滑动使用adapter提供的位置
        if (start == -1 && end == -1 && mAdapter != null) {
            start = 0;
            end = ((MediaRecyclerAdapter) mAdapter).getLastPosition();
        }
        if (start < 0 || end < 0 || end < start) {
            return;
        }
        if (mAdapter instanceof MediaRecyclerAdapter) {
            for (int i = start; i <= end; i++) {
                if (isShow) {
                    ((MediaRecyclerAdapter) mAdapter).enterPosition(i, getPid());
                } else {
                    ((MediaRecyclerAdapter) mAdapter).exitPosition(i, getPid());
                }
            }
        }
    }

    /**
     * 界面第一次展示时是否可以自动播放
     */
    private boolean isFirstShowCanAutoPlay() {

        boolean isCircle = !TextUtils.isEmpty(getPageName()) && getPageName().contains("社区")
                && (getPageName().contains("最新") || getPageName().contains("热聊"));

        if ((!TextUtils.equals(getPid(), StatPid.HOME_RECOMMEND) && !isCircle) || !isFirstScroll) {
            return true;
        } else {
            return false;
        }
    }

}
