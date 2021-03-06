package com.hzlz.aviation.feature.community.fragment.qa;

import android.view.View;

import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2022/1/10
 * desc : 回答者viewModel
 **/
public class QAAnswerMessageViewModel {

    private Circle circle;

    private String pid;

    public QAAnswerMessageViewModel(Circle circle,String pid) {
        this.circle = circle;
        this.pid = pid;
    }

    public void onAskQuestionClick(View view) {
        onAskQuestionClick(view, null);
    }

    public void onAskQuestionClick(View view, AuthorModel authorModel) {
        if (PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            if (!InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId).get()) {
                PluginManager.get(CirclePlugin.class).joinCircle(circle, pid, true).subscribe(f -> {
                    if (f) {
                        PluginManager.get(RecordPlugin.class).startQAPublishActivity(view.getContext(), circle,
                                        authorModel, pid);
                    }
                });
            } else {
                PluginManager.get(RecordPlugin.class).startQAPublishActivity(view.getContext(), circle, authorModel,
                                pid);
            }
        } else {
            PluginManager.get(AccountPlugin.class).startLoginActivity(view.getContext());
        }
    }

    public void onAvatarClick(View view, AuthorModel authorModel) {
        PluginManager.get(AccountPlugin.class).startPgcActivity(view, authorModel);
    }

    /**
     * 进入全部导师选择界面
     */
    public void onQAGroupALlClicked(View view) {
        PluginManager.get(CirclePlugin.class).startQAAnswerListActivity(view, circle, pid);
    }
}
