package com.hzlz.aviation.kernel.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.hzlz.aviation.kernel.base.dialog.DefaultNetworkDialog;
import com.hzlz.aviation.kernel.base.dialog.INetworkDialog;
import com.hzlz.aviation.kernel.base.model.NetworkDialogModel;
import com.hzlz.aviation.kernel.base.model.PlaceholderModel;
import com.hzlz.aviation.kernel.base.model.StartActivityForResultModel;
import com.hzlz.aviation.kernel.base.model.ToastModel;
import com.hzlz.aviation.kernel.base.placeholder.DefaultPlaceholderLayout;
import com.hzlz.aviation.kernel.base.placeholder.IPlaceholderLayout;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderListener;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.toolbar.DefaultToolbar;
import com.hzlz.aviation.kernel.base.toolbar.IToolbar;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarAbility;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarListener;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarUtils;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity ??????
 *
 *
 * @since 2020-01-06 21:32
 */
public abstract class BaseActivity<VDB extends ViewDataBinding> extends AppCompatActivity
    implements PlaceholderListener, ToolbarAbility, ToolbarListener {
  //<editor-fold desc="??????">
  // ??????????????????
  protected VDB mBinding;
  // ViewModel
  @Nullable
  private BaseViewModel mViewModel;
  @Nullable
  private IToolbar mToolbar;
  // ????????????
  @Nullable
  private IPlaceholderLayout mPlaceholderLayout;
  // ????????????
  @Nullable
  private INetworkDialog mNetworkDialog;
  @NonNull
  protected Handler mHandler = new SafeHandler(this);

  // Activity???????????????Fragment???PageName??????Pid
  private String currentFragmentPageName;

  //</editor-fold>

  //<editor-fold desc="????????????">

  /**
   * ?????????????????? id
   *
   * @return ???????????? id
   */
  @LayoutRes
  protected abstract int getLayoutId();

  /**
   * ???????????????
   */
  protected abstract void initView();

  /**
   * ??????????????????????????? ViewModel
   */
  protected abstract void bindViewModels();

  /**
   * ????????????
   */
  protected abstract void loadData();
  //</editor-fold>

  //<editor-fold desc="????????????">

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int layoutId = getLayoutId();
    if (layoutId != 0) {
      mBinding = DataBindingUtil.setContentView(this, layoutId);
      initToolbar();
      initView();
      bindViewModels();
      loadData();
    }
  }

  @Override
  protected void onDestroy() {
    if (mNetworkDialog != null) {
      mNetworkDialog.onDestroy();
    }
    if (mPlaceholderLayout != null) {
      mPlaceholderLayout.onDestroy();
    }
    mBackPressHandlerList.clear();
    super.onDestroy();
  }
  //</editor-fold>

  //<editor-fold desc="ViewModel????????????">

  /**
   * ?????? ViewModel
   *
   * @param viewModelClass ViewModel Class ???
   * @param <VM> ????????? ViewModel ??????
   * @return ????????? ViewModel
   */
  @SuppressWarnings({ "SameParameterValue", "unchecked" })
  protected <VM extends BaseViewModel> VM bingViewModel(@NonNull Class<VM> viewModelClass) {
    mViewModel = ViewModelProviders.of(this).get(viewModelClass);
    // ??????
    mViewModel.getToastLiveData().observe(this, new NotNullObserver<ToastModel>() {
      @Override
      protected void onModelChanged(@NonNull ToastModel toastModel) {
        if (toastModel.getText() != null) {
          showToast(toastModel.getText());
        } else {
          showToast(toastModel.getStringResourceId(), toastModel.getArguments());
        }
      }
    });
    // ????????????
    mViewModel.getPlaceholderLiveData().observe(this, new NotNullObserver<PlaceholderModel>() {
      @Override
      protected void onModelChanged(@NonNull PlaceholderModel placeholderModel) {
        updatePlaceholderLayoutType(placeholderModel.getType());
      }
    });
    // ????????????
    mViewModel.getNetworkDialogLiveData().observe(this, new NotNullObserver<NetworkDialogModel>() {
      @Override
      protected void onModelChanged(@NonNull NetworkDialogModel networkDialogModel) {
        if (networkDialogModel.isShow()) {
          Integer textResId = networkDialogModel.getTextResId();
          if (textResId != null) {
            showNetworkDialog(textResId);
          } else {
            showNetworkDialog(networkDialogModel.getText());
          }
        } else {
          hideNetworkDialog();
        }
      }
    });
    // StartActivityForResult
    mViewModel.getStartActivityForResultModelLiveData().observe(
        this,
        new NotNullObserver<StartActivityForResultModel>() {
          @Override
          protected void onModelChanged(@NonNull StartActivityForResultModel model) {
            startActivityForResult(model.getIntent(), model.getRequestCode());
          }
        });
    return (VM) mViewModel;
  }

  /**
   * Model ?????? Null ??? Observer ?????????
   *
   * @param <M> ????????????
   */
  protected abstract class NotNullObserver<M> implements Observer<M> {

    //<editor-fold desc="????????????">

    /**
     * ????????????????????????
     *
     * @param m ??????
     */
    protected abstract void onModelChanged(@NonNull M m);
    //</editor-fold>

    //<editor-fold desc="????????????">
    @Override
    public final void onChanged(M m) {
      if (m != null) {
        onModelChanged(m);
      }
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="Toolbar">

  /**
   * ???????????? Toolbar, ????????????
   *
   * @return true : ?????????false : ?????????
   */
  protected boolean showToolbar() {
    return true;
  }

  @NonNull
  protected IToolbar getToolbar() {
    if (mToolbar == null) {
      mToolbar = new DefaultToolbar();
    }
    return mToolbar;
  }

  /**
   * ????????? toolbar
   */
  private void initToolbar() {
    if (showToolbar()) {
      ToolbarUtils.addToolbar(mBinding.getRoot(), getToolbar());
      setToolbarListener(this);
    }
  }

  @Override
  public void setToolbarBackgroundColor(@ColorInt int color) {
    if (mToolbar != null) {
      mToolbar.setToolbarBackgroundColor(color);
    }
  }

  @Override
  public void setToolbarTitle(@StringRes int resId, Object... args) {
    if (mToolbar != null) {
      mToolbar.setToolbarTitle(resId, args);
    }
  }

  @Override
  public void setToolbarTitle(@NonNull String text) {
    if (mToolbar != null) {
      mToolbar.setToolbarTitle(text);
    }
  }

  @Override
  public String getToolbarTitle() {
    if (mToolbar != null){
      return mToolbar.getToolbarTitle();
    }
    return null;
  }


  @Override
  public void setLeftBackText(@StringRes int resId, Object... args) {
    if (mToolbar != null) {
      mToolbar.setLeftBackText(resId, args);
    }
  }

  @Override
  public void setLeftBackText(@NonNull String text) {
    if (mToolbar != null) {
      mToolbar.setLeftBackText(text);
    }
  }

  @Override
  public void showRightOperationTextView(boolean show) {
    if (mToolbar != null) {
      mToolbar.showRightOperationTextView(show);
    }
  }

  @Override
  public void enableRightOperationTextView(boolean enable) {
    if (mToolbar != null) {
      mToolbar.enableRightOperationTextView(enable);
    }
  }

  @Override
  public void setRightOperationTextViewText(@StringRes int resId, Object... args) {
    if (mToolbar != null) {
      mToolbar.setRightOperationTextViewText(resId, args);
    }
  }

  @Override
  public void setRightOperationTextViewText(@NonNull String text) {
    if (mToolbar != null) {
      mToolbar.setRightOperationTextViewText(text);
    }
  }

  @Override
  public void setToolbarListener(@NonNull ToolbarListener listener) {
    if (mToolbar != null) {
      mToolbar.setToolbarListener(listener);
    }
  }

  @Override
  public void onLeftBackPressed(@NonNull View view) {
    if (!Navigation.findNavController(view).popBackStack()){
      finish();
    }
  }

  @Override
  public void onRightOperationPressed(@NonNull View view) {

  }

  @Override
  public void showRightOperationImageView(boolean show){
    if (mToolbar != null) {
      mToolbar.showRightOperationImageView(show);
    }
  }

  @Override
  public void enableRightOperationImageView(boolean enable){
    if (mToolbar != null) {
      mToolbar.enableRightOperationImageView(enable);
    }
  }

  @Override
  public void setRightOperationImage(@DrawableRes int resId){
    if (mToolbar != null) {
      mToolbar.setRightOperationImage(resId);
    }
  }


  //</editor-fold>

  //<editor-fold desc="????????????">

  /**
   * ??????????????????
   *
   * @return ???????????? {@link IPlaceholderLayout}
   */
  @Nullable
  protected IPlaceholderLayout getPlaceholderLayout() {
    return new DefaultPlaceholderLayout();
  }

  /**
   * ???????????????????????????????????????????????????????????? android.R.id.content
   */
  protected void setupPlaceholderLayout() {
    setupPlaceholderLayout(android.R.id.content);
  }

  /**
   * ??????????????????
   *
   * @param layoutId ??????????????????????????? id
   */
  @SuppressWarnings("SameParameterValue")
  protected void setupPlaceholderLayout(@IdRes int layoutId) {
    setupPlaceholderLayout(android.R.id.content, 0);
  }

  /**
   * ??????????????????
   *
   * @param layoutId ??????????????????????????? id
   * @param marginTopInDp ???????????????????????????????????????????????? MarginTop (?????? dp)
   */
  @SuppressWarnings("SameParameterValue")
  protected void setupPlaceholderLayout(@IdRes int layoutId, int marginTopInDp) {
    if (mPlaceholderLayout != null) {
      throw new IllegalStateException("cannot setup placeholder fragment_entry twice!!!");
    }
    mPlaceholderLayout = getPlaceholderLayout();
    if (mPlaceholderLayout == null) {
      return;
    }
    mPlaceholderLayout.setPlaceholderListener(this);
    ViewGroup layout = findViewById(layoutId);
    int index = -1;
    ViewGroup.MarginLayoutParams params = null;
    if (layout instanceof RelativeLayout) {
      params = new RelativeLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
      );
    } else if (layout instanceof FrameLayout) {
      params = new FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
      );
    } else if (layout instanceof LinearLayout) {
      params = new LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
      );
      if (layout.getChildCount() > 0) {
        index = 0;
      }
    } else if (layout instanceof ConstraintLayout) {
      ConstraintLayout.LayoutParams cp = new ConstraintLayout.LayoutParams(0, 0);
      cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
      cp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
      cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
      cp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
      params = cp;
    }
    if (params != null) {
      params.topMargin = marginTopInDp;
      layout.addView(mPlaceholderLayout.getView(this), index);
    }
  }

  /**
   * ????????????????????????
   *
   * @param type ?????????????????? {@link PlaceholderType}
   */
  protected void updatePlaceholderLayoutType(@PlaceholderType int type) {
    if (mPlaceholderLayout != null) {
      mPlaceholderLayout.updateType(type, false, 0, false);
    }
  }

  @Override
  public void onReload(@NonNull View view) {

  }

  @Override
  public void onLogin(@NonNull View view) {

  }
  //</editor-fold>

  //<editor-fold desc="??????">

  /**
   * ????????????
   *
   * @param stringResourceId ??????????????? id
   * @param arguments ?????????????????????
   */
  protected void showToast(@StringRes int stringResourceId, @Nullable Object... arguments) {
    try {
      showToast(getString(stringResourceId, arguments));
    } catch (Resources.NotFoundException ignore) {
    }
  }

  /**
   * ????????????
   *
   * @param text ????????????
   */
  protected void showToast(@NonNull String text) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }
  //</editor-fold>

  //<editor-fold desc="????????????">

  /**
   * ??????????????????
   *
   * @return ???????????? {@link INetworkDialog}
   */
  @Nullable
  protected INetworkDialog getNetworkDialog() {
    return new DefaultNetworkDialog(this);
  }

  /**
   * ??????????????????
   *
   * @param stringResourceId ??????????????? id
   */
  protected void showNetworkDialog(@StringRes int stringResourceId) {
    if (mNetworkDialog == null) {
      mNetworkDialog = getNetworkDialog();
    }
    if (mNetworkDialog != null) {
      mNetworkDialog.show(stringResourceId);
    }
  }

  /**
   * ??????????????????
   *
   * @param text ?????????
   */
  protected void showNetworkDialog(@Nullable String text) {
    if (mNetworkDialog == null) {
      mNetworkDialog = getNetworkDialog();
    }
    if (mNetworkDialog != null) {
      mNetworkDialog.show(text);
    }
  }

  /**
   * ??????????????????
   */
  protected void hideNetworkDialog() {
    if (mNetworkDialog != null) {
      mNetworkDialog.hideDialog();
    }
  }
  //</editor-fold>

  //<editor-fold desc="ActivityResult">

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (mViewModel != null) {
      mViewModel.onActivityResult(requestCode, resultCode, data);
    }
    PluginManager.get(SharePlugin.class).doShareResultIntent(requestCode,resultCode,data);
  }

  //<editor-fold desc="??????">

  /**
   * ???????????????
   *
   * @param view ?????????????????????
   */
  protected void openSoftKeyBoard(@NonNull View view) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      view.setFocusable(true);
      view.setFocusableInTouchMode(true);
      view.requestFocus();
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
      if (view instanceof EditText) {
        ((EditText) view).setSelection(((EditText) view).getText().length());
      }
    }
  }

  /**
   * ?????????????????????
   *
   * @param view ?????????????????????
   */
  protected void openSoftKeyBoardDelay(@NonNull View view) {
    mHandler.postDelayed(() -> openSoftKeyBoard(view), 200);
  }

  /**
   * ???????????????
   *
   * @param view ???????????????
   */
  protected void closeSoftKeyboard(@NonNull View view) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
      view.clearFocus();
    }
  }
  //</editor-fold>

  /**
   * ??????Back??????
   */
  public void registerBackPressHandler(BackPressHandler backPressHandler) {
    if (!mBackPressHandlerList.contains(backPressHandler)) {
      mBackPressHandlerList.add(backPressHandler);
    }
  }

  /**
   * ??????Back??????
   */
  public void unregisterBackPressHandler(BackPressHandler backPressHandler) {
    if (mBackPressHandlerList.contains(backPressHandler)) {
      mBackPressHandlerList.remove(backPressHandler);
    }
  }

  private List<BackPressHandler> mBackPressHandlerList = new ArrayList<>();

  @Override public void onBackPressed() {
    //????????????Back?????????????????????????????????????????????????????????
    if (mBackPressHandlerList != null && !mBackPressHandlerList.isEmpty()) {
      for (BackPressHandler backPressHandler : mBackPressHandlerList) {
        if (backPressHandler.onBackPressed()) {
          return;
        }
      }
    }
    super.onBackPressed();
  }

  public String getCurrentFragmentPageName() {
    return currentFragmentPageName;
  }

  public void setCurrentFragmentPageName(String currentFragmentPageName) {
    this.currentFragmentPageName = currentFragmentPageName;
    Log.d("setCurrentFragmentName","currentFragmentName -->> "+ currentFragmentPageName);
  }

  public String getStringDataFromBundle(String key) {
    if (key == null) {
      return "";
    }
    Intent intent = getIntent();
    if (intent == null) {
      return "";
    }
    return intent.getStringExtra(key);
  }

  public boolean getBooleanDataFromBundle(String key) {
    if (key == null) {
      return false;
    }
    Intent intent = getIntent();
    if (intent == null) {
      return false;
    }
    return intent.getBooleanExtra(key,false);
  }

}