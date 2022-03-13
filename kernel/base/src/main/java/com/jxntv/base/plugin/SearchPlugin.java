package com.jxntv.base.plugin;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.ioc.Plugin;
import io.reactivex.rxjava3.core.Observable;

/**
 * search plugin
 */
public interface SearchPlugin extends Plugin {

    /** KEY: fragment uuid */
    String KEY_FRAGMENT_UUID = "key_fragment_uuid";

    /**
     * 跳转至搜索activity
     *
     * @param context 上下文
     */
    void navigateToSearchActivity(@NonNull Context context);

    /**
     * 跳转至搜索activity
     *
     * @param context 上下文
     * @param searchWord 搜索的关键词
     */
    void navigateToSearchActivity(@NonNull Context context,String searchWord);

    /**
     * 加载更多search短视频/音频数据
     *
     * @param refresh 进入详情页首次加载数据时，会清空掉历史cursor；
     * @param data  对应的bundle信息，用于内部判断
     * @return  对应的observable
     */
    @Nullable
    Observable<ShortVideoListModel> loadMoreSearchData(boolean refresh, Bundle data);
}
