package com.hzlz.aviation.kernel.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
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
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.hzlz.aviation.kernel.base.dialog.DefaultNetworkDialog;
import com.hzlz.aviation.kernel.base.dialog.INetworkDialog;
import com.hzlz.aviation.kernel.base.entity.ExitPageData;
import com.hzlz.aviation.kernel.base.immersive.Immersive;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.NetworkDialogModel;
import com.hzlz.aviation.kernel.base.model.PlaceholderModel;
import com.hzlz.aviation.kernel.base.model.StartActivityForResultModel;
import com.hzlz.aviation.kernel.base.model.ToastModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.placeholder.DefaultPlaceholderLayout;
import com.hzlz.aviation.kernel.base.placeholder.IPlaceholderLayout;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderListener;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.toolbar.DefaultToolbar;
import com.hzlz.aviation.kernel.base.toolbar.IToolbar;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarAbility;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarListener;
import com.hzlz.aviation.kernel.base.toolbar.ToolbarUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.util.SoftInputUtils;

import java.util.UUID;


/**
 * Fragment ??????
 *
 * @since 2020-01-06 21:32
 */
public abstract class BaseFragment<VDB extends ViewDataBinding> extends Fragment
        implements PlaceholderListener, ToolbarAbility, ToolbarListener, Immersive {
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
    // View ?????????????????????
    private boolean mIsViewCreated;
    // UI ????????????
    private boolean mIsUIVisible;
    // ???????????????????????????
    private boolean mHasBeenInitialized;
    // Handler
    @NonNull
    protected SafeHandler mHandler = new SafeHandler(this);
    // ????????????
    protected String mId = UUID.randomUUID().toString();

    protected long leaveTime;

    /**
     * ???????????????????????????Pid,??????????????????
     * ?????????????????????pid???????????????????????????ViewPager??????Fragment?????????
     * ???????????????????????????????????????
     * <p>
     * ??????????????????????????????????????????{@link BaseFragment#mViewModel}????????????
     * ?????????????????????????????????{@link BaseFragment#mViewModel}???????????????????????????
     */
    private String pageName;

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

    /**
     * ?????????????????????????????? false
     *
     * @return true : ?????????false : ?????????
     */
    protected boolean enableLazyLoad() {
        return false;
    }

    /**
     * ??????ViewPager??????Fragment??????????????????????????????
     *
     * @return
     */
    protected boolean isUIVisible() {
        return mIsUIVisible;
    }
    //</editor-fold>

    //<editor-fold desc="????????????">

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        showLogDebug("BaseFragment", "Fragment?????? -->> " + getClass().getName());
        if (mBinding == null) {
            int layoutId = getLayoutId();
            if (layoutId != 0) {
                mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false);
            }
        }
        mIsViewCreated = true;
        if (mBinding != null) {
            View view = mBinding.getRoot();
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
            return mBinding.getRoot();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ???????????????????????????
        if (enableLazyLoad()) {
            lazyLoad();
        } else {
            if (!mHasBeenInitialized) {
                initToolbar();
                initImmersive();
                initArguments();
                initView();
                bindViewModels();
                loadData();
                mHasBeenInitialized = true;
            }
        }
    }

    /**
     * ???????????????
     */
    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            onArgumentsHandle(bundle);
        }
    }

    /**
     * ??????????????????
     */
    protected void onArgumentsHandle(Bundle bundle) {
    }


    @Override
    public void onDestroyView() {
        if (mNetworkDialog != null) {
            mNetworkDialog.onDestroy();
        }
        if (mPlaceholderLayout != null) {
            mPlaceholderLayout.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    /**
     * ?????????Fragment??????????????????BaseActivity???
     *
     * @param fragmentName ?????????????????????
     */
    private void updateFragmentNameToActivity(String fragmentName){
        Activity activity=getActivity();
        if(!(activity instanceof BaseActivity)){
            return;
        }
        ((BaseActivity)activity).setCurrentFragmentPageName(fragmentName);
    }

    /**
     * ??? Fragment ???????????????
     */
    protected void onVisible() {
        mIsUIVisible = true;
        if (enableLazyLoad()) {
            lazyLoad();
        }

        updateFragmentNameToActivity(getPageName());
        // GVideoEventBus.get(NOTIFICATION_HOME_CLEAN_FRESH_TASK).post(null);
    }

    /**
     * ??? Fragment ??????????????????
     */
    protected void onInVisible() {
        mIsUIVisible = false;
    }

    /**
     * ????????????????????????
     */
    public void onFragmentResume() {
        enterPage();
        onVisible();
    }

    /**
     * ????????????????????????
     */
    public void onFragmentPause() {
        exitPage();
        onInVisible();
    }

    /**
     * tab???????????????
     */
    public void onTabResumeFragment() {
        enterPage();
        onVisible();
    }

    /**
     * tab??????????????????
     */
    public void onTabPauseFragment() {
        exitPage();
        onInVisible();
    }

    /**
     * ?????????
     */
    private void lazyLoad() {
        if (mIsViewCreated && mIsUIVisible && !mHasBeenInitialized) {
            initToolbar();
            initImmersive();
            initArguments();
            initView();
            bindViewModels();
            loadData();
            mHasBeenInitialized = true;
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewModel????????????">

    /**
     * ?????? ViewModel
     *
     * @param viewModelClass ViewModel Class ???
     * @param <VM>           ????????? ViewModel ??????
     * @return ????????? ViewModel
     */
    @SuppressWarnings("unchecked")
    protected <VM extends BaseViewModel> VM bingViewModel(@NonNull Class<VM> viewModelClass) {
        mViewModel = ViewModelProviders.of(this).get(viewModelClass);

        // ??????pageName?????????????????????????????????????????????
        // ?????????????????????????????????????????????pid
        mViewModel.setPid(getPageName());

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

    /**
     * ?????????????????????
     *
     * @return true ????????????
     */
    protected boolean isFullTransparent() {
        Activity activity = getActivity();
        if ((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
            return true;
        }
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }
        TypedValue typedValue = new TypedValue();
        activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowFullscreen}).getValue(0, typedValue);
        if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
            if (typedValue.data != 0) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    protected IToolbar getToolbar() {
        if (mToolbar == null) {
            boolean f = isFullTransparent();
            if (f) {
                ImmersiveUtils.setStatusBarIconColor(this, true);
            }
            mToolbar = new DefaultToolbar(f);
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
    public String getToolbarTitle() {
        if (mToolbar != null){
           return mToolbar.getToolbarTitle();
        }
        return null;
    }

    @Override
    public void setToolbarTitle(@NonNull String text) {
        if (mToolbar != null) {
            mToolbar.setToolbarTitle(text);
        }
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
        if (!Navigation.findNavController(view).popBackStack()) {
            finishActivity();
        }
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {

    }


    @Override
    public void showRightOperationImageView(boolean show) {
        if (mToolbar != null) {
            mToolbar.showRightOperationImageView(show);
        }
    }

    @Override
    public void enableRightOperationImageView(boolean enable) {
        if (mToolbar != null) {
            mToolbar.enableRightOperationImageView(enable);
        }
    }

    @Override
    public void setRightOperationImage(@DrawableRes int resId) {
        if (mToolbar != null) {
            mToolbar.setRightOperationImage(resId);
        }
    }

    /**
     * ?????????????????????
     */
    protected boolean enableImmersive() {
        return false;
    }

    /**
     * ??????????????????
     */
    protected void initImmersive() {
        if (!enableImmersive()) {
            return;
        }
        ImmersiveUtils.enterImmersive(this);
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
        return new DefaultPlaceholderLayout(isDarkMode());
    }

    /**
     * ???????????????????????????????????????????????????????????? android.R.id.content
     *
     * @param layoutId ???????????????????????????????????????????????? MarginTop (?????? dp)
     */
    @SuppressWarnings("SameParameterValue")
    protected void setupPlaceholderLayout(@IdRes int layoutId) {
        setupPlaceholderLayout(layoutId, 0);
    }

    /**
     * ??????????????????
     *
     * @param layoutId      ??????????????????????????? id
     * @param marginTopInDp ???????????????????????????????????????????????? MarginTop (?????? dp)
     */
    @SuppressWarnings("SameParameterValue")
    protected void setupPlaceholderLayout(@IdRes int layoutId, int marginTopInDp) {
        if (mPlaceholderLayout != null) {
            return;
        }
        mPlaceholderLayout = getPlaceholderLayout();
        final Context context = getContext();
        if (mPlaceholderLayout == null || mBinding == null || context == null) {
            return;
        }
        mPlaceholderLayout.setPlaceholderListener(this);
        ViewGroup layout = mBinding.getRoot().findViewById(layoutId);
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
            layout.addView(mPlaceholderLayout.getView(context), index);
        }
    }

    /**
     * ????????????????????????
     *
     * @param type ?????????????????? {@link PlaceholderType}
     */
    protected void updatePlaceholderLayoutType(@PlaceholderType int type) {
        updatePlaceholderLayoutType(type, 0, false);
    }

    /**
     * ????????????????????????
     *
     * @param type        ?????????????????? {@link PlaceholderType}
     * @param paddingTop  ?????????
     * @param needShowTop ????????????????????????
     */
    protected void updatePlaceholderLayoutType(@PlaceholderType int type, int paddingTop, boolean needShowTop) {
        if (mPlaceholderLayout != null) {
            mPlaceholderLayout.updateType(type, isDarkMode(), paddingTop, needShowTop);
        }
    }

    @Override
    public void onReload(@NonNull View view) {
        if (mViewModel != null) {
            mViewModel.placeholderReload(view);
        }
    }

    @Override
    public void onLogin(@NonNull View view) {
        if (mViewModel != null) {
            mViewModel.placeholderLogin(view);
        }
    }
    //</editor-fold>

    //<editor-fold desc="??????">

    /**
     * ????????????
     *
     * @param stringResourceId ??????????????? id
     * @param arguments        ?????????????????????
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
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
    //</editor-fold>

    //<editor-fold desc="????????????">

    protected void hideSoftInputNoToken() {
        final Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            // ???????????????
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * ??????????????????
     *
     * @return ???????????? {@link INetworkDialog}
     */
    @Nullable
    protected INetworkDialog getNetworkDialog() {
        final Context context = getContext();
        if (context != null) {
            return new DefaultNetworkDialog(context);
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param textRedId ??????????????? id
     */
    protected void showNetworkDialog(@StringRes int textRedId) {
        if (mNetworkDialog == null) {
            mNetworkDialog = getNetworkDialog();
        }
        if (mNetworkDialog != null) {
            mNetworkDialog.show(textRedId);
        }
    }

    protected void finishActivity() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.finish();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mViewModel != null) {
            mViewModel.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * ???????????????
     *
     * @param view ?????????????????????
     */
    protected void openSoftKeyBoard(@NonNull View view) {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
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
     */
    protected void closeSoftKeyboard() {
        SoftInputUtils.hideSoftInput(getActivity());
//    final Context context = getContext();
//    if (context == null) {
//      return;
//    }
//    InputMethodManager imm =
//        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//    if (imm != null) {
//      imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//      view.clearFocus();
//    }
    }
    //</editor-fold>

    //<editor-fold desc="?????????">

    /**
     * ???????????????
     *
     * @param resId ??????????????????
     */
    public void addDestination(@NavigationRes int resId) {
        NavController navController = NavHostFragment.findNavController(this);
        NavDestination current = navController.getCurrentDestination();
        if (current == null) {
            return;
        }
        NavGraph graph = navController.getGraph();
        NavGraph destGraph = navController.getNavInflater().inflate(resId);
        NavDestination dest = graph.findNode(destGraph.getId());
        if (dest == null) {
            graph.addDestination(destGraph);
        }
    }
    //</editor-fold>

    //<editor-fold desc="????????????">

    /**
     * ??????fragment ????????????
     *
     * @return fragment????????????
     */
    public String getGvFragmentId() {
        return mId;
    }

    /**
     * ?????????????????????pid
     *
     * @return
     */
    public String getPid() {
        return "";
    }


    /**
     * Fragment??????????????????????????????Fragment
     *
     * @return
     */
    public String getFragmentLabel() {
        return getPid();
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public VideoModel getVideoModel() {
        return null;
    }

    /**
     * ?????????????????????
     *
     * @return ??????????????????????????????false
     */
    protected boolean isDarkMode() {
        return false;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        if (!TextUtils.isEmpty(pageName)){
            return pageName;
        }if (!TextUtils.isEmpty(getPid())){
            return getPid();
        }if (showToolbar()){
            return getToolbarTitle();
        }
        return null;
    }

    private void enterPage() {
        GVideoEventBus.get(Constant.EVENT_MSG.PAGE_ENTER).post(getPageName());
    }

    private void exitPage() {
        String pageName = getPageName();
        if (TextUtils.isEmpty(pageName)) {
            return;
        }
        ExitPageData exitPageData = new ExitPageData();
        exitPageData.videoModel = getVideoModel();
        exitPageData.pageName = getPageName();
        exitPageData.communityName = getCommunityName();
        exitPageData.tenantId = getTenantId();
        exitPageData.tenantName = getTenantName();
        exitPageData.sourcePage = getSourcePage();
        GVideoEventBus.get(Constant.EVENT_MSG.PAGE_EXIT).post(exitPageData);
    }

    protected String getSourcePage(){ return ""; }

    /**
     * ?????????????????????????????????????????????????????? -_-!
     */
    protected String getCommunityName() {
        return "";
    }

    protected Long getTenantId() {
        return null;
    }

    protected String getTenantName() {
        return "";
    }

    public void showFragmentWithTabId(String tabId){

    }

    protected void showLogDebug(String tag, String content) {
        Log.d(tag, content);
    }

    protected String getChildFragmentName(){
        return "";
    }

    public String getStringDataFromBundle(String key) {
        if (key == null) {
            return "";
        }
        Bundle bundle = getArguments();
        if (bundle == null) {
            return "";
        }
        return bundle.getString(key,"");
    }

    //</editor-fold>
}
