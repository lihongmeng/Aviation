package com.jxntv.base.utils;

import static com.jxntv.base.Constant.BUNDLE_KEY.CIRCLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.DimenRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.base.R;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.widget.GVideoBanner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/9/10
 * desc :
 **/
public class BannerUtils {

    /**
     * 加载Banner数据
     * <p>
     * 因为Banner控件的布局未知，“若Banner数据无效，隐藏.....”之类的逻辑
     * 放在业务层自行处理更为合适，故此方法就直接使用Banner数据，不做有效判断
     *
     * 新闻页的Banner使用的是MediaModel，因为Module的访问级别问题
     * 就不使用此方法
     *
     * @param model  Banner数据
     * @param banner Banner控件
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initDefaultBanner(BannerModel model, GVideoBanner banner,String pid) {
        Context context = banner.getContext();
        if (context == null) {
            return;
        }
        BannerImageAdapter bannerImageAdapter = new BannerImageAdapter(model.getImages()) {
            @Override
            public void onBindView(Object o, Object data, int position, int size) {
                ImageView imageView = ((BannerImageHolder) o).imageView;
                Context context = imageView.getContext();
                Glide.with(context)
                        .load(((BannerModel.ImagesBean) data).getUrl())
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_8DP))))
                        .into(imageView);
                // ImageLoaderManager.loadImage(imageView, ((BannerModel.ImagesBean) data).getUrl(), true);
            }
        };
        int interval = model.getInterval();
        long loopTime = interval == 0 ? 3000 : interval * 1000L;
        OnBannerListener onBannerListener = (data, position) -> {
            BannerModel.ImagesBean bean = (BannerModel.ImagesBean) data;
            int mediaType = bean.getMediaType();
            if (mediaType == MediaType.QA_LIST_GROUP){
                try {
                    Circle circle = new Circle();
                    circle.setJoin(bean.isJoined());
                    circle.setGroupId(Long.parseLong(bean.getMediaId()));
                    Bundle bundle=new Bundle();
                    bundle.putParcelable(CIRCLE,circle);
                    PluginManager.get(CirclePlugin.class).startQAGroupActivity(banner, bundle);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, new VideoModel(bean), null);
            }
            PluginManager.get(StatPlugin.class).bannerClick(bean, position, pid, bean.getType());
        };
        List<BannerModel.ImagesBean> imagesBeanList = model.getImages();
        banner.setAdapter(bannerImageAdapter).setLoopTime(loopTime).setOnBannerListener(onBannerListener);
        if (imagesBeanList != null && imagesBeanList.size() > 1) {
            initDefaultIndicator(banner, true ,IndicatorConfig.Direction.RIGHT, R.dimen.DIMEN_10DP, R.dimen.DIMEN_7DP);
        }
    }

    /**
     * @param isWhite   true 未选择时白色指示器， 否则黑色
     */
    @SuppressLint("ResourceType")
    public static void initDefaultIndicator(GVideoBanner banner, boolean isWhite, @IndicatorConfig.Direction int gravity,
                    @DimenRes int rightMargin, @DimenRes int bottomMargin) {
        if (banner != null) {
            Context context = banner.getContext();
            IndicatorConfig.Margins margins = new IndicatorConfig.Margins();
            if (rightMargin > 0) {
                margins.rightMargin = context.getResources().getDimensionPixelOffset(rightMargin);
            }
            if (bottomMargin > 0) {
                margins.bottomMargin = context.getResources().getDimensionPixelOffset(bottomMargin);
            }
            banner.setIndicator(new CircleIndicator(banner.getContext()))
                            .setIndicatorNormalColorRes(isWhite?R.color.color_ffffff_50:R.color.color_000000_15)
                            .setIndicatorSelectedColorRes(R.color.color_e4344e)
                            .setIndicatorNormalWidth(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_4DP))
                            .setIndicatorSelectedWidth(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_6DP))
                            .setIndicatorGravity(gravity)
                            .setIndicatorMargins(margins);
        }

    }

}
