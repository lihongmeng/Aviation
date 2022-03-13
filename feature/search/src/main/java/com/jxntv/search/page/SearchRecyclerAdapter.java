package com.jxntv.search.page;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.base.view.recyclerview.NoMoreDataViewHolder;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.template.IMediaTemplate;
import com.jxntv.media.template.MediaBaseTemplate;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.search.R;
import com.jxntv.search.model.ISearchModel;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.model.SearchType;
import com.jxntv.search.template.ISearchTemplate;
import com.jxntv.search.template.SearchBaseTemplate;
import com.jxntv.stat.StatPid;

import java.util.HashMap;

/**
 * feed recycler view适配器
 */
public class SearchRecyclerAdapter extends BaseRecyclerAdapter<SearchDetailModel, BaseRecyclerViewHolder> {

    /** tab 类型 */
    private int mTabType;
    /** 当前数据对应的query词 */
    private String mQuery;
    /** 当前数据对应的预制文案query词 */
    private String mHintQuery;
    /** no more data type */
    private static final int ITEM_TYPE_NO_MORE_DATA = -1;
    /** 当前是否显示no more data */
    private boolean mNoMoreData = false;
    /** 默认no more data 字符串资源 */
    private int mNoMoreDataRes = R.string.all_try_load_more;

    private final HashMap<Integer,IMediaTemplate> mMomentViews;
    /**
     * 构造函数
     */
    public SearchRecyclerAdapter(Context context, int tabType) {
        super(context);
        mTabType = tabType;
        mMomentViews = new HashMap<>();
    }

    public HashMap<Integer,IMediaTemplate> getMomentViews() {
        return mMomentViews;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果是Header、Footer的对象则占据spanCount的位置，否则就只占用1个位置
                    return (isFooter(position)) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 判断是否是Footer的位置
     *
     * @param position  对应位置
     * @return 是否未footer
     */
    public boolean isFooter(int position){
        return mNoMoreData && position == getItemCount() - 1;
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
            final SearchDetailModel model = mList.get(position);
            model.setPosition(position);
            model.setSearchWord(mQuery);
            model.setHintSearchWord(mHintQuery);
            final MediaBaseTemplate template = ((SearchRecyclerVH) searchRecyclerVH).getSearchTemplate();
            if(mTabType == SearchType.CATEGORY_ALL) {
                if (position == 0) {
                    model.setShowTag(true);
                } else {
                    final ISearchModel lastModel = mList.get(position - 1);
                    model.setShowTag(lastModel.getSearchType() != model.getSearchType());
                }
            }
            model.showMediaPageSource = MediaPageSource.PageSource.SEARCH;
            if (template instanceof SearchBaseTemplate) {
                ((SearchBaseTemplate)template).update(model);
            }
            template.update(model,false, StatPid.SEARCH,position);
        } else if (searchRecyclerVH instanceof NoMoreDataViewHolder) {
            int dp24 = GVideoRuntime.getAppContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_24DP);
            ((NoMoreDataViewHolder) searchRecyclerVH).setPadding(0,dp24,0, dp24);
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
        return SearchLayoutHelper.getPageViewType(mList.get(position).category, mList.get(position));
    }

    public void setQuery(String query) {
        mQuery = query;
    }
    public void setHintQuery(String hintQuery) {
        mHintQuery = hintQuery;
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
