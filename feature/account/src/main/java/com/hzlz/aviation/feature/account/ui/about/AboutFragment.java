package com.hzlz.aviation.feature.account.ui.about;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentAboutBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.LogUtils;

/**
 * 关于界面
 *
 * @since 2020-02-18 20:24
 */
@SuppressWarnings("FieldCanBeLocal")
public final class AboutFragment extends BaseFragment<FragmentAboutBinding> {
    //<editor-fold desc="属性">
    private AboutViewModel mAboutViewModel;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.about_gvideo);
    }

    @Override
    protected void bindViewModels() {
        mAboutViewModel = bingViewModel(AboutViewModel.class);
        mBinding.setViewModel(mAboutViewModel);

            mBinding.screen.setText(getAndroidScreenProperty());
    }

    @Override
    protected void loadData() {

    }

    @Override
    public String getPid() {
        return StatPid.ABOUT_JSP;
    }

    public String getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        LogUtils.d("屏幕宽度（像素）：" + width);
        LogUtils.d("屏幕高度（像素）：" + height);
        LogUtils.d("屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        LogUtils.d("屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        LogUtils.d("屏幕宽度（dp）：" + screenWidth);
        LogUtils.d("屏幕高度（dp）：" + screenHeight);
        return "屏幕宽度（像素）：" + width +"\n"
                +"屏幕高度（像素）：" + height +"\n"
                +"屏幕密度（0.75 / 1.0 / 1.5）：" + density+"\n"
                +"屏幕密度dpi（120 / 160 / 240）：" + densityDpi+"\n"
                +"屏幕宽度（dp）：" + screenWidth+"\n"
                +"屏幕宽度（dp）：" + screenWidth+"\n"
                +"屏幕高度（dp）：" + screenHeight;
    }
    //</editor-fold>
}
