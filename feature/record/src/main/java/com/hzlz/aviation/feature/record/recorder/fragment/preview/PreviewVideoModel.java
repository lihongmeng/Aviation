package com.hzlz.aviation.feature.record.recorder.fragment.preview;

import static com.hzlz.aviation.feature.record.recorder.fragment.preview.PreviewVideoFragment.INTENT_PREVIEW_TYPE_NORMAL;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.kernel.base.BaseViewModel;

/**
 * 预览视频数据模型
 */
public class PreviewVideoModel extends BaseViewModel {

  /** 当前持有的video 数据模型 */
  private ImageVideoEntity mCurrentImageVideoEntity;
  /** 预览类型 */
  private String mPreviewType;

  /**
   * 构造函数
   */
  public PreviewVideoModel(@NonNull Application application) {
    super(application);
    mCurrentImageVideoEntity = VideoChooseHelper.getInstance().getPreviewVideoEntity();
  }

  /**
   * 更新预览类型
   *
   * @param type  预览类型
   */
  void updateType(String type) {
    mPreviewType = type;
  }

  /**
   * 当前预览视频是否选中
   *
   * @return 是否选择
   */
  public boolean isSelect() {
    return mCurrentImageVideoEntity.selectPosition > 0;
  }

  /**
   * 是否可以显示选择部分
   *
   * @return 是否可以显示选择部分
   */
  public boolean canShowSelect() {
    return !TextUtils.equals(mPreviewType, INTENT_PREVIEW_TYPE_NORMAL);
  }

  /**
   * 获取当前选择数目文字
   *
   * @return 当前选择数目文字
   */
  public String getSelectText() {
    return String.valueOf(mCurrentImageVideoEntity.selectPosition);
  }

  /**
   * 预览选择文字点击事件
   *
   * @param  v  点击的view
   */
  public void onPreviewSelectTextClick(View v) {
    if (isSelect()) {
      VideoChooseHelper.getInstance().removeVideoEntity(mCurrentImageVideoEntity);
    } else {
      VideoChooseHelper.getInstance().addVideoEntity(mCurrentImageVideoEntity);
    }
    Navigation.findNavController(v).popBackStack();
  }

  /**
   * 选择物料文字点击事件（当前选中）
   *
   * @param  v  点击的view
   */
  public void onChooseItemTextClick(View v) {
    VideoChooseHelper.getInstance().removeVideoEntity(mCurrentImageVideoEntity);
    Navigation.findNavController(v).popBackStack();
  }

  /**
   * 预览选择图片点击事件（当前未选择）
   *
   * @param  v  点击的view
   */
  public void onChooseItemImgClick(View v) {
    VideoChooseHelper.getInstance().addVideoEntity(mCurrentImageVideoEntity);
    Navigation.findNavController(v).popBackStack();
  }

  /**
   * 预览取消点击事件
   *
   * @param  v  点击的view
   */
  public void onPreviewBackCancel(View v) {
    Navigation.findNavController(v).popBackStack();
  }
}
