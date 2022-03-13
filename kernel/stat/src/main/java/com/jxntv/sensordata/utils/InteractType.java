package com.jxntv.sensordata.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author huangwei
 * date : 2021/7/14
 * desc :
 **/
@IntDef({
        InteractType.FAVORITE,
        InteractType.COMMENT,
        InteractType.SHARE,
        InteractType.REPORT,
        InteractType.FAVORITE_CANCEL,
        InteractType.DEL_COMMENT,
        InteractType.QA_ANSWER_COMMENT,
        InteractType.QA_NORMAL_COMMENT,
})

@Retention(RetentionPolicy.SOURCE)
public @interface InteractType {
    int FAVORITE = 1;
    int COMMENT = 2;
    int SHARE = 3;
    int REPORT = 4;
    int FAVORITE_CANCEL = 5;
    int DEL_COMMENT = 6;
    /**
     * 问答 - 被提问者回答
     */
    int QA_ANSWER_COMMENT = 7;
    /**
     * 问答 - 普通用户回答
     */
    int QA_NORMAL_COMMENT = 8;
}
