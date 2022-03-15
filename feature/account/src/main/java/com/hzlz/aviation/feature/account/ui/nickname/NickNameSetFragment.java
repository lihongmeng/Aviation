package com.hzlz.aviation.feature.account.ui.nickname;

import static com.hzlz.aviation.kernel.base.Constant.CROP;
import static com.hzlz.aviation.kernel.base.Constant.SELECT_AVATAR;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentNickNameSetBinding;
import com.hzlz.aviation.feature.account.model.AvatarInfo;
import com.hzlz.aviation.feature.account.ui.crop.CropFragmentArgs;
import com.hzlz.aviation.feature.account.utils.HeaderUtils;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

import java.io.FileNotFoundException;

public class NickNameSetFragment extends BaseFragment<FragmentNickNameSetBinding> {

    private NickNameSetViewModel viewModel;

    private DefaultEnsureCancelDialog backTipDialog;

    @Override
    protected void initImmersive() {
        super.initImmersive();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nick_name_set;
    }

    @Override
    protected void initView() {

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mBinding.root.setPadding(0, WidgetUtils.getStatusBarHeight(),0,0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        PluginManager.get(AccountPlugin.class).addDestinations(this);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        dealBackPressed();
                    }
                }
        );

        mBinding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.currentNickName = TextUtils.isEmpty(s) ? "" : s.toString();
                viewModel.confirmEnable.set(
                        !TextUtils.isEmpty(viewModel.currentNickName) && viewModel.currentNickName.length() > 0);
                mBinding.nicknameNum.setText(s.toString().length()+"/12");
            }
        });
        mBinding.nicknameNum.setText("0/12");
        mBinding.back.setOnClickListener(v -> dealBackPressed());
        mBinding.root.setOnClickListener(v -> hideSoftInputNoToken());

    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(NickNameSetViewModel.class);
        mBinding.setViewModel(viewModel);

        // 登出
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
                this, o -> {
                    backTipDialog.dismiss();
                    PluginManager.get(AccountPlugin.class).startLoginActivity(getContext());
                    finishActivity();
                }
        );

        // 获取到裁剪的结果
        GVideoEventBus.get(CROP, Uri.class).observe(
                this,
                uri -> {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    viewModel.isSelectDefaultAvatar = false;
                    viewModel.mAvatarUri = uri;
                    try {
                        mBinding.avatar.setImageBitmap(BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    viewModel.picId = "";
                }
        );

        // 选择默认头像后的通知
        GVideoEventBus.get(SELECT_AVATAR, AvatarInfo.class).observe(
                this,
                avatarData -> {
                    viewModel.isSelectDefaultAvatar = true;
                    viewModel.picId = avatarData.getId();
                    viewModel.avatar.setValue(avatarData.getUrl());
                    viewModel.mAvatarUri = null;
                }
        );

        // 由ViewModel层发起的图片裁剪
        viewModel.cropImage.observe(
                this,
                cropImage -> {
                    CropFragmentArgs args = new CropFragmentArgs.Builder(viewModel.mAvatarUri).build();
                    Navigation.findNavController(mBinding.getRoot()).navigate(R.id.crop_nav_graph, args.toBundle());
                }
        );

        // 由ViewModel层通知的提交成功
        viewModel.submitSuccess.observe(
                this,
                cropImage -> {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    if (StaticParams.isForeLogin) {
                        StaticParams.isForeLogin = false;
                        PluginManager.get(HomePlugin.class).restartApp(activity);
                    } else {

                        // 准备一张头像，用于分享
                        HeaderUtils.getInstance().preHeaderImage(activity);
                        finishActivity();
                    }
                }
        );

        viewModel.avatar.observe(
                this,
                avatar -> {
                    if (avatar != null && !TextUtils.isEmpty(avatar)) {
                        ImageLoaderManager.loadHeadImage(mBinding.avatar, avatar);
                    } else {
                        mBinding.avatar.setImageResource(R.drawable.ic_default_avatar);
                    }
                }
        );

    }

    @Override
    protected void loadData() {
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    private void dealBackPressed() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (backTipDialog == null) {
            backTipDialog = new DefaultEnsureCancelDialog(activity);
        }
        backTipDialog.init(
                view -> {
                    backTipDialog.dismiss();
                },
                view -> {
                    PluginManager.get(AccountPlugin.class).logout();
                },
                "",
                getString(R.string.nick_name_modify_tip));
        backTipDialog.show();
    }

    @Override
    public String getPid() {
        return StatPid.NICKNAME_SET;
    }

}
