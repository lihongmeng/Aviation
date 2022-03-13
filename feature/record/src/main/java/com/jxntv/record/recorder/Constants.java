package com.jxntv.record.recorder;

public interface Constants {

    String NET_BODY_KEY_CONTENT = "content";
    String NET_BODY_KEY_FILE_TYPE = "fileType";
    String NET_BODY_KEY_IMAGE_ID = "imageId";
    String NET_BODY_KEY_IMAGE_LIST = "imageList";
    String NET_BODY_KEY_INTRODUCTION = "introduction";
    String NET_BODY_KEY_IS_PUBLIC = "isPublic";
    String NET_BODY_KEY_LENGTH = "length";
    String NET_BODY_KEY_SOUND_CONTENT = "soundContent";
    String NET_BODY_KEY_VIDEO_ID = "videoId";
    String NET_BODY_KEY_TOPIC_ID = "topicId";
    String NET_BODY_KEY_GROUP_ID = "groupId";
    String NET_BODY_KEY_FILE_NAME = "fileName";
    String NET_BODY_KEY_PLAY_STYLE = "playStyle";
    String NET_BODY_KEY_IS_QA = "answer";
    String NET_BODY_KEY_LINK_TITLE = "linkTitle";
    String NET_BODY_KEY_LINK_VALUE = "linkValue";

    interface PublishFileType {

        // 视频
        int VIDEO = 1;

        // 文字、图片
        int PIC = 3;

        // 语音
        int SOUND = 4;

    }

    interface SELECT_IMAGE_TYPE {

        // 批量替换
        int BATCH_REPLACE = 0;

        // 单张替换
        int SINGLE_PLACE = 1;

        // 批量添加
        int BATCH_ADD = 2;

    }

}
