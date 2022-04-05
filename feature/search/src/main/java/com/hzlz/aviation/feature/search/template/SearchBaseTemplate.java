package com.hzlz.aviation.feature.search.template;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.search.SearchRuntime;
import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索模板基类
 */
public abstract class SearchBaseTemplate extends MediaBaseTemplate implements ISearchTemplate {

    /** 持有的搜索数据模型 */
    protected ISearchModel mSearchModel;

    /**
     * 构造函数
     *
     * @param context
     */
    public SearchBaseTemplate(Context context) {
        super(context);
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);
        TextView titleView = getTitleTextView();

        if (titleView != null && !TextUtils.isEmpty(mediaModel.getTitle())
                && mediaModel.getWords() != null && mediaModel.getWords().size() > 0) {
            titleView.setText(SpannableStringUtils.setSpanColor(mediaModel.getTitle(),
                    mediaModel.getWords(), R.color.color_e4344e));
        }
    }

    @Override
    public void update(ISearchModel searchModel) {
        mSearchModel = searchModel;
//        TextView title = getTitleTextView();
//        if (title != null) {
//            SearchSpanTextHelper.createSearchTitle(title, searchModel.getTitle(),
//                    searchModel.getSearchWord(), getTitleColor());
//        }
        //if (searchModel instanceof SearchDetailModel) {
        //    SearchDetailModel detailModel = (SearchDetailModel) searchModel;
        //    if (detailModel.isAudio) {
        //        TagTextHelper.createTagTitle(SearchRuntime.getAppContext(), getTagTextView(), "",
        //                detailModel.tagType);
        //    }
        //
        //}
    }

    /**
     * 获取用于统一处理title样式的 title view
     *
     * @return title view
     */
    protected abstract TextView getTitleTextView();

    /**
     * 获取用于统一处理tag样式的 title view
     *
     * @return title view
     */
    protected abstract TextView getTagTextView();

    /**
     * 获取title颜色
     *
     * @return title颜色
     */
    protected int getTitleColor() {
        return SearchRuntime.getAppContext().getResources().getColor(R.color.color_212229);
    }

    @Override
    public ISearchModel getSearchModel() {
        return mSearchModel;
    }

    @Override
    public abstract ViewDataBinding getDataBinding();


    @Override
    protected View getPlayView() {
        return null;
    }

    @Override
    public void onChangeToDetail() {

    }

    @Override
    public void onBackFeed() {

    }

}
