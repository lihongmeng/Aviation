package com.hzlz.aviation.feature.share.model;

import android.view.View;

import androidx.annotation.DrawableRes;

import com.hzlz.aviation.feature.share.strategy.ShareStrategy;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;

/**
 * 分享数据
 */
public class ShareItemModel {

  /** 标题 */
  public String title;
  /** 图片res */
  public @DrawableRes int drawableRes;
  /** 点击事件 */
  public View.OnClickListener clickListener;
  /** 分享数据模型 */
  public ShareDataModel shareDataModel;
  /** 分享策略类 */
  public ShareStrategy shareStrategy;
}
