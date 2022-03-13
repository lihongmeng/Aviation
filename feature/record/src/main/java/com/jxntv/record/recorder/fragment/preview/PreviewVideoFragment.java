package com.jxntv.record.recorder.fragment.preview;

import android.net.Uri;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.jxntv.base.BaseFragment;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.record.R;
import com.jxntv.record.databinding.FragmentPreviewBinding;
import com.jxntv.record.recorder.GVideoRecordController;
import com.jxntv.record.recorder.data.ImageVideoEntity;
import com.jxntv.record.recorder.helper.VideoChooseHelper;
import com.jxntv.utils.ResourcesUtils;

import java.io.File;

import static com.jxntv.android.liteav.LiteavConstants.TITLE_MODE_NONE;

/**
 * 预览视频、图片页面
 */
public class PreviewVideoFragment extends BaseFragment<FragmentPreviewBinding> {

  /** 录制 intent type */
  public static final String INTENT_PREVIEW_TYPE = "preview_type";
  public static final String INTENT_PREVIEW_TYPE_CHOOSE = "preview_type_choose";
  public static final String INTENT_PREVIEW_TYPE_NORMAL = "preview_type_normal";

  /** 当前fragment持有的view model */
  private PreviewVideoModel mPreviewViewModel;
  /** 当前持有的video 数据模型 */
  private ImageVideoEntity mCurrentImageVideoEntity;

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_preview;
  }

  @Override
  protected boolean enableImmersive() {
    return true;
  }

  @Override
  public int statusBarColor() {
    return ResourcesUtils.getColor(R.color.c_bg02);
  }

  @Override public boolean darkImmersive() {
    return false;
  }

  @Override
  protected void initView() {
    mCurrentImageVideoEntity = VideoChooseHelper.getInstance().getPreviewVideoEntity();
    NavController controller = Navigation.findNavController(mBinding.getRoot());
    if (mCurrentImageVideoEntity == null) {
      controller.popBackStack();
    }
    mBinding.playerView.setKSVideoPlayerListener(new GVideoPlayerListener() {
      @Override
      public void onPlayStateChanged(boolean isPlaying) {
        if (isPlaying) {
          mBinding.videoBigImg.setVisibility(View.GONE);
          return;
        }
        mBinding.videoBigImg.setVisibility(View.VISIBLE);
      }
    });
    mBinding.playerView.setMediaController(new GVideoRecordController(requireContext()));
    mBinding.playerView.setCanFullscreen(false);
  }

  /**
   * 视频广告显示
   */
  private void handleVideoShow() {
    mBinding.playerView.setTitleModeInWindowMode(TITLE_MODE_NONE);
    mBinding.playerView.setVisibility(View.VISIBLE);
    mBinding.playerView.setCanResize(false);
    mBinding.playerView.startPlay(mCurrentImageVideoEntity.path);
    mBinding.playerView.setLoop(true);
    mBinding.playerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
  }

  @Override
  protected void bindViewModels() {
    mPreviewViewModel = bingViewModel(PreviewVideoModel.class);
    mBinding.setViewModel(mPreviewViewModel);
  }

  @Override
  protected void loadData() {
    String type = getArguments() != null ? getArguments().getString(INTENT_PREVIEW_TYPE) : null;
    mPreviewViewModel.updateType(type);
    if (mCurrentImageVideoEntity.isVideo){
      mBinding.playerView.setVisibility(View.VISIBLE);
      mBinding.imageView.setVisibility(View.GONE);
      handleVideoShow();
    }else {
      mBinding.playerView.setVisibility(View.GONE);
      mBinding.imageView.setVisibility(View.VISIBLE);
      ImageLoaderManager.loadImage(mBinding.imageView,mCurrentImageVideoEntity.path,false);
    }

  }

  @Override
  public void onDestroy() {
    mBinding.playerView.release();
    super.onDestroy();
  }
}
