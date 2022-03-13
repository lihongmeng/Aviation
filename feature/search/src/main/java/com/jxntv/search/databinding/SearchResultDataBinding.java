package com.jxntv.search.databinding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.ioc.PluginManager;
import com.jxntv.search.R;
import com.jxntv.search.SearchRuntime;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.model.SearchType;
import com.jxntv.search.utils.SearchUtils;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;

/**
 * 搜索结果data binding
 */
public class SearchResultDataBinding {

    public void clickCommon(SearchDetailModel searchDetailModel) {
        GVideoSensorDataManager.getInstance().clickSearchResult(
                searchDetailModel.getId(),
                getContentName(searchDetailModel),
                searchDetailModel.getSearchTypeString(),
                searchDetailModel.searchWord,
                searchDetailModel.position,
                searchDetailModel.hintSearchWord
        );
    }

    /**
     * 模板点击，跳转详情页
     */
    public void onLayoutClick(View view, SearchDetailModel model) {
        clickCommon(model);
        Bundle extras = new Bundle();
        extras.putInt(VideoPlugin.EXTRA_START, VideoPlugin.START_DEFAULT);
        extras.putString(VideoPlugin.EXTRA_FROM_PID, StatPid.SEARCH);
        VideoModel videoModel = SearchUtils.buildVideoModel(model);
        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(view.getContext(), videoModel, extras);
    }


    /**
     * 模板点击事件
     */
    public void onCommunityClick(View view, SearchDetailModel model) {
        clickCommon(model);
        Circle circle = new Circle();
        circle.groupId = Long.parseLong(model.getId());
        circle.name = model.getTitle();
        PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(view.getContext(), circle, null);
    }

    /**
     * 作者点击事件
     */
    public void onAvatarClick(View view, SearchDetailModel model) {
        clickCommon(model);
        AuthorModel authorModel = new AuthorModel();
        authorModel.setId(model.getId());
        authorModel.setType(model.authorType);
        PluginManager.get(AccountPlugin.class).startPgcActivity(view, authorModel);
    }

    public void onProgramClick(View view,SearchDetailModel model){
        clickCommon(model);
        PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(view.getContext(),
                Long.parseLong(model.getId()), model.getPid());
    }

    /**
     * 关注点击事件
     */
    public void onAttentionClick(View view, SearchDetailModel model) {
//        if (TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken())) {
//            PluginManager.get(AccountPlugin.class).startLoginFragment(view);
//            return;
//        }
//        if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
//            return;
//        }
//        PluginManager.get(AccountPlugin.class).getFollowRepository()
//            .followAuthor(model.getId(), model.getType(),model.getName(), !model.isFollowed.get())
//            .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
//                @Override protected void onRequestData(Boolean followed) {
//                    model.isFollowed.set(followed);
//                }
//
//                @Override protected void onRequestError(Throwable throwable) {
//                    GVideoSensorDataManager.getInstance().followAccount(!model.isFollowed.get(),
//                            model.getId(),model.getName(),model.getType(),throwable.getMessage());
//                }
//            });
    }


    /**
     * 获取音频蒙层资源
     */
    public Drawable getSoundResource(SearchDetailModel model) {
        if (model.getTagType() == TagHelper.FEED_TAG_LIVE) {
            return SearchRuntime.getAppContext().getResources().getDrawable(R.drawable.search_fm_cover);
        }
        return SearchRuntime.getAppContext().getResources().getDrawable(R.drawable.search_music_cover);
    }

    public String getContentName(SearchDetailModel searchDetailModel) {
        String result = "";
        switch (searchDetailModel.category) {
            case SearchType.CATEGORY_AUTHORS:
                result = searchDetailModel.authorName;
                break;
            case SearchType.CATEGORY_COMMUNITY:
            case SearchType.CATEGORY_ALL:
            case SearchType.CATEGORY_PROGRAM:
            case SearchType.CATEGORY_NEWS:
            case SearchType.CATEGORY_MOMENT:
                result = searchDetailModel.getTitle();
                break;
        }
        return result;
    }

}


