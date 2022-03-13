package com.jxntv.account.ui.notification;


import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentMessageNotificationBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.ioc.PluginManager;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * 通知
 */
public final class MessageNotificationFragment extends BaseFragment<FragmentMessageNotificationBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_notification;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.setStatusBarIconColor(this,true);

        PluginManager.get(ChatIMPlugin.class).setOfflineMessageDoBackground(false);

        mBinding.rootLayout.setPadding(0, WidgetUtils.getStatusBarHeight(),0,0);
        mBinding.tabLayout.resetCustomTabView();

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());

        creator.add("通知",SystemMessageFragment.class);
        creator.add("私信", IMChatNotificationFragment.class);
        FragmentPagerItemAdapter mPagerAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), creator.create());
        mBinding.viewPage.setAdapter(mPagerAdapter);
        mBinding.viewPage.setCurrentItem(0);

        mBinding.tabLayout.setViewPager(mBinding.viewPage);

        mBinding.back.setOnClickListener(view -> onLeftBackPressed(mBinding.back));
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {
        setIMMessageCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        PluginManager.get(ChatIMPlugin.class).setOfflineMessageDoBackground(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        PluginManager.get(ChatIMPlugin.class).setOfflineMessageDoBackground(false);
        setIMMessageCount();

    }

    private void setIMMessageCount(){
        if (mBinding!=null){
            PluginManager.get(ChatIMPlugin.class).getUnreadMessageCount(new ChatIMPlugin.TotalUnreadMessageCountListener() {
                @Override
                public void onSuccess(long count) {
                    mBinding.tabLayout.setUpdateUnread(1, (int) count);
                }

                @Override
                public void onError() {

                }
            });
        }
    }

}

