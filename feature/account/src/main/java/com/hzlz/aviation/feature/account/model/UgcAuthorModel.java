package com.hzlz.aviation.feature.account.model;

import com.hzlz.aviation.kernel.base.model.video.AuthorModel;

/**
 * @author huangwei
 * date : 2021/6/17
 * desc : 个人主页关注的人、加入的圈子
 **/
public class UgcAuthorModel extends AuthorModel {

    //圈子相关
    private String groupId;
    private String groupImageUrl;
    private String groupName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
