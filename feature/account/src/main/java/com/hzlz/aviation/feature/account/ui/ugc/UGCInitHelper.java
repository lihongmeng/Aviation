package com.hzlz.aviation.feature.account.ui.ugc;

import static com.hzlz.aviation.library.util.ResourcesUtils.getString;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.UGCMenuTabModel;
import com.hzlz.aviation.feature.account.model.UserAuthor;
import com.hzlz.aviation.feature.account.ui.ugc.detail.UgcContentFragment;
import com.hzlz.aviation.feature.account.ui.ugc.detail.UgcContentType;
import com.hzlz.aviation.feature.account.ui.ugc.detail.UgcMyCommentFragment;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.HashMap;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/12/27
 * desc : ugc 主页
 **/
public class UGCInitHelper {

    private ViewPager viewPager;
    private GVideoSmartTabLayout tabLayout;
    private BaseFragment currentBaseFragment;
    private FragmentAdapter adapter;
    private Context context;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<>();
    private UGCMenuTabModel tabModel;
    private UserAuthor userAuthor;
    private FragmentManager fragmentManager;

    public UGCInitHelper(ViewPager viewPager, GVideoSmartTabLayout tabLayout) {
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.context = viewPager.getContext();
    }

    public void setUserAuthor(UserAuthor author){
        this.userAuthor = author;
    }

    public void setUGCMenuTabModel(UGCMenuTabModel model){
        this.tabModel = model;
    }

    public UGCMenuTabModel getUGCMenuTabModel(){
        return tabModel;
    }

    public boolean hasInit(){
        return adapter!=null;
    }

    public void clear(){
        if (fragmentManager!=null) {
            List<Fragment> list = fragmentManager.getFragments();
            if (list != null && ! list.isEmpty()) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment f : list) {
                    ft.remove(f);
                }
                ft.commitAllowingStateLoss();
            }
        }
        fragmentManager = null;
        adapter = null;
        currentBaseFragment = null;
        tabModel = null;
        userAuthor = null;
        fragmentHashMap.clear();
    }

    /**
     * 初始化Tab
     */
    public boolean initTab(FragmentManager fragmentManager) {

        if (fragmentManager == null || userAuthor == null){
            return false;
        }
        this.fragmentManager = fragmentManager;
        FragmentPagerItems items = getFragmentPagerItems(userAuthor, tabModel);
        adapter = new FragmentAdapter(fragmentManager, items);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(items.size());
        tabLayout.setEnableChangeTextSize(true);
        tabLayout.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabPauseFragment();
                }
                currentBaseFragment = (BaseFragment) adapter.getPage(position);
                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabResumeFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return true;
    }

    /**
     * 创建菜单 FragmentPagerItems
     *
     */
    private FragmentPagerItems getFragmentPagerItems(@NonNull UserAuthor author, UGCMenuTabModel model){

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(context);

        Bundle bundle = new Bundle();
        AuthorModel authorModel = Author.fromAuthorModel(author);
        bundle.putParcelable("author", authorModel);
        bundle.putInt("type", UgcContentType.COMPOSITION);
        creator.add(getString(R.string.moment), UgcContentFragment.class, bundle);

        if (model!=null) {

            if (model.isCommentTab()) {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("author", authorModel);
                creator.add(getString(R.string.comment), UgcMyCommentFragment.class, bundle1);
            }

            if (model.isFavoriteTab()) {
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("author", authorModel);
                bundle2.putInt("type", UgcContentType.FAVORITE);
                creator.add(getString(R.string.favorite), UgcContentFragment.class, bundle2);
            }

            if (model.isQuestionTab()) {
                Bundle bundle3 = new Bundle();
                bundle3.putParcelable("author", authorModel);
                bundle3.putInt("type", UgcContentType.QUESTION);
                creator.add(getString(R.string.question), UgcContentFragment.class, bundle3);
            }

            if (model.isAnswerTab()) {
                Bundle bundle4 = new Bundle();
                bundle4.putParcelable("author", authorModel);
                bundle4.putInt("type", UgcContentType.ANSWER);
                creator.add(getString(R.string.answer), UgcContentFragment.class, bundle4);
            }
        }

        return creator.create();
    }


    public void onFragmentPause() {
        if (currentBaseFragment!=null){
            currentBaseFragment.onFragmentPause();
        }
    }

    public void onFragmentResume() {
        if (currentBaseFragment!=null){
            currentBaseFragment.onFragmentResume();
        }
    }

    public void reloadData()  {
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Fragment f = adapter.getPage(i);
            if (f instanceof BaseFragment) {
                ((BaseFragment) f).onReload(viewPager);
            }
        }
    }

    private class FragmentAdapter extends FragmentPagerItemAdapter {

        private FragmentPagerItems pagerItems;

        public FragmentAdapter(FragmentManager fm, FragmentPagerItems pages) {
            super(fm, pages);
            this.pagerItems = pages;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            String title = pagerItems.get(position).getTitle().toString();
            if (fragmentHashMap.get(title)!=null){
                fragment = fragmentHashMap.get(title);
            }else {
                fragment = super.getItem(position);
                fragmentHashMap.put(title, fragment);
            }
            if (currentBaseFragment == null) {
                currentBaseFragment = (BaseFragment) fragment;
            }
            return fragment;
        }
    }
}
