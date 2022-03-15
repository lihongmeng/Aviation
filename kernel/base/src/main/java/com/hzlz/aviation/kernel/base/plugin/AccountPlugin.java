package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.library.ioc.Plugin;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

/**
 * 账户模块接口
 *
 * @since 2020-01-13 14:06
 */
public interface AccountPlugin extends Plugin {
    //<editor-fold desc="常量">
    String EVENT_ACCOUNT_FREEZE = "EVENT_ACCOUNT_FREEZE";
    String EVENT_AVATAR_UPDATE = "EVENT_AVATAR_UPDATE";

    // 首页展示推荐关注列表
    String EVENT_SHOW_FOLLOW_LIST = "EVENT_SHOW_FOLLOW_LIST";
    // 首页展示我的关注列表，以及关注对象对应的信息流
    String EVENT_SHOW_FOLLOW_CONTENT_LIST = "EVENT_SHOW_FOLLOW_CONTENT_LIST";

    String EVENT_NEW_MOMENT = "new_moment";
    String EVENT_UNREAD_NOTIFICATION = "unread_notification";
    String EVENT_REFRESH_DATA = "event_refresh_data";

    String EVENT_UPDATE_UNREAD_MESSAGE_COUNT = "update_unread_message_count";

    // 选择头像时的默认头像列表
    String DEFAULT_AVATAR_LIST = "default_avatar_list";

    // 设置昵称时的随机数
    String RANDOM_INDEX = "random_index";

    /**
     * 账户认证状态
     */
    int VERIFICATION_STATUS_NO_VERIFY = -2;
    int VERIFICATION_STATUS_REJECT = -1;
    int VERIFICATION_STATUS_VERIFYING = 0;
    int VERIFICATION_STATUS_VERIFIED = 1;

    //</editor-fold>

    //<editor-fold desc="页面跳转">

    /**
     * 跳转至登录界面，分享弹框等非Fragment页面使用
     *
     * @param context Context
     */
    void startLoginActivity(Context context);

    /**
     * 跳转至登录界面，分享弹框等非Fragment页面使用
     *
     * @param context Context
     */
    void startLoginActivity(Context context, Bundle bundle);

    /**
     * 启动昵称修改界面
     *
     * @param context Context
     */
    void startNickNameSetActivity(Context context);

    /**
     * 跳转 PGC 页面
     *
     * @param view   被点击的 View
     * @param author 作者
     */
    void navigateToPgc(@NonNull View view, @NonNull AuthorModel author);

    void startPgcActivity(@NonNull View view, @NonNull AuthorModel author);

    /**
     * 个人头像预览
     *
     * @param view   被点击的 view
     * @param author 作者
     */
    void startAvatarPreviewActivity(@NonNull View view, @NonNull AuthorModel author);

    /**
     * 跳转 圈子 页面
     *
     * @param view   被点击的 View
     * @param author 作者
     */
    void startCircleFragment(@NonNull View view, @NonNull AuthorModel author);

    /**
     * 跳转 账号与安全 界面
     *
     * @param view 被点击的 View
     */
    void startAccountSecurityFragment(@NonNull View view);
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 获取我的 Fragment
     *
     * @return 我的 Fragment
     */
    @NonNull
    BaseFragment getMeFragment();

    /**
     * 获取动态Fragment
     *
     * @return
     */
    BaseFragment getMomentFragment();

    /**
     * 获取首页关注Fragment
     */
    BaseFragment getHomeFollowFragment();

    /**
     * 获取侧边栏 Fragment
     *
     * @return 侧边栏 Fragment
     */

    @NonNull
    Fragment getDrawerFragment();

    /**
     * 添加抽屉目的地
     *
     * @param fragment Fragment
     */
    void addDestinations(@NonNull BaseFragment fragment);

    /**
     * 获取关注仓库
     *
     * @return 关注仓库
     */
    @NonNull
    IFollowRepository getFollowRepository();

    /**
     * 获取文件仓库
     *
     * @return 关注仓库
     */
    @NonNull
    IFileRepository getFileRepository();

    /**
     * 获取 Token
     *
     * @return Token
     */
    @Nullable
    String getToken();

    /**
     * 获取登陆用户uid
     *
     * @return uid
     */
    String getUserId();

    /**
     * 获取登陆用户昵称
     *
     * @return nickname
     */
    String getNickName();

    String getProvince();

    String getCity();

    String getPhoneNumber();

    /**
     * 获取加入的圈子id
     */
    List<String> getJoinGroup();

    /**
     * 获取登陆用户头像
     *
     * @return uid
     */
    String getUserAvatar();

    /**
     * 获取用户展示且审核通过的用户头像
     */
    String getRealUserAvatar();

    /**
     * 获取实名认证状态
     *
     * @return 实名认证状态
     */
    int getAuditStatus();

    int getNickNameAuditStatus();

    //</editor-fold>

    /**
     * 是否平台运营人员
     */
    boolean getIsPlatformUser();

    /**
     * 退出登录
     */
    void logout();


    boolean hasLoggedIn();

    void preHeaderImage(Context context);

    Bitmap getHeaderImage();

    void onHomeRelease();

    void startNotificationFragment(View view, String pid);

    /**
     * 打开通知详情
     *
     * @param msgType 消息类型
     * @param title   界面title
     */
    void startNotificationDetailActivity(
            Context context,
            int msgType,
            String title,
            String sourceFragmentPid
    );

    boolean isMe(String userId);

    /**
     * 获取某用户与自己的关注关系
     *
     * @param userId 用户id
     */
    Observable<Map<String, Boolean>> getFollowRelationship(String userId);


    /**
     * 显示举报弹窗
     *
     * @param context           内容上下文
     * @param id                资源id、评论id、用户id 、群id
     * @param resType           0 资源， 1 评论， 2 回复 , 3 用户 , 4 私信群
     * @param sourceFragmentPid 调用此方法的界面pid
     */
    void showReportDialog(Context context, String id, int resType, String sourceFragmentPid);

    void requestUserIsJoinedCommunity(
            String userId,
            long communityId,
            BaseResponseObserver<Boolean> baseResponseObserver
    );

    void initOneKeyLoginSDK();

}
