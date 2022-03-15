package com.hzlz.aviation.kernel.base.dialog;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.SP_KEY.ALREADY_SHOW_COUNT;
import static com.hzlz.aviation.kernel.base.Constant.SP_KEY.LAST_SHOW_TIME;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.DialogHomeBusinessBinding;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.StatPlugin;
import com.hzlz.aviation.kernel.base.sharedprefs.KernelSharedPrefs;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.DeviceId;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoLimitWHCenterDialog;

public class BusinessDialog extends GVideoLimitWHCenterDialog {

    private Context context;
    private String pid;
    private String imageUrl;
    private BannerModel.ImagesBean imagesBean;
    private DialogHomeBusinessBinding binding;
    private Circle circle;

    public BusinessDialog(
            Context context,
            BannerModel.ImagesBean imagesBean,
            String pid,
            Circle circle
    ) {
        super(context);
        this.context = context;
        this.circle = circle;
        this.imagesBean = imagesBean;
        this.pid = pid;

        float widthHeightRatio;
        if (imagesBean == null) {
            widthHeightRatio = 0;
        } else {
            this.imageUrl = imagesBean.getUrl();
            BannerModel.Image image = imagesBean.getImage();
            widthHeightRatio = image.getWidthHeightRatio();
        }
        mExpectWindowWidth = (int) (ScreenUtils.getScreenWidth(context) - ResourcesUtils.getDimens(R.dimen.DIMEN_80DP));
        if (widthHeightRatio == 0) {
            mExpectWindowHeight = (int) (mExpectWindowWidth * 16L / 9L);
        } else {
            mExpectWindowHeight = (int) (mExpectWindowWidth / widthHeightRatio);
        }
        // 给26dp的关闭按钮留位置
        mExpectWindowWidth += ResourcesUtils.getDimens(R.dimen.DIMEN_26DP);
        mExpectWindowHeight += ResourcesUtils.getDimens(R.dimen.DIMEN_26DP);

        View rootView = onCreateView(LayoutInflater.from(context), null, null);
        setContentView(rootView);

        setOnDismissListener(dialog -> PluginManager.get(StatPlugin.class).dialogClose(circle));
    }

    private View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_home_business,
                container,
                false
        );
        View root = binding.getRoot();
        binding.close.setOnClickListener(v -> {
            dismiss();
        });
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().transform(
                            new CenterCrop(),
                            new RoundedCorners(SizeUtils.dp2px(8))
                    )).into(binding.content);
        }
        binding.content.setOnClickListener(v -> {
            PluginManager.get(StatPlugin.class).dialogClick(circle);
            if (imagesBean == null) {
                dismiss();
                return;
            }
            int mediaType = imagesBean.getMediaType();
            if (mediaType == MediaType.QA_LIST_GROUP) {
                try {
                    Circle circle = new Circle();
                    circle.setJoin(imagesBean.isJoined());
                    circle.setGroupId(Long.parseLong(imagesBean.getMediaId()));
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(CIRCLE, circle);
                    PluginManager.get(CirclePlugin.class).startQAGroupActivity(binding.content, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                PluginManager.get(DetailPagePlugin.class).dispatchToDetail(
                        context,
                        new VideoModel(imagesBean),
                        null
                );
            }
            dismiss();
        });
        return root;
    }

    public void showImagesBeanData(BannerModel.ImagesBean imagesBean) {
        if (imagesBean == null) {
            return;
        }
        KernelSharedPrefs kernelSharedPrefs = KernelSharedPrefs.getInstance();
        String deviceId = DeviceId.get();
        deviceId = TextUtils.isEmpty(deviceId) ? "" : deviceId;

        // Banner Id
        int bannerId = imagesBean.getBannerId();

        // 服务端设置的弹窗最大展示次数
        int popTime = imagesBean.getPopTime();

        long lastShowTime = kernelSharedPrefs.getLong(
                LAST_SHOW_TIME + "_" + bannerId + "_" + deviceId, 0);

        // 如果本地保存的时间转换成 年-月-日
        // 不是今天,那就清除本地的数据，将服务端的数据保存起来
        if (!DateUtils.isCurrentDay(lastShowTime)) {
            showHomeBusinessDialog();
            saveDataToSp(deviceId, bannerId, popTime, 1);
            return;
        }

        int alreadyShowCount = kernelSharedPrefs.getInt(
                ALREADY_SHOW_COUNT + "_" + bannerId + "_" + deviceId, 0);

        // 如果本地已播放的次数小于服务端配置的最大弹出次数
        // 展示弹窗并保存
        if (alreadyShowCount < popTime) {
            showHomeBusinessDialog();
            saveDataToSp(deviceId, bannerId, popTime, alreadyShowCount + 1);
        }
    }

    private void saveDataToSp(String deviceId, int bannerId, int maxShowCount, int alreadyShowCount) {
        KernelSharedPrefs kernelSharedPrefs = KernelSharedPrefs.getInstance();
        kernelSharedPrefs.putLong(LAST_SHOW_TIME + "_" + bannerId + "_" + deviceId, System.currentTimeMillis());
        kernelSharedPrefs.putInt(ALREADY_SHOW_COUNT + "_" + bannerId + "_" + deviceId, Math.max(alreadyShowCount, 0));
    }

    private void showHomeBusinessDialog() {
        PluginManager.get(StatPlugin.class).dialogShow(circle);
        show();
    }


}
