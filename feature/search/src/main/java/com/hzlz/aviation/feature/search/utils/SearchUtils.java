package com.hzlz.aviation.feature.search.utils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索 工具类
 */
public class SearchUtils {

    /**
     * 生成video model列表
     *s
     * @param models 待生成的search models
     * @return 生成的video model列表
     */
    @NonNull
    public static List<VideoModel> buildVideoModelList(List<SearchDetailModel> models) {
        List<VideoModel> list = new ArrayList<>();
        if (models == null) {
            return list;
        }
        VideoModel model;
        for (int i = 0; i < models.size(); i++) {
            model = buildVideoModel(models.get(i));
            if (model != null) {
                list.add(model);
            }
        }
        return list;
    }

    /**
     * 生成video model
     *
     * @param model 待生成的searchmodel
     * @return 生成的video model
     */
    public static VideoModel buildVideoModel(SearchDetailModel model) {
        if (model == null || !model.isValid()) {
            return null;
        }
        VideoModel vm = VideoModel.Builder.aVideoModel()
                .withTitle(model.getTitle())
                .withAuthor(buildAuthorMode(model.authorName, model.authorAvatar))
                .withId(model.getId())
                .withCoverUrl(model.getCoverUrl())
                .withMediaType(model.getMediaType())
                //.withMediaUrls(model.mediaUrls)
                //.withSourceType(model.sourceType)
                //.withTagType(model.tagType)
                //.withIsFavor(0)
                .build();
        return vm;
    }

    /**
     * 生成author model
     *
     * @return 生成的作者模板
     */
    private static AuthorModel buildAuthorMode(String name, String avatar) {
        return AuthorModel.Builder.anAuthorModel()
                //.withId(authorModel.id)
                .withName(name)
                .withAvatar(avatar)
                //.withIntro(authorModel.intro)
                //.withIsFollow(authorModel.isFollow)
                .build();
    }
}
