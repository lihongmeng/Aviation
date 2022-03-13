package com.jxntv.circle.fragment.qa;

import static com.jxntv.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.jxntv.base.plugin.CirclePlugin.HAS_JOIN_CIRCLE;
import static com.jxntv.base.plugin.CirclePlugin.INTENT_CIRCLE_TYPE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.Constant;
import com.jxntv.base.dialog.DefaultEnsureDialog;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.circle.R;
import com.jxntv.circle.activity.CircleActivity;
import com.jxntv.circle.databinding.FragmentQaGroupListBinding;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * @author huangwei
 * date : 2021/9/6
 * desc : 问答广场列表
 **/
public class QAGroupListFragment extends MediaPageFragment<FragmentQaGroupListBinding> {

    private Circle circle;

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        circle = bundle.getParcelable(CIRCLE);
    }

    @Override
    protected void initView() {
        super.initView();
        init(getGvFragmentId(), false);
        setToolbarTitle("问答广场");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qa_group_list;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected void bindViewModels() {
        super.bindViewModels();
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        mViewModel = bingViewModel(QAGroupViewModel.class);
        ((QAGroupViewModel) mViewModel).setGroupId(String.valueOf(circle.groupId));
        mViewModel.updateTabId(getPid());

        mBinding.askQuestion.setOnClickListener(view -> {
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (!accountPlugin.hasLoggedIn()) {
                accountPlugin.startLoginActivity(getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPid()),
                        getString(R.string.publish)
                );
                return;
            }
            if (!circle.isJoin()) {
                PluginManager.get(CirclePlugin.class).joinCircle(circle,getPid(),true).subscribe(a->{
                    if (a){
                        circle.setJoin(true);
                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                                        .post(new CircleJoinStatus(circle.groupId, true));
                        clickPublishReal(mBinding.askQuestion);
                    }
                });
            } else {
                clickPublishReal(mBinding.askQuestion);
            }
        });

        GVideoEventBus.get(HAS_JOIN_CIRCLE, Long.class).observe(this, s -> {
            if (s == circle.groupId) {
                circle.setJoin(true);
            }
        });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(this, t -> {
            if (UserManager.getCurrentUser().getJoinGroup() != null) {
                for (String id : UserManager.getCurrentUser().getJoinGroup()) {
                    if (TextUtils.equals(id, circle.groupId + "")) {
                        circle.setJoin(true);
                        break;
                    }
                }
            }
        });

        GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH).observe(
                this,
                startPublishFrom -> {
                    if (mViewModel == null) {
                        return;
                    }
                    mViewModel.loadRefreshData();
                }
        );

    }

    public void clickPublishReal(View view) {
         PluginManager.get(CirclePlugin.class).startQAAnswerListActivity(view, circle, getPid());
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return null;
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        finishActivity();
    }

    @Override
    public String getPid() {
        return StatPid.GROUP_QA;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @Override
    protected Long getTenantId() {
        if (circle == null) {
            return null;
        }
        return circle.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (circle == null) {
            return "";
        }
        return circle.tenantName;
    }

}
