package com.jxntv.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.plugin.SearchPlugin;
import com.jxntv.search.page.fragment.SearchShortVideoFragment;
import com.jxntv.search.utils.SearchFragmentHelper;
import io.reactivex.rxjava3.core.Observable;

public class SearchPluginImpl implements SearchPlugin {

    @Override
    public void navigateToSearchActivity(@NonNull Context context) {
        navigateToSearchActivity(context, "");
    }

    @Override
    public void navigateToSearchActivity(@NonNull Context context, String searchWord) {
        Intent intent = new Intent(context, SearchActivity.class);
        if (!TextUtils.isEmpty(searchWord)) {
            Bundle arguments = new Bundle();
            arguments.putString("searchWord", searchWord);
            intent.putExtras(arguments);
        }
        context.startActivity(intent);
    }

    @Nullable
    @Override
    public Observable<ShortVideoListModel> loadMoreSearchData(boolean refresh, Bundle data) {
        if (data == null) {
            return null;
        }
        String fragmentId = data.getString(KEY_FRAGMENT_UUID);
        if (TextUtils.isEmpty(fragmentId)) {
            return null;
        }
        SearchShortVideoFragment fragment =
                SearchFragmentHelper.getInstance().getShortVideoFragment(fragmentId);
        if (fragment == null) {
            return null;
        }
        return fragment.loadMoreShortData(refresh);
    }
}
