package com.hzlz.aviation.feature.feed.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.feed.FeedFragmentManager;
import com.hzlz.aviation.feature.feed.frame.recycler.FeedPageViewModel;
import com.hzlz.aviation.feature.feed.frame.recycler.FeedRecyclerAdapter;
import com.hzlz.aviation.feature.feed.utils.FeedUtils;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.MediaConstants;
import com.hzlz.aviation.kernel.media.databinding.MediaRecyclerLayoutBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.player.MediaPlayManager;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.recycler.MediaRecyclerVH;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

import io.reactivex.rxjava3.core.Observable;

/**
 * feed  page页面fragment
 */
public class FeedPageFragment extends MediaPageFragment<MediaRecyclerLayoutBinding> {
    private FeedPageViewModel mFeedPageViewModel;
    private String mPid;

    @Override
    public String getPid() {
        return mPid;
    }

    public boolean isFeedFragmentVisible() {
        return isUIVisible();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        notifyTemplateOnVisible();
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    /**
     * 初始化方法，记录关键数据tabId,
     * 该数据用于处理多多页面、多播放器状态下的播放器状态同步问题
     *
     * @param tabId      当前页面归属的tabId
     * @param tabName    当前页面归属的tab名称
     * @param isDarkMode 是否为暗黑模式
     */
    public void init(String tabId, String tabName, boolean isDarkMode) {
        super.init(tabId, tabName, isDarkMode);
        mPid = tabId;
    }


    @Override
    protected void initView() {
        super.initView();
        FeedFragmentManager.getInstance().addFragmentRef(this);
        GVideoEventBus.get(MediaConstants.EVENT_PENDANT_CLICK, PendantModel.class)
                .observe(this, pendantModel -> {
                    if (!isUIVisible()) return;
                    FeedUtils.clickAdvert(mBinding.getRoot(), pendantModel, getGvFragmentId());
                    statPendant(pendantModel, true);
                });
        GVideoEventBus.get(MediaConstants.EVENT_PENDANT_SHOW, PendantModel.class)
                .observe(this, pendantModel -> {
                    if (!isUIVisible()) return;
                    statPendant(pendantModel, false);
                });
    }

    @Override
    protected void bindViewModels() {
        mFeedPageViewModel = bingViewModel(FeedPageViewModel.class);
        mFeedPageViewModel.updateTabId(mTabItemId);
        mFeedPageViewModel.updateTabName(tabName);
    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<MediaModel, MediaRecyclerVH> createAdapter() {
        FeedRecyclerAdapter adapter = new FeedRecyclerAdapter(getContext());
        adapter.setIsDarkMode(mIsDarkMode);
        adapter.setViewGroup(findFullScreenVideoLayout());
        if (mTabItemId != null) {
            adapter.setTabId(mTabItemId);
        }
        return adapter;
    }

    @Override
    protected void updatePlaceholderLayoutType(int type) {
        super.updatePlaceholderLayoutType(type);
        if (type == PlaceholderType.NETWORK_NOT_AVAILABLE) {
            //网络不可用时，停止播放
            MediaPlayManager.getInstance().stop(mTabItemId);
        }
    }

    /**
     * 加载更多短视频/音频数据
     *
     * @param refresh 进入详情页首次加载数据时会清空掉历史cursor
     * @return 对应的observable
     */
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return mFeedPageViewModel.loadMoreShortData(refresh);
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return mFeedPageViewModel.createShortListModel(mediaModel);
    }

    @Override
    protected void checkLoginStatus() {
        handleLoadData(true);
    }

    private StatFromModel getStat(PendantModel pendantModel) {
        String contentId = pendantModel.contentId;
        String channelId = getChannelId();
        String fromPid = "";
        String fromChannelId = "";
        StatFromModel stat = new StatFromModel(contentId, mPid, channelId, fromPid, fromChannelId);
        return stat;
    }

    private void statPendant(PendantModel pendantModel, boolean click) {
        String extendId = pendantModel.extendId;
        String extendName = pendantModel.title;
        String extendShowType = String.valueOf(pendantModel.extendType);
        String place = StatConstants.DS_KEY_PLACE_PENDANT;
        StatFromModel stat = getStat(pendantModel);
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_SHOW_TYPE, extendShowType);
        ds.addProperty(StatConstants.DS_KEY_PLACE, place);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withPid(stat.pid)
                .withEv(StatConstants.EV_ADVERT)
                .withDs(ds.toString())
                .withType(click ? StatConstants.TYPE_CLICK_C : StatConstants.TYPE_SHOW_E)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    /**
     * 通知模板当前可见
     */
    private void notifyTemplateOnVisible() {
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                int firstPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                RecyclerView.ViewHolder holder;
                // 仅通知当前可见模板
                for (int i = firstPosition; i <= lastPosition; i++) {
                    holder = mRecyclerView.findViewHolderForLayoutPosition(i);
                    if (holder instanceof MediaRecyclerVH) {
                        ((MediaRecyclerVH) holder).getFeedTemplate().onFragmentSwitchVisible();
                    }
                }
            }
        }
    }
}
