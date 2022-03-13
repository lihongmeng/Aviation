package com.jxntv.account.ui.profile;

import static com.jxntv.base.Constant.CROP;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentProfileBinding;
import com.jxntv.account.dialog.DatePickerBottomSheetDialog;
import com.jxntv.account.dialog.GenderBottomSheetDialog;
import com.jxntv.account.dialog.RegionPickerBottomSheetDialog;
import com.jxntv.account.model.RegionModel;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.account.ui.crop.CropFragmentArgs;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.dialog.GVideoBottomSheetItemDialog;
import com.jxntv.event.GVideoEventBus;
import java.util.Date;
import java.util.List;

/**
 * 个人中心界面
 *
 *
 * @since 2020-01-13 18:43
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ProfileFragment extends BaseFragment<FragmentProfileBinding>
    implements GenderBottomSheetDialog.OnGenderSelectedListener,
    DatePickerBottomSheetDialog.Listener, GVideoBottomSheetItemDialog.OnItemSelectedListener {
  //<editor-fold desc="属性">
  private ProfileViewModel mProfileViewModel;
  @Nullable
  private GVideoBottomSheetItemDialog mAvatarEntryDialog;
  @Nullable
  private GenderBottomSheetDialog mGenderDialog;
  @Nullable
  private RegionPickerBottomSheetDialog mRegionDialog;
  @Nullable
  private DatePickerBottomSheetDialog mBirthdayDialog;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_profile;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.fragment_profile_title);
    TextView descriptionTextView = mBinding.cellViewDescription.getRightTextTextView();
    if (descriptionTextView != null) {
      int paddingLeft = getResources().getDimensionPixelSize(
          R.dimen.fragment_profile_modify_description_text_padding_left);
      descriptionTextView.setPadding(paddingLeft, 0, 0, 0);
      descriptionTextView.setMaxLines(2);
      descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
    }
  }

  @Override
  protected void bindViewModels() {
    GVideoEventBus.get(CROP, Uri.class).observe(this, new Observer<Uri>() {
      @Override public void onChanged(Uri uri) {
        mProfileViewModel.uploadAvatar(getActivity(),uri);
      }
    });
    mProfileViewModel = bingViewModel(ProfileViewModel.class);
    mBinding.setViewModel(mProfileViewModel);
    mProfileViewModel.getCropLiveData().observe(this, new NotNullObserver<Uri>() {
      @Override protected void onModelChanged(@NonNull Uri uri) {
        CropFragmentArgs args = new CropFragmentArgs.Builder(uri).build();
        Navigation.findNavController(mBinding.getRoot()).navigate(R.id.crop_nav_graph, args.toBundle());
      }
    });
    // 用户监听
    mProfileViewModel.getUserLiveData().observe(this, new NotNullObserver<User>() {
      @Override
      protected void onModelChanged(@NonNull User user) {
        mBinding.setUser(user.getUserObservable());
      }
    });
    // 头像监听
    mProfileViewModel.getAvatarLiveData().observe(this, new Observer<Object>() {
      @Override public void onChanged(Object o) {
        showAvatarEntryDialog();
      }
    });
    // 性别监听
    mProfileViewModel.getGenderLiveData().observe(this, new NotNullObserver<Object>() {
      @Override
      protected void onModelChanged(@NonNull Object genderAndPrivacyRange) {
        Object[] temp = (Object[]) genderAndPrivacyRange;
        showGenderDialog((int) temp[0], (int) temp[1]);
      }
    });
    mProfileViewModel.getUpdateGenderDialogLiveData().observe(this, new NotNullObserver<Object>() {
      @Override
      protected void onModelChanged(@NonNull Object genderAndPrivacyRange) {
        Object[] temp = (Object[]) genderAndPrivacyRange;
        updateGenderDialog((int) temp[0], (int) temp[1]);
      }
    });
    // 生日监听
    mProfileViewModel.getBirthdayLiveData().observe(this, new NotNullObserver<Object>() {

      @Override
      protected void onModelChanged(@NonNull Object dateAndPrivacyRange) {
        Object[] temp = (Object[]) dateAndPrivacyRange;
        showBirthdayDialog((Date) temp[0], (int) temp[1]);
      }
    });
    mProfileViewModel.getUpdateBirthdayDialogLiveData()
        .observe(this, new NotNullObserver<Object>() {
          @Override
          protected void onModelChanged(@NonNull Object dateAndPrivacyRange) {
            Object[] temp = (Object[]) dateAndPrivacyRange;
            updateBirthdayDialog((Date) temp[0], (int) temp[1]);
          }
        });

    mProfileViewModel.mRegionLiveData.observe(this, new Observer<List<RegionModel>>() {
      @Override
      public void onChanged(List<RegionModel> regionModels) {
        showRegionDialog(regionModels);
      }
    });

  }

  @Override
  protected void loadData() {
    mProfileViewModel.loadData();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void showAvatarEntryDialog() {
    Context context = getContext();
    if (context == null) {
      return;
    }
    if (mAvatarEntryDialog == null) {
      mAvatarEntryDialog = new GVideoBottomSheetItemDialog.Builder(context)
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

  private void showGenderDialog(@Gender int gender, @PrivacyRange int privacyRange) {
    Context context = getContext();
    if (context == null) {
      return;
    }
    if (mGenderDialog == null) {
      mGenderDialog = new GenderBottomSheetDialog(context, gender, privacyRange);
      mGenderDialog.setOnGenderSelectedListener(this);
    }
    if (!mGenderDialog.isShowing()) {
      mGenderDialog.show();
    }
  }

  private void updateGenderDialog(@Gender int gender, @PrivacyRange int privacyRange) {
    if (mGenderDialog != null) {
      mGenderDialog.setGender(gender, privacyRange);
    }
  }

  private void showBirthdayDialog(@NonNull Date date, @PrivacyRange int privacyRange) {
    Context context = getContext();
    if (context == null) {
      return;
    }
    if (mBirthdayDialog == null) {
      mBirthdayDialog = new DatePickerBottomSheetDialog(context, date, privacyRange);
      mBirthdayDialog.setListener(this);
    }
    if (!mBirthdayDialog.isShowing()) {
      mBirthdayDialog.show();
    }
  }

  public void showRegionDialog(List<RegionModel> regionModels){
    if (mRegionDialog==null){
      mRegionDialog = new RegionPickerBottomSheetDialog(getContext());
      mRegionDialog.setData(regionModels, (provinceId, province, cityId, city) -> {
        mProfileViewModel.onRegionSelected(provinceId,province, cityId, city);
      });
    }
    mRegionDialog.show();
  }

  private void updateBirthdayDialog(@NonNull Date date, @PrivacyRange int privacyRange) {
    if (mBirthdayDialog != null) {
      mBirthdayDialog.setDate(date, privacyRange);
    }
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
    mProfileViewModel.onAvatarEntrySelected(mBinding.getRoot(), position);
  }

  @Override
  public void onSelected(
      @NonNull GenderBottomSheetDialog dialog,
      @Gender int gender,
      @PrivacyRange int privacyRange) {
    mProfileViewModel.onGenderSelected(gender, privacyRange);
  }

  @Override
  public void onDateAndPrivacySelected(
      @NonNull DatePickerBottomSheetDialog dialog,
      @NonNull Date date,
      @PrivacyRange int privacyRange) {
    mProfileViewModel.onBirthdaySelected(date, privacyRange);
  }
  //</editor-fold>
}