package com.jxntv.circle.databinding;

import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.circle.CircleTag;

import java.util.List;

public class FindCircleObservable {
    public final CheckThreadLiveData<List<CircleTag>> circleTagList = new CheckThreadLiveData<>();

}
