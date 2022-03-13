package com.jxntv.share.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.base.StaticParams;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.BitmapSaveLocalUtils;
import com.jxntv.base.utils.BitmapUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.player.MediaPlayManager;
import com.jxntv.share.R;
import com.jxntv.share.databinding.LayoutCreateBillBinding;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.ScreenUtils;
import com.jxntv.utils.SizeUtils;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoRImageView;
import com.jxntv.widget.GVideoTextView;

public class CreateBillDialog extends GVideoCenterDialog {

    private LayoutCreateBillBinding layoutCreateBillBinding;
    private Activity activity;

    public CreateBillDialog(Context context) {
        super(context);

        activity = (Activity) context;

        // TODO: 2022/1/12  
        // MediaPlayManager.getInstance().stop(StaticParams.currentTabId);

        // 先生成需要分享的图片
        Bitmap shareBitmap = createResultBitmap();

        // 创建弹窗的布局
        layoutCreateBillBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_create_bill,
                null,
                false
        );

        setContentView(layoutCreateBillBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // 将需要分享的图片加载到弹窗中
        Glide.with(context)
                .load(shareBitmap)
                .apply(new RequestOptions().transform(new RoundedCorners(SizeUtils.dp2px(20))))
                .into(layoutCreateBillBinding.billContent);

        layoutCreateBillBinding.close.setOnClickListener(v -> dismiss());

        layoutCreateBillBinding.button.setOnClickListener(v -> {

            new BitmapSaveLocalUtils(context, shareBitmap, new BitmapSaveLocalUtils.ResultListener() {
                @Override
                public void onSuccess() {
                    ToastUtils.showShort(ResourcesUtils.getString(R.string.save_success));
                    dismiss();
                }

                @Override
                public void onFailed() {
                    ToastUtils.showShort(ResourcesUtils.getString(R.string.save_failed));
                }
            }).saveImage();
        });

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        // TODO: 2022/1/12
        // setOnDismissListener(dialog -> MediaPlayManager.getInstance().tryStartPlay(StaticParams.currentTabId));
    }

    private Bitmap createResultBitmap() {
        View picLayout = LayoutInflater.from(activity).inflate(R.layout.layout_create_bill_result, null);
        ((GVideoImageView) picLayout.findViewById(R.id.bill_content)).setImageBitmap(ScreenUtils.takeScreenShot(activity));

        GVideoRImageView header = picLayout.findViewById(R.id.header);
        Bitmap bitmap = PluginManager.get(AccountPlugin.class).getHeaderImage();
        if (bitmap == null) {
            header.setImageResource(R.drawable.ic_default_avatar);
        } else {
            header.setImageBitmap(bitmap);
        }

        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        picLayout.findViewById(R.id.user_info_layout).setVisibility(
                accountPlugin.hasLoggedIn() ? View.VISIBLE : View.GONE
        );

        String userName = accountPlugin.getNickName();
        GVideoTextView name = picLayout.findViewById(R.id.name);
        name.setText((TextUtils.isEmpty(userName)) ? "" : userName);

        Bitmap result = BitmapUtils.getBitmap(
                activity,
                picLayout,
                ResourcesUtils.getIntDimens(R.dimen.DIMEN_360DP),
                ResourcesUtils.getIntDimens(R.dimen.DIMEN_640DP)
        );

        picLayout.destroyDrawingCache();
        picLayout.setDrawingCacheEnabled(false);
        return result;
    }

}
