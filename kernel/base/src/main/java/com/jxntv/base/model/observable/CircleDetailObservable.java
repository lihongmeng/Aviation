package com.jxntv.base.model.observable;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableLong;

import com.jxntv.base.model.circle.CircleCommentImage;
import com.jxntv.base.model.circle.CircleDetailFavoriteVO;
import com.jxntv.base.model.circle.CircleDetailLiveVo;
import com.jxntv.base.model.circle.TopicDetail;

import java.util.List;

public class CircleDetailObservable {

    /**
     * 圈子封面
     */
    public ObservableField<CircleCommentImage> cover = new ObservableField<>();

    /**
     * 红人信息
     */
    public ObservableField<CircleDetailFavoriteVO> favoriteVO = new ObservableField<>();

    /**
     * 圈子id
     */
    public ObservableLong groupId = new ObservableLong();

    /**
     * 圈子简介
     */
    public ObservableField<String> introduction = new ObservableField<>();

    /**
     * 当前用户是否加入圈子
     */
    public ObservableBoolean join = new ObservableBoolean();

    /**
     * 直播对象
     */
    public ObservableField<CircleDetailLiveVo> liveVO = new ObservableField<>();

    /**
     * 圈子名称
     */
    public ObservableField<String> name = new ObservableField<>();

    /**
     * 圈主id
     */
    public ObservableLong owner = new ObservableLong();

    /**
     * 圈主手机
     */
    public ObservableField<String> ownerMobile = new ObservableField<>();


    /**
     * 话题前三
     */
    public ObservableField<List<TopicDetail>> topicList = new ObservableField<>();


}
