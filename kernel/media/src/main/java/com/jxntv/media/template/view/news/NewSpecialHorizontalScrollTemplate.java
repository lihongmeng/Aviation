package com.jxntv.media.template.view.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.MediaFragmentManager;
import com.jxntv.media.R;
import com.jxntv.media.databinding.NewsTplAdapterSpecialImageItemBinding;
import com.jxntv.media.databinding.NewsTplSpecialHorizontalScrollBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.media.template.MediaBaseTemplate;

/**
 * @author huangwei
 * date : 2021/11/17
 * desc : 横向滑动专题新闻
 **/
public class NewSpecialHorizontalScrollTemplate extends MediaBaseTemplate {

    private NewsTplSpecialHorizontalScrollBinding mBinding;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewSpecialHorizontalScrollTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_special_horizontal_scroll,
                parent, false);
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);

        mBinding.setVariable(BR.feedModel, mMediaModel);
        Context mContext = mBinding.getRoot().getContext();

        mBinding.llContent.removeAllViews();
        LayoutInflater inflaters = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < mediaModel.getItems().size(); i++) {
            MediaModel model = mediaModel.getItems().get(i);
            View view = inflaters.inflate(R.layout.news_tpl_adapter_special_image_item, null);
            NewsTplAdapterSpecialImageItemBinding binding = DataBindingUtil.bind(view);
            assert binding != null;
            binding.setFeedModel(model);
            view.setOnClickListener(v -> {
                PluginManager.get(DetailPagePlugin.class).dispatchToDetail(mContext, model, null);
            });
            mBinding.llContent.addView(view);
        }

        View footerMore = inflaters.inflate(R.layout.news_tpl_special_footer_more, null);
        mBinding.llContent.addView(footerMore);
        footerMore.setOnClickListener(v -> mBinding.more.performClick());

        BaseFragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
        RecyclerView recyclerView = null;
        if (fragment instanceof MediaPageFragment) {
            recyclerView = ((MediaPageFragment) fragment).getRecyclerView();
        }
        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mBinding.scroll.scrollTo((int) (mBinding.scroll.getScrollX() + (dy * 0.12)),
                            mBinding.scroll.getScrollY());
                }
            });
        }

        mBinding.more.setOnClickListener(view -> {
            PluginManager.get(VideoPlugin.class).startNewsList(mContext, mediaModel, null);
        });

        getRootLayout().requestLayout();
        getRootLayout().invalidate();

    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    @Override
    protected View getPlayView() {
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onChangeToDetail() {

    }

    @Override
    public void onBackFeed() {

    }

    @Override
    public void mute(boolean value) {
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }


}
