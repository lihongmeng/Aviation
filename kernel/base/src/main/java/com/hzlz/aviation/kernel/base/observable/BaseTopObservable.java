package com.hzlz.aviation.kernel.base.observable;

import android.content.Context;

interface BaseTopObservable {
    void updateToNormal(Context context);
    void updateToRed(Context context);
}
