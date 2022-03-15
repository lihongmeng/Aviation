package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

/**
 * @author huangwei
 * date : 2021/12/29
 * desc :
 **/
public class GvideoLottieAnimationView extends LottieAnimationView {

    public GvideoLottieAnimationView(Context context) {
        this(context,null);
    }

    public GvideoLottieAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GvideoLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        //使用Destination进行跳转时会进行数据保存，再次回到主页数据会进行重新加载，但数据是错误的，暂时通过屏蔽数据保存解决
        return null;
    }
}
