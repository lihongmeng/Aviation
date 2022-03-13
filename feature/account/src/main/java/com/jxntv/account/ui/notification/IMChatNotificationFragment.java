package com.jxntv.account.ui.notification;


import android.view.View;

import androidx.lifecycle.Observer;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentMessageImChatBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.AsyncUtils;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * 通知
 */
public final class IMChatNotificationFragment extends BaseFragment<FragmentMessageImChatBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_im_chat;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.setStatusBarIconColor(this, true);

        PluginManager.get(ChatIMPlugin.class).setOfflineMessageDoBackground(false);

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());
        creator.add("私信", PluginManager.get(ChatIMPlugin.class).getConversationFragmentClass());
        FragmentPagerItemAdapter mPagerAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(),
                        creator.create());
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.viewPager.setCurrentItem(0);

        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class).observe(this, (Observer<Integer>) integer -> {
            setView();
        });
        AsyncUtils.runOnUIThread(this::setView,1000);
    }

    private void setView() {
        if (getActivity()==null || getActivity().isFinishing()){
            return;
        }
        boolean hasMessage = PluginManager.get(ChatIMPlugin.class).getHasUnreadMessage() ||
                                             PluginManager.get(ChatIMPlugin.class).getHasConversation();
        mBinding.viewPager.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        mBinding.placeHolder.setVisibility(hasMessage ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }

}

