package com.hzlz.aviation.feature.community.databinding;

import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.circle.CircleTag;

import java.util.List;

public class FindCircleObservable {
    public final CheckThreadLiveData<List<CircleTag>> circleTagList = new CheckThreadLiveData<>();

}
