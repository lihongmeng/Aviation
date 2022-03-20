package com.hzlz.aviation.feature.community;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TOPIC_DETAIL;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_CIRCLE_FRAGMENT_INDEX;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_GROUP_ID;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_IS_START_WITH_ACTIVITY;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_MODIFY_INFO;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_MODIFY_TYPE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.community.activity.CircleActivity;
import com.hzlz.aviation.feature.community.activity.CircleDetailActivity;
import com.hzlz.aviation.feature.community.databinding.LayoutPrgramQaAnswerMessageBinding;
import com.hzlz.aviation.feature.community.fragment.HomeCommunityFragment;
import com.hzlz.aviation.feature.community.fragment.qa.QAAnswerMessageViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureDialog;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfoPid;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class CirclePluginImpl implements CirclePlugin {

    @Override
    public void addDestinations(@NonNull BaseFragment fragment) {
        // 侧边栏相关 Destination
        fragment.addDestination(R.navigation.circle_nav_graph);
        fragment.addDestination(R.navigation.circle_detail_graph);
    }

    @NonNull
    @Override
    public BaseFragment getHomeCommunityFragment() {
        return new HomeCommunityFragment();
    }

    // ===================================== 启动话题详情页 ===========================================

    @Override
    public void startTopicDetailWithActivity(Context context, Circle circle, TopicDetail topicDetail) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, CircleActivity.class);
        intent.putExtra(CIRCLE, circle);
        intent.putExtra(TOPIC_DETAIL, topicDetail);
        intent.putExtra(EXTRA_IS_START_WITH_ACTIVITY, true);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.topicDetailFragment);
        context.startActivity(intent);
    }

    @Override
    public void navigationToTopicDetail(View view, Circle circle, TopicDetail topicDetail) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(CIRCLE, circle);
        arguments.putParcelable(TOPIC_DETAIL, topicDetail);
        arguments.putBoolean(EXTRA_IS_START_WITH_ACTIVITY, false);
        Navigation.findNavController(view).navigate(R.id.topicDetailFragment, arguments);
    }

    public void startTopicDetail(View view, GroupInfoPid groupInfoPid) {
        if (TextUtils.isEmpty(groupInfoPid.pid) || groupInfoPid.groupInfo == null) {
            return;
        }
        switch (groupInfoPid.pid) {
            case StatPid.CIRCLE_DETAIL:
                PluginManager.get(CirclePlugin.class).navigationToTopicDetail(
                        view,
                        new Circle(groupInfoPid.groupInfo),
                        new TopicDetail(groupInfoPid.groupInfo)
                );
                break;
            case StatPid.CIRCLE_MINE:
            default:
                PluginManager.get(CirclePlugin.class).startTopicDetailWithActivity(
                        view.getContext(),
                        new Circle(groupInfoPid.groupInfo),
                        new TopicDetail(groupInfoPid.groupInfo)
                );
        }
    }

    @Override
    public void startQAGroupActivity(View view, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        Intent intent = new Intent(view.getContext(), CircleActivity.class);
        bundle.putInt(INTENT_CIRCLE_TYPE, R.id.qaGroupListFragment);
        intent.putExtras(bundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(intent);
    }

    @Override
    public View getQAAnswerMessageView(Context context, Circle circle, CircleModule gather, AuthorModel authorModel, String pid) {
        LayoutInflater inflaters = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflaters.inflate(R.layout.layout_prgram_qa_answer_message, null);
        LayoutPrgramQaAnswerMessageBinding binding = DataBindingUtil.bind(view);
        QAAnswerMessageViewModel viewModel = new QAAnswerMessageViewModel(circle, pid);
        binding.content.root.getHelper().setBackgroundColorNormal(Color.TRANSPARENT);
        binding.setVariable(BR.viewModel, viewModel);
        binding.setVariable(BR.circle,gather);
        binding.content.setAuthorModel(authorModel);
        binding.content.setViewModel(viewModel);
        binding.content.setAuthorObservable(authorModel.getObservable());
        binding.content.line.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ececec));
        return view;
    }

    @Override
    public void showJoinCircleDialog(Context context, long groupId, String groupName, Long tenantId, String tenantName, List<String> labels, String sourceFragmentPid) {

        DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(context);
        dialog.init(v -> dialog.dismiss(), v -> joinCircle(groupId, "", new BaseResponseObserver<Object>() {
                            @Override
                            protected void onRequestData(Object o) {
                                Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                                GVideoEventBus.get(NEED_UPDATE_CIRCLE_INFO).post(null);
                        GVideoEventBus.get(HAS_JOIN_CIRCLE).post(groupId);
                        GVideoSensorDataManager.getInstance().followCommunity(
                                groupId,
                                groupName,
                                tenantId,
                                tenantName,
                                labels,
                                StatPid.getPageName(StaticParams.currentStatPid),
                                null
                        );
                        dialog.dismiss();
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        Toast.makeText(context, "加入失败", Toast.LENGTH_SHORT).show();
                        GVideoSensorDataManager.getInstance().followCommunity(
                                groupId,
                                groupName,
                                tenantId,
                                tenantName,
                                labels,
                                StatPid.getPageName(StaticParams.currentStatPid),
                                throwable.getMessage()
                        );
                        dialog.dismiss();
                    }
                }),
                "",
                "请加入" + groupName + "后再发言",
                "取消",
                "加入"
        );
        dialog.show();

    }

    @Override
    public void getRecommendCommunity(
            int page,
            int pageSize,
            Long firstGroupId,
            BaseResponseObserver<ListWithPage<Circle>> baseResponseObserver) {
        new CircleRepository()
                .getRecommendCommunity(page, pageSize, firstGroupId)
                .subscribe(baseResponseObserver);
    }

    // =============================================================================================

    // ===================================== 启动圈子详情页 ===========================================

    public void startCircleDetail(View view, GroupInfoPid groupInfoPid) {
        if (TextUtils.isEmpty(groupInfoPid.pid) || groupInfoPid.groupInfo == null) {
            return;
        }
        switch (groupInfoPid.pid) {
            case StatPid.CIRCLE_DETAIL:
                PluginManager.get(CirclePlugin.class).navigationToCircleDetail(
                        view,
                        new Circle(groupInfoPid.groupInfo)
                );
                break;
            case StatPid.CIRCLE_MINE:
            default:
                PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                        view.getContext(),
                        new Circle(groupInfoPid.groupInfo),
                        null
                );
        }
    }

    @Override
    public void startCircleDetailWithActivity(Context context, Circle circle, Bundle bundle) {
        if (context == null) {
            return;
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        Intent intent = new Intent(context, CircleDetailActivity.class);
        bundle.putParcelable(CIRCLE, circle);
        bundle.putInt(INTENT_CIRCLE_TYPE, R.id.circleDetailFragment);
        intent.putExtras(bundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void startFXAActivity(Context context, String id) {
        Intent intent = new Intent(context, CircleActivity.class);
        intent.putExtra(INTENT_CIRCLE_ID, id);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.fxaFragment);
        context.startActivity(intent);
    }

    @Override
    public void navigationToCircleDetail(View view, Circle circle) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(CIRCLE, circle);
        navigationToCircleDetail(view, arguments);
    }

    @Override
    public void navigationToCircleDetail(View view, Bundle bundle) {
        Navigation.findNavController(view).navigate(R.id.circleDetailFragment, bundle);
    }

    @Override
    public void navigationQAAnswerListFragment(View view, Circle circle, String formPid) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CIRCLE, circle);
        bundle.putString(Constant.EXTRA_FROM_PID, formPid);
        Navigation.findNavController(view).navigate(R.id.qaTeacherListFragment, bundle);
    }

    @Override
    public void startQAAnswerListActivity(View view, Circle circle, String formPid) {
        Intent intent = new Intent(view.getContext(), CircleActivity.class);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.qaTeacherListFragment);
        intent.putExtra(CIRCLE, circle);
        intent.putExtra(Constant.EXTRA_FROM_PID, formPid);
        view.getContext().startActivity(intent);
    }

    // =============================================================================================

    // ===================================== 启动修改页 ==============================================

    @Override
    public void startModifyCircleTextInfoWithActivity(Context context, int modifyType, String info) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, CircleActivity.class);
        intent.putExtra(EXTRA_MODIFY_TYPE, modifyType);
        intent.putExtra(EXTRA_MODIFY_INFO, info);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.modifyCircleTextInfoFragment);
        context.startActivity(intent);
    }

    @Override
    public void navigationToModifyCircleTextInfo(View view, int modifyType, String info) {
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_MODIFY_TYPE, modifyType);
        arguments.putString(EXTRA_MODIFY_INFO, info);
        Navigation.findNavController(view).navigate(R.id.modifyCircleTextInfoFragment, arguments);
    }

    @Override
    public void dealGVideoAPIExceptionError(Context context, GVideoAPIException gVideoAPIException) {
        if (gVideoAPIException == null) {
            return;
        }
        JsonElement result = gVideoAPIException.getResult();
        if (result == null) {
            return;
        }
        JsonObject jsonObject = (JsonObject) result;

        JsonElement nameJsonElement = jsonObject.get("name");
        String circleName = nameJsonElement == null ? "" : nameJsonElement.getAsString();

        JsonElement idJsonElement = jsonObject.get("id");
        long id = idJsonElement == null ? 0L : idJsonElement.getAsLong();

        JsonArray labelsJsonElement =  jsonObject.getAsJsonArray("labels");
        String str = labelsJsonElement == null ? ""  : labelsJsonElement.toString();

        JsonElement tenantIdJsonElement = jsonObject.get("tenantId");
        Long tenantId = tenantIdJsonElement == null ? 0L : tenantIdJsonElement.getAsLong();

        JsonElement tenantNameJsonElement = jsonObject.get("tenantName");
        String tenantName = tenantNameJsonElement == null ? "" : tenantNameJsonElement.getAsString();

        List<String> labels = new Gson().fromJson(str, new TypeToken<List<String>>() {
        }.getType());
        showJoinCircleDialog(context, id, circleName, tenantId, tenantName, labels,"");
    }

    @Override
    public void navigationToCircleTab(@NonNull View view, int index) {
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_CIRCLE_FRAGMENT_INDEX, index);
        NavController controller = Navigation.findNavController(view);
        controller.navigate(R.id.circle_nav_graph, arguments);
    }


    // =============================================================================================

    public void navigationToCircleFamousList(@NonNull View view, long groupId) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_GROUP_ID, groupId);
        Navigation.findNavController(view).navigate(R.id.circleFamousFragment, arguments);
    }

    @Override
    public void navigationToSearchTopic(View view, Circle circle) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(CIRCLE, circle);
        Navigation.findNavController(view).navigate(R.id.searchTopicFragment, arguments);
    }

    @Override
    public void startSearchTopicWithActivity(Context context, Circle circle) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, CircleActivity.class);
        intent.putExtra(CIRCLE, circle);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.searchTopicFragment);
        context.startActivity(intent);
    }

    @Override
    public void startSelectCircleWithActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, CircleActivity.class);
        intent.putExtra(INTENT_CIRCLE_TYPE, R.id.circleSelectFragment);
        context.startActivity(intent);
    }

    @Override
    public void joinCircle(long groupId, String reason, BaseResponseObserver<Object> baseResponseObserver) {
        new CircleRepository().joinCircle(groupId, reason).subscribe(baseResponseObserver);
    }

    @Override
    public Observable<Boolean> joinCircle(Circle circle, String fromPid, boolean showTipDialog) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) {

                if (InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId).get()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {

                    new CircleRepository().joinCircle(circle.groupId, "").subscribe(new BaseResponseObserver<Object>() {
                        @Override
                        protected void onRequestData(Object o) {
                            if (circle != null) {
                                GVideoSensorDataManager.getInstance().followCommunity(circle.groupId, circle.name,
                                                circle.tenantId, circle.tenantName, circle.labels,
                                                StatPid.getPageName(fromPid), "");
                                if (showTipDialog) {
                                    AsyncUtils.runOnUIThread(() -> {
                                        DefaultEnsureDialog needJoinCommunityDialog = new DefaultEnsureDialog(
                                                        AppManager.getAppManager().currentActivity());
                                        needJoinCommunityDialog.init(v -> {
                                                            GVideoEventBus.get(NEED_UPDATE_CIRCLE_INFO).post(null);
                                                            needJoinCommunityDialog.dismiss();
                                                            emitter.onNext(true); },
                                                        ResourcesUtils.getString(R.string.publish_join_community_tip),
                                                        ResourcesUtils.getString(R.string.alright));
                                        needJoinCommunityDialog.showJoinCommunityStyle();
                                        needJoinCommunityDialog.show();
                                        needJoinCommunityDialog.setOnCancelListener(v -> emitter.onComplete());
                                    });
                                } else {
                                    emitter.onNext(true);
                                    emitter.onComplete();
                                }
                            } else {
                                emitter.onComplete();
                            }
                        }

                        @Override
                        protected void onRequestError(Throwable throwable) {
                            if (throwable != null && throwable.getMessage() != null && throwable.getMessage().contains(
                                            "已加入该社区")) {
                                emitter.onNext(true);
                            } else {
                                GVideoSensorDataManager.getInstance().followCommunity(circle.groupId, circle.name,
                                                circle.tenantId, circle.tenantName, circle.labels,
                                                StatPid.getPageName(fromPid), throwable.getMessage());
                                emitter.onNext(false);
                                if (throwable instanceof TimeoutException || throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
                                    ToastUtils.showShort(R.string.all_network_not_available_action_tip);
                                } else {
                                    ToastUtils.showShort(throwable.getMessage());
                                }
                            }
                            emitter.onComplete();
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
