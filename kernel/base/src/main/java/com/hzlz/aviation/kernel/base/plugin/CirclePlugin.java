package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfoPid;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.Plugin;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;


public interface CirclePlugin extends Plugin {

    // 标识用户的圈子信息发生变化
    String NEED_UPDATE_CIRCLE_INFO = "need_update_circle_info";

    // 用于获取Intent内部 fragment id 的Key
    String INTENT_CIRCLE_TYPE = "intent_circle_type";

    String INTENT_CIRCLE_ID = "intent_circle_id";

    // 通知我的圈子页面，更新圈子列表信息，但是不需要更新下方内容信息
    String UPDATE_MY_CIRCLE_LIST = "update_my_circle_list";

    // 通知发现圈子页，需要刷新数据
    String UPDATE_FIND_CIRCLE_LIST = "update_find_circle_list";

    // 通知修改信息的内容
    String MODIFY_CIRCLE_INFO = "modify_circle_info";

    // 标识网络请求报错，错误原因是用户没有加入圈子
    String HAS_NOT_JOIN_CIRCLE = "has_not_join_circle";

    String HAS_JOIN_CIRCLE = "has_join_circle";

    // 通知圈子热聊Fragment，刷新最新数据
    String CIRCLE_HOTTEST_FRAGMENT_UPDATE = "circle_hottest_fragment_update";

    /**
     * 添加抽屉目的地
     *
     * @param fragment Fragment
     */
    void addDestinations(@NonNull BaseFragment fragment);

    @NonNull
    public BaseFragment getCircleFragment();

    void navigationToSearchTopic(View view, Circle circle);

    void startSearchTopicWithActivity(Context context, Circle circle);

    void startSelectCircleWithActivity(Context context);

    void startTopicDetailWithActivity(Context context, Circle circle, TopicDetail topicDetail);

    void navigationToTopicDetail(View view, Circle circle, TopicDetail topicDetail);

    void startCircleDetailWithActivity(Context context, Circle circle, Bundle bundle);

    void startFXAActivity(Context context, String id);

    void navigationToCircleDetail(View view, Circle circle);

    void navigationToCircleDetail(View view, Bundle bundle);

    void navigationQAAnswerListFragment(View view, Circle circle, String formPid);

    void startQAAnswerListActivity(View view, Circle circle, String formPid);

    void startModifyCircleTextInfoWithActivity(Context context, int modifyType, String info);

    void navigationToModifyCircleTextInfo(View view, int modifyType, String info);

    void dealGVideoAPIExceptionError(Context context, GVideoAPIException gVideoAPIException);

    void navigationToCircleTab(@NonNull View view, int index);

    void joinCircle(long groupId, String reason, BaseResponseObserver<Object> baseResponseObserver);

    /**
     * 返回加入社区结果
     * 此方法已处理埋点事件，报错提示
     *
     * @param circle        社区信息（用于请求加入社区和埋点上报）
     * @param fromPid       来源页面
     * @param showTipDialog 加入社区后是否显示提示弹窗
     */
    Observable<Boolean> joinCircle(Circle circle, String fromPid, boolean showTipDialog);

    void startCircleDetail(View view, GroupInfoPid groupInfoPid);

    void startTopicDetail(View view, GroupInfoPid groupInfoPid);

    /**
     * 进入问答广场
     */
    void startQAGroupActivity(View view, Bundle bundle);

    /**
     * 获取问答老师组件view，目前在界面详情展示
     *
     * @param context     上下文
     * @param circle      圈子信息
     * @param gather      组件信息
     * @param authorModel 老师信息
     * @param pid         当前页面pid
     */
    View getQAAnswerMessageView(Context context, Circle circle, CircleModule gather, AuthorModel authorModel, String pid);

    /**
     * 显示加入社区弹窗
     *
     * @param context    Context
     * @param groupId    社区id
     * @param groupName  社区名称
     * @param tenantId   所属MCN
     * @param tenantName 所属MCN名称
     * @param labels     标签
     */
    void showJoinCircleDialog(
            Context context,
            long groupId,
            String groupName,
            Long tenantId,
            String tenantName,
            List<String> labels,
            String sourceFragmentPid
    );

    void getRecommendCommunity(
            int page,
            int pageSize,
            Long firstGroupId,
            BaseResponseObserver<ListWithPage<Circle>> baseResponseObserver
    );

}
