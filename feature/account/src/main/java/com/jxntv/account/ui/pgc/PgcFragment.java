package com.jxntv.account.ui.pgc;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentPgcBinding;
import com.jxntv.account.model.Author;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;


/**
 * PGC 页面
 *
 * @since 2020-02-17 15:23
 */
public final class PgcFragment extends BaseFragment<FragmentPgcBinding> {
    //<editor-fold desc="属性">
    private PgcViewModel mPgcViewModel;

    private boolean statusBarBlack = false;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean enableImmersive() {
        return false;
    }

    @Override
    protected boolean isUIVisible() {
        return isVisible();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pgc;
    }

    @Override
    public String getPid() {
        return StatPid.PGC;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.enterImmersiveFullTransparent(getActivity());
        int top = WidgetUtils.getStatusBarHeight();
        mBinding.toolbar.setPadding(0,top,0,0);
        mBinding.appBarLayout.layoutAuthor.setPadding(0,top/2,0,0);

        mBinding.openToolbar.setPadding(0,top,0,0);

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);

        mBinding.share.setVisibility(PluginManager.get(SharePlugin.class).isCanShare()? View.VISIBLE:View.GONE);
    }

    @Override
    protected void loadData() {
        mPgcViewModel.loadData();
    }

    @Override
    protected void bindViewModels() {

        String authorId = getArguments() != null ? getArguments().getString("authorId") : "";
        int authorType = getArguments() != null ? getArguments().getInt("authorType") : AuthorType.PGC;
        mPgcViewModel = bingViewModel(PgcViewModel.class);
        mPgcViewModel.setAuthorId(authorId, authorType);

        mBinding.setViewModel(mPgcViewModel);
        mBinding.appBarLayout.setViewModel(mPgcViewModel);
        mPgcViewModel.getAuthorLiveData().observe(this, author -> {
            if (author != null) {
                mBinding.setAuthorObservable(author.getObservable());
                mBinding.appBarLayout.setAuthorObservable(author.getObservable());
                initViewPager(author);
            }
        });

        mBinding.appBarLayout.appBar.addOnOffsetChangedListener((appBarLayout, i) -> {
            Rect rect = new Rect();
            boolean isShow = mBinding.appBarLayout.appbarContent.getGlobalVisibleRect(rect);
            if (statusBarBlack != isShow) {
                statusBarBlack = isShow;
                ImmersiveUtils.setStatusBarIconColor(this, !statusBarBlack);
            }
        });


        mBinding.leftBack.setOnClickListener(view -> getActivity().finish());

    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
        if (currentBaseFragment!=null) {
            currentBaseFragment.onFragmentResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
        if (currentBaseFragment!=null){
            currentBaseFragment.onFragmentPause();
        }
    }

    private PgcContentFragment currentBaseFragment;

    private void initViewPager(Author author) {

        List<Integer> tagName = new ArrayList<>();
        List<Integer> tagType = new ArrayList<>();

        tagName.add(R.string.pgc_video);
        tagType.add(1);

        tagName.add(R.string.pgc_news);
        tagType.add(4);

//        tagName.add(R.string.pgc_special);
//        tagType.add(5);

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(requireContext());
        //0-全部、1-音视频、2-图文、3-语音、4-新闻、5-专题
        for (int i = 0; i < tagName.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("authorId", author.getId());
            bundle.putInt("authorType", author.getType());
            bundle.putInt("tagType", tagType.get(i));
            creator.add(tagName.get(i), PgcContentFragment.class, bundle);
        }

        FragmentPagerItemAdapter adapter = new NewsAdapter(getChildFragmentManager(), creator.create());

        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(adapter.getCount());
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabPauseFragment();
                }
                currentBaseFragment = (PgcContentFragment) adapter.getPage(position);
                currentBaseFragment.onTabResumeFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mBinding.appBarLayout.tabLayout.setEnableChangeTextSize(false);
        mBinding.appBarLayout.tabLayout.setViewPager(mBinding.viewPager);
        mBinding.viewPager.setCurrentItem(0);
    }


    public class NewsAdapter extends FragmentPagerItemAdapter {

        public NewsAdapter(FragmentManager fm, FragmentPagerItems pages) {
            super(fm, pages);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = super.getItem(position);
            if (currentBaseFragment == null && fragment instanceof PgcContentFragment) {
                currentBaseFragment = (PgcContentFragment) fragment;
            }
            return fragment;
        }
    }

    //</editor-fold>

}
