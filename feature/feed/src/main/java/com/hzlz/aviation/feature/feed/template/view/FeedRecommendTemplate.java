package com.hzlz.aviation.feature.feed.template.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.feed.BR;
import com.hzlz.aviation.feature.feed.FeedRuntime;
import com.hzlz.aviation.feature.feed.R;
import com.hzlz.aviation.feature.feed.databinding.FeedOneImgTemplateLayoutBinding;
import com.hzlz.aviation.feature.feed.databinding.FeedSlideChildLayoutBinding;
import com.hzlz.aviation.feature.feed.databinding.FeedSlideChildLayoutBindingImpl;
import com.hzlz.aviation.feature.feed.databinding.FeedSlideLayoutBinding;
import com.hzlz.aviation.feature.feed.databinding.FeedSlideRecommendChildLayoutBinding;
import com.hzlz.aviation.feature.feed.frame.databind.FeedRecommendDataBind;
import com.hzlz.aviation.feature.feed.utils.FeedConstants;
import com.hzlz.aviation.feature.feed.utils.FeedSpanTextHelper;
import com.hzlz.aviation.feature.feed.utils.FeedUtils;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.media.MediaConstants;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

import java.util.List;

/**
 * feed ??????????????????
 */
public class FeedRecommendTemplate {
  private static final boolean DEBUG = true;
  private static final String TAG = FeedRecommendTemplate.class.getSimpleName();
  /** ???????????????binding */
  private FeedOneImgTemplateLayoutBinding mNormalRecommendBinding;
  /** ??????????????????????????? slide layout binding */
  private FeedSlideLayoutBinding mSlideBinding;
  /** ????????????????????? */
  private boolean mIsDarkMode;

  private String mFragmentId;
  private MediaModel mMediaModel;
  private Context mContext;
  private ViewGroup mParentView;
  public FeedRecommendTemplate(Context context, ViewGroup parent,
      FeedSlideLayoutBinding slideLayoutBinding,
      FeedOneImgTemplateLayoutBinding normalRecommendBinding) {
    mContext = context;
    mParentView = parent;
    mSlideBinding = slideLayoutBinding;
    mNormalRecommendBinding = normalRecommendBinding;
  }

  /**
   * ?????????????????????
   *
   * @param model    feed????????????m
   */
  public void initRecommendView(@NonNull MediaModel model, boolean isDarkMode, String fragmentId) {
    if (DEBUG) {
      Log.d(TAG, "initRecommendView:" + model.getTitle() + "/isStick:" + model.isStick() + "/adverts:" + model.feedRecommendModelList);
    }
    mFragmentId = fragmentId;
    mMediaModel = model;
    mIsDarkMode = isDarkMode;

    if (model.isStick()) {
      initStickRecommend(model);
      mNormalRecommendBinding.normalRecommendLayout.setVisibility(GONE);
      return;
    }

    mSlideBinding.feedSlideLayout.setVisibility(GONE);
    if (mMediaModel == null || model.showRecTime < 0) {
      mNormalRecommendBinding.normalRecommendLayout.setVisibility(GONE);
    }
    if (model.showRecTime == 0) {
      showNormalRecommend(false);
    }
  }

  /**
   * ??????????????????????????? -- ????????????
   *
   * @param model    ??????????????????
   */
  private void initStickRecommend(@NonNull MediaModel model) {
    List<RecommendModel> list = model.feedRecommendModelList;
    if (list == null || list.size() == 0) {
      return;
    }
    mSlideBinding.feedSlideLayout.setVisibility(View.VISIBLE);
    // ????????????
    handleInitSlideRecommend(list,model.showMediaPageSource);
  }

  /**
   * ???????????????????????????
   *
   * @param models        ????????????????????????
   */
  private void handleInitSlideRecommend(List<RecommendModel> models, int pageSource) {
    if (mSlideBinding == null) {
      return;
    }

    mSlideBinding.recyclerView.setFocusable(false);
    mSlideBinding.recyclerView.setFocusableInTouchMode(false);
    mSlideBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

    if (pageSource == MediaPageSource.PageSource.AUDIOVISUAL){
      SlideRecommendAdapter mSlideAdapter = new SlideRecommendAdapter(mContext);
      mSlideBinding.recyclerView.setAdapter(mSlideAdapter);
      mSlideAdapter.refreshData(models);
    }else {
      SlideAdapter mSlideAdapter = new SlideAdapter(mContext);
      mSlideBinding.recyclerView.setAdapter(mSlideAdapter);
      mSlideAdapter.refreshData(models);
    }
  }

