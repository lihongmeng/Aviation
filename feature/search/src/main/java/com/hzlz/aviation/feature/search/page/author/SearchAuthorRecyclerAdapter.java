package com.hzlz.aviation.feature.search.page.author;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.search.model.ISearchModel;
import com.hzlz.aviation.feature.search.model.SearchAuthorModel;
import com.hzlz.aviation.feature.search.page.SearchLayoutHelper;
import com.hzlz.aviation.feature.search.page.SearchRecyclerVH;
import com.hzlz.aviation.feature.search.template.SearchBaseTemplate;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.base.view.recyclerview.NoMoreDataViewHolder;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.feature.search.R;

/**
 * feed recyclerview适配器
 */
public class SearchAuthorRecyclerAdapter extends BaseRecyclerAdapter<SearchAuthorModel, BaseRecyclerViewHolder> {

    /** 当前数据对应的query词 */
    private String mQuery;
    /** no more data type */
    private static final int ITEM_TYPE_NO_MORE_DATA = -1;
    /** 当前是否显示no more data */
    private boolean mNoMoreData = false;
    /** 默认no more data 字符串资源 */
    private int mNoMoreDataRes = R.string.all_try_load_more;

    /**
     * 构造函数
     */
    public SearchAuthorRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (mNoMoreData ? 1 : 0);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NO_MORE_DATA) {
            return new NoMoreDataViewHolder(parent,
                new ContextThemeWrapper(parent.getContext(), R.style.SearchNoMoreDataDefaultStyle),
                R.string.all_try_load_more);
        }
        MediaBaseTemplate template = SearchLayoutHelper.createInstance(mContext, viewType, parent);
        return new SearchRecyclerVH(template);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder searchRecyclerVH, int position) {
        if (searchRecyclerVH instanceof SearchRecyclerVH) {
            final ISearchModel model = mList.get(position);
            model.setPosition(position);
            model.setSearchWord(mQuery);
            final MediaBaseTemplate template = ((SearchRecyclerVH) searchRecyclerVH).getSearchTemplate();
            if (template instanceof SearchBaseTemplate) {
                ((SearchBaseTemplate)template).update(model);
            }
        } else if (searchRecyclerVH instanceof NoMoreDataViewHolder) {
            ((NoMoreDataViewHolder) searchRecyclerVH).updateNoMoreDataText(mNoMoreDataRes);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mNoMoreData && position == mList.size()) {
            return ITEM_TYPE_NO_MORE_DATA;
        }
        if (mList == null || position >= mList.size()) {
            return super.getItemViewType(position);
        }
        return SearchLayoutHelper.SEARCH_LAYOUT_TYPE_AUTHOR;
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    /**
     * 显示没有更多数据
     *
     * @param stringRes 待更新的字符串资源
     */
    void showNoMoreData(int stringRes) {
        if (mNoMoreData) {
            updateNoMoreDataText(stringRes);
            return;
        }
        mNoMoreData = true;
        mNoMoreDataRes = stringRes;
        if (!mList.isEmpty()) {
            notifyItemInserted(getItemCount());
        }
    }

    /**
     * 隐藏没有更多数据
     */
    void hideNoMoreData() {
        if (!mNoMoreData) {
            return;
        }
        mNoMoreData = false;
        notifyItemRemoved(getItemCount());
    }

    /**
     * 更新没有更多数据
     *
     * @param stringRes 待更新的字符串资源
     */
    void updateNoMoreDataText(int stringRes) {
        if (!mNoMoreData) {
            return;
        }
        mNoMoreDataRes = stringRes;
        notifyItemChanged(getItemCount() - 1);
    }
}
