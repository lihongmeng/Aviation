package com.jxntv.base;

import static com.jxntv.base.Constant.BUNDLE_KEY.SOURCE_FRAGMENT_PID;

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

import com.jxntv.base.dialog.DefaultNetworkDialog;
import com.jxntv.base.dialog.INetworkDialog;
import com.jxntv.base.entity.ExitPageData;
import com.jxntv.base.immersive.Immersive;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.NetworkDialogModel;
import com.jxntv.base.model.PlaceholderModel;
import com.jxntv.base.model.StartActivityForResultModel;
import com.jxntv.base.model.ToastModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.DefaultPlaceholderLayout;
import com.jxntv.base.placeholder.IPlaceholderLayout;
import com.jxntv.base.placeholder.PlaceholderListener;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.toolbar.DefaultToolbar;
import com.jxntv.base.toolbar.IToolbar;
import com.jxntv.base.toolbar.ToolbarAbility;
import com.jxntv.base.toolbar.ToolbarListener;
import com.jxntv.base.toolbar.ToolbarUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.utils.SoftInputUtils;

import java.util.UUID;


/**
 * Fragment 基类
 *
 * @since 2020-01-06 21:32
 */
public abstract class BaseFragment<VDB extends ViewDataBinding> extends Fragment
        implements PlaceholderListener, ToolbarAbility, ToolbarListener, Immersive {
    //<editor-fold desc="属性">
    // 页面数据绑定
    protected VDB mBinding;
    // ViewModel
    @Nullable
    private BaseViewModel mViewModel;
    @Nullable
    private IToolbar mToolbar;
    // 占位布局
    @Nullable
    private IPlaceholderLayout mPlaceholderLayout;
    // 网络弹窗
    @Nullable
    private INetworkDialog mNetworkDialog;
    // View 是否已经被创建
    private boolean mIsViewCreated;
    // UI 是否可见
    private boolean mIsUIVisible;
    // 是否已经被初始化过
    private boolean mHasBeenInitialized;
    // Handler
    @NonNull
    protected SafeHandler mHandler = new SafeHandler(this);
    // 唯一标识
    protected String mId = UUID.randomUUID().toString();

    protected long leaveTime;

    /**
     * 每个页面有唯一一个Pid,用于神策埋点
     * 但是有些页面的pid需要上层指定，例如ViewPager结合Fragment的形式
     * 所以上级在创建之后指定该值
     * <p>
     * 并且因为上级指定的时候，往往{@link BaseFragment#mViewModel}还没创建
     * 所以保存在本类中，并在{@link BaseFragment#mViewModel}创建的时候赋值给它
     */
    private String pageName;

    //</editor-fold>

    //<editor-fold desc="抽象方法">

    /**
     * 获取当前布局 id
     *
     * @return 当前布局 id
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 绑定当前页面需要的 ViewModel
     */
    protected abstract void bindViewModels();

    /**
     * 加载数据
     */
    protected abstract void loadData();
    //</editor-fold>

    //<editor-fold desc="内部方法">

    /**
     * 是否启用懒加载，默认 false
     *
     * @return true : 启用；false : 不启用
     */
    protected boolean enableLazyLoad() {
        return false;
    }

    /**
     * 对于ViewPager样式Fragment，判断是否在前台展示
     *
     * @return
     */
    protected boolean isUIVisible() {
        return mIsUIVisible;
    }
    //</editor-fold>

    //<editor-fold desc="生命周期">

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        showLogDebug("BaseFragment", "Fragment名称 -->> " + getClass().getName());
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
        // 判断是否进行懒加载
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
     * 初始化参数
     */
    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            onArgumentsHandle(bundle);
        }
    }

    /**
     * 解析传递参数
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
     * 将当前Fragment的类名保存到BaseActivity中
     *
     * @param fragmentName 需要保存的名称
     */
    private void updateFragmentNameToActivity(String fragmentName){
        Activity activity=getActivity();
        if(!(activity instanceof BaseActivity)){
            return;
        }
        ((BaseActivity)activity).setCurrentFragmentPageName(fragmentName);
    }

    /**
     * 当 Fragment 可见时回调
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
     * 当 Fragment 不可见时回调
     */
    protected void onInVisible() {
        mIsUIVisible = false;
    }

    /**
     * 用于处理多层嵌套
     */
    public void onFragmentResume() {
        enterPage();
        onVisible();
    }

    /**
     * 用于处理多层嵌套
     */
    public void onFragmentPause() {
        exitPage();
        onInVisible();
    }

    /**
     * tab切换后可见
     */
    public void onTabResumeFragment() {
        enterPage();
        onVisible();
    }

    /**
     * tab切换后不可见
     */
    public void onTabPauseFragment() {
        exitPage();
        onInVisible();
    }

    /**
     * 懒加载
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

    //<editor-fold desc="ViewModel相关方法">

    /**
     * 绑定 ViewModel
     *
     * @param viewModelClass ViewModel Class 类
     * @param <VM>           具体的 ViewModel 泛型
     * @return 具体的 ViewModel
     */
    @SuppressWarnings("unchecked")
    protected <VM extends BaseViewModel> VM bingViewModel(@NonNull Class<VM> viewModelClass) {
        mViewModel = ViewModelProviders.of(this).get(viewModelClass);

        // 如果pageName不为空，优先使用上级的设定好的
        // 如果上级没有指定，就使用固定的pid
        mViewModel.setPid(getPageName());

        // 吐司
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
        // 占位布局
        mViewModel.getPlaceholderLiveData().observe(this, new NotNullObserver<PlaceholderModel>() {
            @Override
            protected void onModelChanged(@NonNull PlaceholderModel placeholderModel) {
                updatePlaceholderLayoutType(placeholderModel.getType());
            }
        });
        // 网络弹窗
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
     * Model 不为 Null 的 Observer 观察者
     *
     * @param <M> 模型泛型
     */
    protected abstract class NotNullObserver<M> implements Observer<M> {

        //<editor-fold desc="抽象方法">

        /**
         * 当模型更新时回调
         *
         * @param m 模型
         */
        protected abstract void onModelChanged(@NonNull M m);
        //</editor-fold>

        //<editor-fold desc="方法实现">
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
     * 是否显示 Toolbar, 默认显示
     *
     * @return true : 显示；false : 不显示
     */
    protected boolean showToolbar() {
        return true;
    }

    /**
     * 是否是全屏显示
     *
     * @return true 无状态栏
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
     * 初始化 toolbar
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
     * 是否需要沉浸式
     */
    protected boolean enableImmersive() {
        return false;
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersive() {
        if (!enableImmersive()) {
            return;
        }
        ImmersiveUtils.enterImmersive(this);
    }

    //</editor-fold>

    //<editor-fold desc="占位布局">

    /**
     * 获取占位布局
     *
     * @return 占位布局 {@link IPlaceholderLayout}
     */
    @Nullable
    protected IPlaceholderLayout getPlaceholderLayout() {
        return new DefaultPlaceholderLayout(isDarkMode());
    }

    /**
     * 设置占位布局，默认的占位布局所在的布局为 android.R.id.content
     *
     * @param layoutId 占位布局在占位布局所在的布局中的 MarginTop (单位 dp)
     */
    @SuppressWarnings("SameParameterValue")
    protected void setupPlaceholderLayout(@IdRes int layoutId) {
        setupPlaceholderLayout(layoutId, 0);
    }

    /**
     * 设置占位布局
     *
     * @param layoutId      占位布局所在的布局 id
     * @param marginTopInDp 占位布局在占位布局所在的布局中的 MarginTop (单位 dp)
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
     * 更新占位布局类型
     *
     * @param type 占位布局类型 {@link PlaceholderType}
     */
    protected void updatePlaceholderLayoutType(@PlaceholderType int type) {
        updatePlaceholderLayoutType(type, 0, false);
    }

    /**
     * 更新占位布局类型
     *
     * @param type        占位布局类型 {@link PlaceholderType}
     * @param paddingTop  上边距
     * @param needShowTop 是否需要顶部显示
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

    //<editor-fold desc="吐司">

    /**
     * 显示吐司
     *
     * @param stringResourceId 字符串资源 id
     * @param arguments        字符串资源参数
     */
    protected void showToast(@StringRes int stringResourceId, @Nullable Object... arguments) {
        try {
            showToast(getString(stringResourceId, arguments));
        } catch (Resources.NotFoundException ignore) {
        }
    }

    /**
     * 显示吐司
     *
     * @param text 吐司文本
     */
    protected void showToast(@NonNull String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
    //</editor-fold>

    //<editor-fold desc="网络弹窗">

    protected void hideSoftInputNoToken() {
        final Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            // 隐藏软键盘
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 获取网络弹窗
     *
     * @return 网络弹窗 {@link INetworkDialog}
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
     * 显示网络弹窗
     *
     * @param textRedId 字符串资源 id
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
     * 显示网络弹窗
     *
     * @param text 字符串
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
     * 隐藏网络弹窗
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
     * 打开软键盘
     *
     * @param view 需要聚焦的视图
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
     * 延迟打开软键盘
     *
     * @param view 需要聚焦的视图
     */
    protected void openSoftKeyBoardDelay(@NonNull View view) {
        mHandler.postDelayed(() -> openSoftKeyBoard(view), 200);
    }

    /**
     * 关闭软件盘
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

    //<editor-fold desc="目的地">

    /**
     * 添加目的地
     *
     * @param resId 导航资源文件
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

    //<editor-fold desc="唯一标识">

    /**
     * 获取fragment 唯一标识
     *
     * @return fragment唯一标识
     */
    public String getGvFragmentId() {
        return mId;
    }

    /**
     * 统计埋点，业务pid
     *
     * @return
     */
    public String getPid() {
        return "";
    }


    /**
     * Fragment标签，用于区分不同的Fragment
     *
     * @return
     */
    public String getFragmentLabel() {
        return getPid();
    }

    /**
     * 统计埋点，具体内容数据
     *
     * @return
     */
    public VideoModel getVideoModel() {
        return null;
    }

    /**
     * 是否为暗黑模式
     *
     * @return 是否为暗黑模式，默认false
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
     * 获取社区名称，只在圈子详情页埋点需要 -_-!
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
        if (BuildConfig.DEBUG) {
            return;
        }
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