  /**
   * ?????????????????????
   */
  private class SlideAdapter extends BaseRecyclerAdapter<RecommendModel, SlideVH> {

    /** ???????????? */
    private int mConnerSize = (int) mContext.getResources().getDimension(R.dimen.feed_normal_border_radius);

    /**
     * ????????????
     */
    public SlideAdapter(Context context) {
      super(context);
    }

    @Override
    public SlideVH onCreateVH(ViewGroup parent, int viewType) {
      SlideVH vh = new SlideVH(
          FeedSlideChildLayoutBinding.inflate(mInflater, parent, false));
      return vh;
    }

    @Override
    public void onBindVH(SlideVH slideVH, int position) {
      RecommendModel model = mList.get(position);
      if (slideVH.getBinding() instanceof FeedSlideChildLayoutBindingImpl) {
        FeedSlideChildLayoutBindingImpl binding = (FeedSlideChildLayoutBindingImpl) slideVH.getBinding();

        FeedSpanTextHelper.createTagTitle(mContext, binding.slideLabel, "", model.tagType,
            mContext.getResources().getColor(R.color.color_ffffff));

        binding.slideChildImg.loadImageWithCorner(model.imageUrl, mConnerSize);

        binding.slideTitleText.setText(model.title);
        binding.slideSource.setText(model.author);
        binding.slideAd.setVisibility(model.isAd == FeedConstants.RECOMMEND_AD ? VISIBLE : GONE);
        binding.slideVideoImg.setVisibility(
                (model.isLongVideo() || model.isShortVideo() || model.isIMLive())&&
                model.isAd != FeedConstants.RECOMMEND_AD ? VISIBLE : GONE);

        if (mIsDarkMode) {
          binding.slideTitleText.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
          binding.slideSource.setTextColor(mContext.getResources().getColor(R.color.color_8a8c99));
        }

        slideVH.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            clickAdvert(mSlideBinding.recyclerView, model);
            statAdvert(model, true, mFragmentId);
          }
        });
        statAdvert(model, false, mFragmentId);
      }

    }
  }


  /**
   * ?????????????????????
   */
  private class SlideRecommendAdapter extends BaseRecyclerAdapter<RecommendModel, SlideVH> {

    /** ???????????? */
    private int mConnerSize = (int) mContext.getResources().getDimension(R.dimen.DIMEN_4DP);

    /**
     * ????????????
     */
    public SlideRecommendAdapter(Context context) {
      super(context);
    }

    @Override
    public SlideVH onCreateVH(ViewGroup parent, int viewType) {
      SlideVH vh = new SlideVH(FeedSlideRecommendChildLayoutBinding.inflate(mInflater, parent, false));
      return vh;
    }

    @Override
    public void onBindVH(SlideVH slideVH, int position) {
      RecommendModel model = mList.get(position);
      if (slideVH.getBinding() instanceof FeedSlideRecommendChildLayoutBinding) {
        FeedSlideRecommendChildLayoutBinding binding = (FeedSlideRecommendChildLayoutBinding) slideVH.getBinding();

        binding.slideChildImg.loadImageWithCorner(model.imageUrl, mConnerSize);

        binding.slideTitleText.setText(model.title);

        if (mIsDarkMode) {
          binding.slideTitleText.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
        }

        slideVH.getBinding().getRoot().setOnClickListener(v -> {
          clickAdvert(mSlideBinding.recyclerView, model);
        });
      }
    }
  }

  /**
   * ??????????????????
   */
  private static class SlideVH extends BaseRecyclerViewHolder {
    /**
     * ????????????
     *
     * @param binding
     */
    SlideVH(ViewDataBinding binding) {
      super(binding);
    }
  }


  /**
   * ????????????recommend??????????????????
   */
  private void showNormalRecommend(boolean needShowAnim) {
    // 1. ????????????????????????????????????
    if (mMediaModel == null || mMediaModel.isStick()) {
      return;
    }
    // 2. ??????????????????
    RecommendModel model = findRecommend();
    if (model == null) {
      return;
    }
    mNormalRecommendBinding.setVariable(BR.recommendModel, model);
    mNormalRecommendBinding.setVariable(BR.recommendBind, new FeedRecommendDataBind(mIsDarkMode, mFragmentId));
    mNormalRecommendBinding.executePendingBindings();
    mNormalRecommendBinding.oneImgAd.setVisibility(model.isAd == FeedConstants.RECOMMEND_AD ? VISIBLE : GONE);
    mNormalRecommendBinding.normalRecommendLayout.setVisibility(VISIBLE);
    statAdvert(model, false, mFragmentId);
    if (needShowAnim) {
      showRecommendAnim();
    } else {
      float height = FeedRuntime.getAppContext().getResources().getDimension(R.dimen.r_h03);
      mNormalRecommendBinding.normalRecommendLayout.getLayoutParams().height = (int) height;
      mNormalRecommendBinding.normalRecommendLayout.requestLayout();
    }
  }

  /**
   * ???????????????recommend??????
   *
   * @return ?????????recommend??????
   */
  private RecommendModel findRecommend() {
    if (mMediaModel == null || mMediaModel.feedRecommendModelList == null
        || mMediaModel.feedRecommendModelList.size() == 0) {
      return null;
    }
    // ?????????????????????????????????????????????
    RecommendModel model = mMediaModel.feedRecommendModelList.get(0);
    if (model == null || model.isManual != FeedConstants.RECOMMEND_MANUAL) {
      return null;
    }
    return model;
  }

  /**
   * ??????recommend??????
   */
  private void showRecommendAnim() {
    float height = FeedRuntime.getAppContext().getResources().getDimension(R.dimen.r_h03);
    // ?????????????????????????????????
    if (mNormalRecommendBinding.normalRecommendLayout.getLayoutParams().height == height) {
      return;
    }
    mNormalRecommendBinding.normalRecommendLayout.setPivotY(0);
    ValueAnimator animator = ValueAnimator.ofInt(0, (int)height);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mNormalRecommendBinding.normalRecommendLayout.getLayoutParams().height =
            (int) animation.getAnimatedValue();
        mNormalRecommendBinding.normalRecommendLayout.requestLayout();
      }
    });
    animator.setDuration(500);
    animator.start();
  }

  private int playTemp = -1;

  public void onPlay() {
    if (mMediaModel == null) {
      return;
    }
    playTemp = 0;
    if (mMediaModel.showRecTime > 0) {
      mParentView.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (mMediaModel.playState == MediaConstants.STATE_PLAY &&
              playTemp == 0) {
            showNormalRecommend(true);
          }
        }
      }, mMediaModel.showRecTime * FeedConstants.THOUSAND);
    }
  }

  public void onStop() {
    playTemp = 1;
  }

  private static StatFromModel getStat(RecommendModel recommendModel, String fragmentId) {
    Fragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
    String pid = "";
    String channelId = "";
    if (fragment instanceof MediaPageFragment) {
      pid = ((MediaPageFragment) fragment).getPid();
      channelId = ((MediaPageFragment) fragment).getChannelId();
    }
    String contentId = recommendModel.contentId;
    String fromPid = "";
    String fromChannelId = "";

    StatFromModel stat = new StatFromModel(contentId, pid, channelId, fromPid, fromChannelId);
    return stat;
  }

  private void clickAdvert(View view, RecommendModel recommendModel) {
    FeedUtils.clickAdvert(view, recommendModel, mFragmentId);
  }

  public static void statAdvert(RecommendModel recommendModel, boolean click, String fragmentId) {

    String extendId = recommendModel.extendId;
    String extendName = recommendModel.title;
    String extendShowType = String.valueOf(recommendModel.extendType);
    String place = StatConstants.DS_KEY_PLACE_ADVERT;

    StatFromModel stat = getStat(recommendModel, fragmentId);
    JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
    ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
    ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
    ds.addProperty(StatConstants.DS_KEY_EXTEND_SHOW_TYPE, extendShowType);
    ds.addProperty(StatConstants.DS_KEY_PLACE, place);
    StatEntity statEntity = StatEntity.Builder.aStatEntity()
        .withPid(stat.pid)
        .withEv(StatConstants.EV_ADVERT)
        .withDs(ds.toString())
        .withType(click ? StatConstants.TYPE_CLICK_C : StatConstants.TYPE_SHOW_E)
        .build();
    GVideoStatManager.getInstance().stat(statEntity);
  }
}
