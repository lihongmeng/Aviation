package com.hzlz.aviation.feature.search.template.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.search.databinding.SearchMomentBinding;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.template.IMediaTemplate;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.media.template.MediaFactory;
import com.hzlz.aviation.kernel.media.template.view.MediaBaseVideoTemplate;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.feature.search.R;

import java.util.HashMap;

/**
 * <p>文件描述：搜索动态</p>
 * <p>作者：hanxw</p>
 * <p>创建时间：2022/2/14</p>
 * <p>更改时间：2022/2/14</p>
 * <p>版本号：1</p>
 */
public class SearchMomentTemplate extends MediaBaseTemplate {
    private final SearchMomentBinding mBinding;

    /**
     * 构造函数
     *
     * @param context
     */
    public SearchMomentTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_moment,
                parent, false);
    }

    public void update(HashMap<Integer, IMediaTemplate> momentViews,
                       @NonNull MediaModel mediaModel, boolean isDarkMode, int position) {
        mBinding.layoutContent.removeAllViews();
        mBinding.contentTag.setVisibility(mediaModel.isShowTag ? View.VISIBLE : View.GONE);

        if (mediaModel.getMediaType() != 0) {
            IMediaTemplate template =
                    MediaFactory.createInstance(mBinding.layoutContent.getContext(),
                            mediaModel.getMediaType(), null);
            mediaModel.updateFrom(mediaModel);
            template.update(mediaModel, isDarkMode, StatPid.SEARCH, position);
            if (template instanceof MediaBaseTemplate) {
                TextView titleView = ((MediaBaseTemplate) template).getTitleView();
                if (titleView != null && !TextUtils.isEmpty(mediaModel.getTitle())
                        && mediaModel.getWords() != null && mediaModel.getWords().size() > 0) {
                    titleView.setText(SpannableStringUtils.setSpanColor(mediaModel.getTitle(),
                            mediaModel.getWords(), R.color.color_e4344e));
                }
            }

            if (momentViews != null) {
                if (template instanceof MediaBaseVideoTemplate) {
                    momentViews.remove(position);
                    momentViews.put(position, template);
                }
            }
            mBinding.layoutContent.addView(template.getDataBinding().getRoot());
        }
    }


    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
//        super.update(mediaModel, isDarkMode, fragmentId, position);
        update(null, mediaModel, isDarkMode, position);
    }


    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    @Override
    protected View getPlayView() {
        return mBinding.rootLayout;
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
}
