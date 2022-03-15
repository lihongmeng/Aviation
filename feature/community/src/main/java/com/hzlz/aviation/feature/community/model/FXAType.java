package com.hzlz.aviation.feature.community.model;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author huangwei
 * date : 2021/7/30
 * desc : 放心爱活动类型
 **/
@IntDef({
        FXAType.SIGN,
        FXAType.PAIR,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FXAType {
    //签到
    int SIGN = 1;
    //配对
    int PAIR = 2;
}
