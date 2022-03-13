package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.ioc.PluginManager;
import com.tencent.qcloud.tuikit.tuichat.presenter.C2CChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;
import com.tencent.qcloud.tuicore.TUICore;

import java.util.Map;

import io.reactivex.functions.Consumer;

public class TUIC2CChatFragment extends TUIBaseChatFragment {
    private static final String TAG = TUIC2CChatFragment.class.getSimpleName();

    private ChatInfo chatInfo;
    private C2CChatPresenter presenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TUIChatLog.i(TAG, "oncreate view " + this);

        baseView = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return baseView;
        }
        chatInfo = (ChatInfo) bundle.getSerializable(TUIChatConstants.CHAT_INFO);
        if (chatInfo == null) {
            return baseView;
        }

        initView();

        return baseView;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();

        titleBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AuthorModel authorModel = new AuthorModel();
//                authorModel.setId(chatInfo.getId());
//                authorModel.setType(AuthorType.UGC);
//                PluginManager.get(AccountPlugin.class).startPgcActivity(titleBar,authorModel);
            }
        });

        chatView.setPresenter(presenter);
        presenter.setChatInfo(chatInfo);
        chatView.setChatInfo(chatInfo);

        PluginManager.get(AccountPlugin.class).getFollowRelationship(chatInfo.getId())
                .subscribe(map -> presenter.setRelationShip(map));

    }

    public void setPresenter(C2CChatPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public C2CChatPresenter getPresenter() {
        return presenter;
    }

    @Override
    public ChatInfo getChatInfo() {
        return chatInfo;
    }
}
