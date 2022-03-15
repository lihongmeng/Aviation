package com.hzlz.aviation.feature.account.ui.notification;


import android.view.View;

import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentMessageImChatBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
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

