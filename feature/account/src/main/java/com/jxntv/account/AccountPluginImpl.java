package com.jxntv.account;

import static com.jxntv.base.Constant.AUTHOR_TYPE;
import static com.jxntv.base.Constant.EXTRA_AUTHOR_ID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.jxntv.account.dialog.ReportDialog;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.User;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.account.repository.FileRepository;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.account.ui.AccountModuleActivity;
import com.jxntv.account.ui.drawer.DrawerFragment;
import com.jxntv.account.ui.follow.HomeFollowFragment;
import com.jxntv.account.ui.me.MeFragment;
import com.jxntv.account.ui.moment.MomentFragment;
import com.jxntv.account.utils.HeaderUtils;
import com.jxntv.account.utils.oneKeyLogin.OneKeyLoginUtils;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.IFileRepository;
import com.jxntv.base.plugin.IFollowRepository;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


/**
 * AccountPlugin 实现类
 *
 * @since 2020-01-13 15:20
 */
public final class AccountPluginImpl implements AccountPlugin {
    //<editor-fold desc="方法实现">

    @Override
    public void startLoginActivity(Context context) {
        startLoginActivity(context, new Bundle());
    }

    @Override
    public void startLoginActivity(Context context, Bundle bundle) {
        OneKeyLoginUtils.getInstance().dispatchLogin(context,bundle);
    }

    @Override
    public void startNickNameSetActivity(Context context) {
        Intent intent = new Intent(context, AccountModuleActivity.class);
        intent.putExtra(Constant.EXTRA_FRAGMENT_ID, R.id.nickNameSetFragment);
        intent.putExtra(Constant.EXTRA_NAVIGATION, R.navigation.phone_nav_graph);
        context.startActivity(intent);
    }

