package com.hzlz.aviation.feature.account.ui.follow;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE;

import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentHomeFollowContentListBinding;
import com.hzlz.aviation.feature.account.databinding.LayoutHomeFollowContentListHeaderBinding;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.model.annotation.RelationType;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.ui.relation.RelationFragmentArgs;
import com.hzlz.aviation.feature.account.ui.ugc.adapter.UgcFollowAdapter;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.recycler.MediaRecyclerAdapter;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

import java.util.ArrayList;

public class HomeFollowContentListFragment extends MediaPageFragment<FragmentHomeFollowContentListBinding> {

    private UgcFollowAdapter followAdapter;
    private HomeFollowContentListViewModel viewModel;
    private LayoutHomeFollowContentListHeaderBinding headerBinding;
    private boolean isOnlyMe;

    public HomeFollowContentListFragment(boolean isOnlyMe) {
        this.isOnlyMe = isOnlyMe;
    }

    @Override
    public String getPid() {
        return StatPid.HOME_FOLLOW_CONTENT_LIST;
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_follow_content_list;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return viewModel.createShortListModel(mediaModel);
    }

    @Override
    protected void initView() {
        init(getGvFragmentId(), false);
        super.initView();

        headerBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_home_follow_content_list_header, null, false);
        ((MediaRecyclerAdapter) mAdapter).addHeaderView(headerBinding.getRoot());

        followAdapter = new UgcFollowAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headerBinding.followList.setLayoutManager(layoutManager);
        headerBinding.followList.setAdapter(followAdapter);

        headerBinding.all.setOnClickListener(view -> {
            Author author = new Author();
            User user = UserManager.getCurrentUser();
            String userId = user.getId();
            if (userId == null || TextUtils.isEmpty(userId)) {
                return;
            }
            author.setId(userId);
            RelationFragmentArgs args = new RelationFragmentArgs.Builder(author).setStart(RelationType.START_FOLLOW).build();
            Navigation.findNavController(view).navigate(R.id.relation_nav_graph, args.toBundle());
        });
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(HomeFollowContentListViewModel.class);
        viewModel.updateTabId(getTabId());

        if (mBinding != null) {
            mBinding.setViewModel(viewModel);
        }

        viewModel.isNeedLoadData.observe(this, o -> viewModel.loadMyFollowList());

        viewModel.myFollowListLiveData.observe(
                this,
                myFollowList -> {
                    if (viewModel == null) {
                        return;
                    }
                    if (myFollowList == null || myFollowList.isEmpty()) {
                        headerBinding.topLayout.setVisibility(GONE);
                        headerBinding.followList.setVisibility(GONE);
                        headerBinding.followListBottom.setVisibility(GONE);
                        if (!isOnlyMe) {
                            GVideoEventBus.get(NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE).post(null);
                        } else {
                            followAdapter.refreshData(new ArrayList<>());
                        }
                    } else {
                        headerBinding.topLayout.setVisibility(VISIBLE);
                        headerBinding.followList.setVisibility(VISIBLE);
                        headerBinding.followListBottom.setVisibility(VISIBLE);
                        followAdapter.refreshData(myFollowList);
                    }
                });

        viewModel.noMediaListData.observe(
                this,
                noData -> {
                    if (viewModel == null) {
                        return;
                    }
                    if (viewModel.myFollowList == null || viewModel.myFollowList.isEmpty()) {
                        GVideoEventBus.get(NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE).post(null);
                    }
                }
        );


    }

    @Override
    protected void loadData() {
        if (viewModel == null) {
            return;
        }
        super.loadData();

        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(this, o -> loadData());

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();
    }
}
