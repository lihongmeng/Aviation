package com.hzlz.aviation.feature.record.recorder.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.record.RecordActivity;
import com.hzlz.aviation.feature.record.RecordPluginImpl;
import com.hzlz.aviation.feature.record.recorder.fragment.publish.PublishHelper;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.record.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 录制弹窗
 */
@SuppressWarnings("ResultOfMethodCallIgnored,CheckResult")
public class RecordDialog extends GVideoBottomSheetDialog {

    // LayoutRecordDialogBinding
    private final LayoutRecordDialogBinding mLayoutBinding;

    // 上层传递进来的数据
    private Bundle bundle;

    public RecordDialog(Context context, Bundle bundle) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_record_dialog, null, false);
        this.bundle=bundle;
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        init();
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.fragment_record_dialog_height)
                )
        );
    }

    /**
     * 初始化内部按钮
     */
    private void init() {
        Observable<String> getHasLivePermissionObservable
                = PluginManager.get(LivePlugin.class).getHasLivePermission();
        if (getHasLivePermissionObservable == null) {
            return;
        }
        getHasLivePermissionObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(errorMessage -> {
                    if (!TextUtils.isEmpty(errorMessage)) {
                        setLiveImgDisable();
                    }
                });

        mLayoutBinding.recordImg.setOnClickListener(v -> {
            Context context = v.getContext();
            if (context == null) {
                return;
            }
            dismiss();
            if(PublishHelper.get().isPublishing()){
                ToastUtils.showLong(R.string.publishing_please_post_later);
            }else {
                Intent intent = new Intent(context, RecordActivity.class);
                if(bundle==null){
                    bundle = new Bundle();
                }
                bundle.putInt(RecordPluginImpl.INTENT_RECORD_TYPE, R.id.publishFragment);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        mLayoutBinding.liveImg.setOnClickListener(v -> {
            hasLivePermission();
        });

        mLayoutBinding.recordCancel.setOnClickListener(v -> {
            Context context = v.getContext();
            if (context == null) {
                return;
            }
            dismiss();
        });

    }

    private void hasLivePermission() {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        Observable<String> getHasLivePermissionObservable
                = PluginManager.get(LivePlugin.class).getHasLivePermission();
        if (getHasLivePermissionObservable == null) {
            return;
        }
        getHasLivePermissionObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(errorMessage -> {
                    if (TextUtils.isEmpty(errorMessage)) {

                        //暂时无需实名认证
                        // if (PluginManager.get(AccountPlugin.class).getAuditStatus() == AccountPlugin.VERIFICATION_STATUS_VERIFIED) {
                        PluginManager.get(LivePlugin.class).startAuthorPrepareActivity(context);
                        RecordDialog.this.dismiss();
                        // } else {
                        //     IdentificationConfirmDialog dialog = new IdentificationConfirmDialog(context);
                        //     dialog.setConfirmDialog(R.string.live_identify_text);
                        //     dialog.init(view -> {
                        //         dialog.dismiss();
                        //     }, view -> {
                        //         dialog.dismiss();
                        //         RecordDialog.this.dismiss();
                        //         PluginManager.get(AccountPlugin.class).startAccountSecurityFragment(RecordDialog.this.view);
                        //     });
                        //     dialog.show();
                        // }
                    } else {
                        setLiveImgDisable();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setLiveImgDisable() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mLayoutBinding.liveImg.setImageResource(R.drawable.ic_dialog_live_disable);
        mLayoutBinding.liveImg.setColorFilter(filter);
        mLayoutBinding.liveImg.setImageAlpha(80);
        mLayoutBinding.liveText.setTextColor(ResourcesUtils.getColor(R.color.t_color03));
    }
}