    @Override
    public void navigateToPgc(@NonNull View view, @NonNull AuthorModel author) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_AUTHOR_ID, author.getId());
        arguments.putInt(AUTHOR_TYPE, author.getType());
        NavController controller = Navigation.findNavController(view);
        NavDestination current = controller.getCurrentDestination();
        int type = author.getType();
        if (type == AuthorType.UGC) {
            if (current != null && current.getParent() != null
                    && current.getParent().getId() == R.id.ugc_nav_graph) {
                return;
            }
            controller.navigate(R.id.ugc_nav_graph, arguments);
        } else { //PGC && PPTV
            if (current != null && current.getParent() != null
                    && current.getParent().getId() == R.id.pgc_nav_graph) {
                return;
            }
            controller.navigate(R.id.pgc_nav_graph, arguments);
        }

    }

    @Override
    public void startPgcActivity(View view, @NonNull AuthorModel author) {

        Intent intent = new Intent(view.getContext(), AccountModuleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_AUTHOR_ID, author.getId());
        arguments.putInt(Constant.EXTRA_AUTHOR_TYPE, author.getType());

        int type = author.getType();
        if (type == AuthorType.UGC) {
            intent.putExtra(Constant.EXTRA_FRAGMENT_ID, R.id.ugcFragment);
        } else {
            intent.putExtra(Constant.EXTRA_FRAGMENT_ID, R.id.pgcFragment);
        }
        intent.putExtra(Constant.EXTRA_NAVIGATION, R.navigation.ugc_nav_graph);
        intent.putExtras(arguments);
        view.getContext().startActivity(intent);
    }

    @Override
    public void startAvatarPreviewActivity(@NonNull View view, @NonNull AuthorModel author) {
        Intent intent = new Intent(view.getContext(), AccountModuleActivity.class);
        Bundle arguments = new Bundle();
        arguments.putParcelable("author", author);
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, rect.left,
                rect.top, view.getWidth(), view.getHeight());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fragmentId", R.id.previewAvatarFragment);
        intent.putExtra("navigation", R.navigation.profile_nav_graph);
        intent.putExtras(arguments);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());

    }

    @Override
    public void startCircleFragment(@NonNull View view, @NonNull AuthorModel author) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_AUTHOR_ID, author.getId());
        arguments.putInt(AUTHOR_TYPE, author.getType());
        NavController controller = Navigation.findNavController(view);
        controller.navigate(R.id.ugc_nav_graph, arguments);
    }

    @Override
    public void startAccountSecurityFragment(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.account_security_nav_graph);
    }

    @NonNull
    @Override
    public BaseFragment getMeFragment() {
        return new MeFragment();
    }

    @Override
    public BaseFragment getMomentFragment() {
        return new MomentFragment();
    }

    @Override
    public BaseFragment getHomeFollowFragment() {
        return new HomeFollowFragment();
    }

    @NonNull
    @Override
    public Fragment getDrawerFragment() {
        return new DrawerFragment();
    }

    @Override
    public void addDestinations(@NonNull BaseFragment fragment) {
        // 侧边栏相关 Destination
        // 添加手机号界面
        fragment.addDestination(R.navigation.phone_nav_graph);
        // 个人中心界面
        fragment.addDestination(R.navigation.profile_nav_graph);
        // 裁剪
        fragment.addDestination(R.navigation.crop_nav_graph);
        // 消息与活动通知界面
        fragment.addDestination(R.navigation.message_notification_nav_graph);
        // 账号安全界面
        fragment.addDestination(R.navigation.account_security_nav_graph);
        // 通用设置界面
        fragment.addDestination(R.navigation.setting_nav_graph);
        // 关于今视频界面
        fragment.addDestination(R.navigation.about_nav_graph);
        // PGC 相关 Destination
        fragment.addDestination(R.navigation.pgc_nav_graph);
        // UGC 相关 Destination
        fragment.addDestination(R.navigation.ugc_nav_graph);
        // 收藏详情相关 Destination
        fragment.addDestination(R.navigation.favorite_detail_nav_graph);
        // 好友关系 Destination
        fragment.addDestination(R.navigation.relation_nav_graph);
    }

    @NonNull
    @Override
    public IFollowRepository getFollowRepository() {
        return new InteractionRepository();
    }

    @NonNull
    @Override
    public IFileRepository getFileRepository() {
        return new FileRepository();
    }

    @Nullable
    @Override
    public String getToken() {
        return UserManager.getToken();
    }

    @Override
    public String getUserId() {
        return UserManager.getCurrentUser().getId();
    }

    @Override
    public String getNickName() {
        return UserManager.getCurrentUser().getNickname();
    }

    @Override
    public String getProvince() {
        return UserManager.getCurrentUser().getProvince();
    }

    @Override
    public String getCity() {
        return UserManager.getCurrentUser().getCity();
    }

    @Override
    public String getPhoneNumber() {
        return UserManager.getCurrentUser().getPhoneNumber();
    }

    @Override
    public List<String> getJoinGroup() {
        return UserManager.getCurrentUser().getJoinGroup();
    }

    @Override
    public String getUserAvatar() {
        return UserManager.getCurrentUser().getAvatarUrl();
    }

    @Override
    public String getRealUserAvatar() {
        return UserManager.getCurrentUser().getRealUserAvatar();
    }

    @Override
    public boolean getIsPlatformUser() {
        return UserManager.getCurrentUser().getPlatformUser() == 1;
    }

    @Override
    public int getAuditStatus() {
        boolean hasIdentityVerified = UserManager.getCurrentUser().hasIdentityVerified();
        if (hasIdentityVerified) {
            return VERIFICATION_STATUS_VERIFIED;
        }
        if (UserManager.getCurrentUser().getNewIdentity() == null) {
            return VERIFICATION_STATUS_NO_VERIFY;
        }
        return UserManager.getCurrentUser().getNewIdentity().getAuditStatus();
    }

    @Override
    public int getNickNameAuditStatus() {
        User.NewValue newIdentity = UserManager.getCurrentUser().getNewNickname();
        return newIdentity == null ? VERIFICATION_STATUS_NO_VERIFY : newIdentity.getAuditStatus();
    }

    @Override
    public void logout() {

        new UserRepository().logout().subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
            @Override
            protected void onRequestData(@NonNull Object result) {
                // 通知登出
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).post(null);
            }
        });
    }

    @Override
    public boolean hasLoggedIn() {
        return UserManager.hasLoggedIn();
    }

    @Override
    public void preHeaderImage(Context context) {
        HeaderUtils.getInstance().preHeaderImage(context);
    }

    @Override
    public Bitmap getHeaderImage() {
        return HeaderUtils.getInstance().headerBitmap;
    }

    @Override
    public void onHomeRelease() {
        HeaderUtils.getInstance().headerBitmap = null;
    }

    @Override
    public void startNotificationFragment(View view, String pid) {
        if (hasLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.message_notification_nav_graph);
        } else {
            startLoginActivity(view.getContext());
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(pid),
                    ResourcesUtils.getString(R.string.open_message_center)
            );
        }
    }

    @Override
    public void startNotificationDetailActivity(
            Context context,
            int msgType,
            String title,
            String sourceFragmentPid
    ) {
        if (!hasLoggedIn()) {
            startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(sourceFragmentPid),
                    ResourcesUtils.getString(R.string.see_notification_detail)
            );
            return;
        }
        Intent intent = new Intent(context, AccountModuleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle arguments = new Bundle();
        arguments.putInt("msgType", msgType);
        arguments.putString("title", title);
        intent.putExtras(arguments);
        intent.putExtra(Constant.EXTRA_FRAGMENT_ID, R.id.messageNotificationDetailFragment);
        intent.putExtra(Constant.EXTRA_NAVIGATION, R.navigation.message_notification_nav_graph);
        context.startActivity(intent);
    }


    @Override
    public boolean isMe(String userId) {
        return !TextUtils.isEmpty(userId) && userId.equals(UserManager.getCurrentUser().getId());
    }

    @Override
    public Observable<Map<String, Boolean>> getFollowRelationship(String userId) {
        Author author = new Author();
        author.setId(userId);
        author.setType(AuthorType.UGC);
        return new AuthorRepository().getUserAuthorIsFollowMe(author);
    }

    @Override
    public void showReportDialog(
            Context context,
            String id,
            int contentType,
            String sourceFragmentPid
    ) {
        if (hasLoggedIn()) {
            new ReportDialog(context, id, contentType).show();
        } else {
            startLoginActivity(context);
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(sourceFragmentPid),
                    ResourcesUtils.getString(R.string.report)
            );
        }
    }

    @Override
    public void requestUserIsJoinedCommunity(String userId, long communityId, BaseResponseObserver<Boolean> baseResponseObserver) {
        new UserRepository().checkUserJoinCommunity(userId, communityId).subscribe(baseResponseObserver);

    }

    @Override
    public void initOneKeyLoginSDK() {
        OneKeyLoginUtils.getInstance().init();
    }

    //</editor-fold>
}
