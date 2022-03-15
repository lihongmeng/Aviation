package com.hzlz.aviation.feature.live.ui.author;


import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.databinding.FragmentAuthorPrepareBinding;
import com.hzlz.aviation.feature.live.model.PlatformMessageModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.utils.BitmapUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc : 直播准备界面
 */
public class AuthorPrepareFragment extends BaseFragment<FragmentAuthorPrepareBinding> {

    // AuthorPrepareViewModel
    private AuthorPrepareViewModel viewModel;

    // 选择入驻号的适配器
    private ArrayAdapter<String> spinnerAdapter;

    // 图片选择方式的弹窗
    private GVideoBottomSheetItemDialog pictureSelectMethodDialog;

    // 选择直播类型的弹窗
    private GVideoBottomSheetItemDialog selectLiveTypeDialog;

    @Override
    protected boolean isFullTransparent() {
        return false;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_author_prepare;
    }

    @Override
    protected void initView() {
        setToolbarTitle("发起直播");
    }

    @Override
    protected void bindViewModels() {

        viewModel = bingViewModel(AuthorPrepareViewModel.class);
        mBinding.setViewModel(viewModel);

        // 账号所属入驻号
        viewModel.platformList.observe(this, platformMessages -> {
            List<String> nameList = new ArrayList<>();
            for (PlatformMessageModel message : platformMessages) {
                nameList.add(message.getName());
            }
            spinnerAdapter.addAll(nameList);
            spinnerAdapter.notifyDataSetChanged();
        });

        // 照片选择方式弹窗
        viewModel.pictureSelectMethodDialog.observe(this, o -> {
            showPictureSelectMethodDialog();
        });

        // 照片选择方式弹窗
        viewModel.selectLiveTypeDialog.observe(this, o -> {
            showSelectLiveTypeDialog();
        });

        // 裁剪图片结果监听
        viewModel.cropUri.observe(this, uri -> {
            LogUtils.d("图片地址：" + uri.toString());
            viewModel.cropThumbUri.set(uri);
            //清理缓存
//            mBinding.liveThumb.setImageURI(null);
//            mBinding.liveThumb.setImageURI(uri);
            mBinding.liveThumb.setImageBitmap(null);
            mBinding.liveThumb.setImageBitmap(BitmapUtils.getBitmapFormPath(uri));
        });

        GVideoEventBus.get(Constants.EVENT_CROP, Uri.class).observe(this, uri -> {
            LogUtils.d("图片裁切成功：" + uri.toString());
        });

        mBinding.spinnerPlatform.setOnItemSelectedListener(viewModel.getItemSelectedClickListener());

    }

    @Override
    protected void loadData() {
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinnerPlatform.setAdapter(spinnerAdapter);
        viewModel.init();
    }

    private void showPictureSelectMethodDialog() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        if (pictureSelectMethodDialog == null) {
            pictureSelectMethodDialog = new GVideoBottomSheetItemDialog.Builder(context)
                    .addItem(R.string.gallery)
                    .addItem(R.string.camera)
                    .cancel(R.string.dialog_back)
                    .itemSelectedListener((dialog, position) -> {
                        if (position == 0) {
                            viewModel.selectPictureFromGallery();
                        } else if (position == 1) {
                            viewModel.doTakePicture(getContext());
                        } else {
                            pictureSelectMethodDialog.dismiss();
                        }
                    })
                    .build();
        }
        if (!pictureSelectMethodDialog.isShowing()) {
            pictureSelectMethodDialog.show();
        }
    }

    private void showSelectLiveTypeDialog() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        if (selectLiveTypeDialog == null) {
            selectLiveTypeDialog = new GVideoBottomSheetItemDialog.Builder(context)
                    .addItem(R.string.video_live)
                    .addItem(R.string.sound_live)
                    .cancel(R.string.dialog_back)
                    .itemSelectedListener((dialog, position) -> {
                        if (position == 0) {
                            viewModel.liveType = Constant.LIVE_ROOM_TYPE.VIDEO;
                            viewModel.liveTypeName.set(ResourcesUtils.getString(R.string.video_live));
                        } else if (position == 1) {
                            viewModel.liveType = Constant.LIVE_ROOM_TYPE.SOUND;
                            viewModel.liveTypeName.set(ResourcesUtils.getString(R.string.sound_live));
                        } else {
                            selectLiveTypeDialog.dismiss();
                        }
                    })
                    .build();
        }
        if (!selectLiveTypeDialog.isShowing()) {
            selectLiveTypeDialog.show();
        }
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        finishActivity();
    }

    @Override
    public String getPid() {
        return StatPid.LIVE_PREPARE;
    }

}