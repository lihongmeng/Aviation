package com.jxntv.account.ui.profile;

import static com.jxntv.base.Constant.CROP;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentPreviewAvatarBinding;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.ui.crop.CropFragmentArgs;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.ImageDownloadLocalUtils;
import com.jxntv.dialog.GVideoBottomSheetItemDialog;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;


/**
 * 个人头像预览
 */
@SuppressWarnings("FieldCanBeLocal")
public final class PreviewAvatarFragment extends BaseFragment<FragmentPreviewAvatarBinding>
        implements GVideoBottomSheetItemDialog.OnItemSelectedListener {

    private ProfileViewModel mProfileViewModel;
    @Nullable
    private GVideoBottomSheetItemDialog mAvatarEntryDialog;
    private AuthorModel author;
    private ImageDownloadLocalUtils utils;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Nullable
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_avatar;
    }

    @Override
    protected void initView() {
        author = getActivity().getIntent().getExtras().getParcelable("author");
        PluginManager.get(AccountPlugin.class).addDestinations(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ImmersiveUtils.enterImmersiveFullTransparent(getActivity());

    }

    @Override
    protected void bindViewModels() {
        GVideoEventBus.get(CROP, Uri.class).observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                mProfileViewModel.uploadAvatar(getActivity(),uri);
            }
        });
        mProfileViewModel = bingViewModel(ProfileViewModel.class);
        mBinding.setViewModel(mProfileViewModel);
        mBinding.setAuthor(author);
        mProfileViewModel.getCropLiveData().observe(this, new NotNullObserver<Uri>() {
            @Override
            protected void onModelChanged(@NonNull Uri uri) {
                CropFragmentArgs args = new CropFragmentArgs.Builder(uri).build();
                Navigation.findNavController(mBinding.getRoot()).navigate(R.id.crop_nav_graph, args.toBundle());
            }
        });

        if (!TextUtils.isEmpty(author.getAvatar())) {
            ImageLoaderManager.loadHeadImage(mBinding.photoView,author.getAvatar());
        } else {
            mBinding.photoView.setImageResource(R.drawable.ic_default_avatar);
        }

        // 头像监听
        mProfileViewModel.getAvatarLiveData().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                showAvatarEntryDialog();
            }
        });

        mBinding.photoView.setOnLongClickListener(view -> {
            if (utils == null) {
                utils = new ImageDownloadLocalUtils(getContext(), author.getAvatar());
            }
            utils.showSaveDialog();
            return false;
        });

        mBinding.photoView.setOnClickListener(view -> {
            getActivity().finish();
        });

        mBinding.root.setOnClickListener(view -> {
            getActivity().finish();
        });

        GVideoEventBus.get(AccountPlugin.EVENT_AVATAR_UPDATE).observe(this, o -> getActivity().finish());

    }

    @Override
    protected void loadData() {
        String userId = PluginManager.get(AccountPlugin.class).getUserId();
        if (!TextUtils.isEmpty(userId) && userId.equals(author.getId())) {
            mBinding.modifyAvatar.setVisibility(View.VISIBLE);
            mProfileViewModel.loadData();
        } else {
            mBinding.modifyAvatar.setVisibility(View.GONE);
        }
    }


    private void showAvatarEntryDialog() {
        if (mAvatarEntryDialog == null) {
            mAvatarEntryDialog = new GVideoBottomSheetItemDialog.Builder(getContext())
                    .addItem(R.string.fragment_modify_avatar_entry_local)
                    .addItem(R.string.fragment_modify_avatar_entry_gallery)
                    .addItem(R.string.fragment_modify_avatar_entry_camera)
                    .cancel(R.string.dialog_back)
                    .itemSelectedListener(this)
                    .build();
        }
        if (!mAvatarEntryDialog.isShowing()) {
            mAvatarEntryDialog.show();
        }
    }

    @Override
    public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
        mProfileViewModel.onAvatarEntrySelected(mBinding.getRoot(), position);
    }

}