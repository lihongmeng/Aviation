package com.hzlz.aviation.feature.community.fragment.qa;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.plugin.CirclePlugin.HAS_JOIN_CIRCLE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentQaGroupListBinding;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

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
