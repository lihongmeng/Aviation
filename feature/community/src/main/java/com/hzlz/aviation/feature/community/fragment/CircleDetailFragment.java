package com.hzlz.aviation.feature.community.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.CircleSortType.DEFAULT_NEWEST_SORT;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_GATHER_ID;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_GROUP_ID;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_PID;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_TYPE;
import static com.hzlz.aviation.kernel.base.Constant.VISITOR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.CircleSharedPrefs;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.adapter.CircleFamousHorizontalAdapter;
import com.hzlz.aviation.feature.community.adapter.qa.AutoScrollHelper;
import com.hzlz.aviation.feature.community.adapter.qa.QABannerAnswerAdapter;
import com.hzlz.aviation.feature.community.databinding.FragmentCircleDetailBinding;
import com.hzlz.aviation.feature.community.model.ModifyCircleTextInfo;
import com.hzlz.aviation.feature.community.viewmodel.CircleDetailViewModel;
import com.hzlz.aviation.feature.community.viewmodel.ModifyCircleTextInfoViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.dialog.BusinessDialog;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureDialog;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleDetail;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.circle.GatherModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.base.utils.BannerUtils;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.base.utils.StringUtils;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.base.utils.TypefaceUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.service.AudioLivePlayHelper;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.AviationBanner;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.youth.banner.config.IndicatorConfig;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("SetTextI18n")
public class CircleDetailFragment extends BaseFragment<FragmentCircleDetailBinding> {

    // ViewModel
    private CircleDetailViewModel viewModel;

    // ?????????????????????Adapter
    private CircleFamousHorizontalAdapter circleFamousHorizontalAdapter;

    // ?????????ViewPager???adapter
    private BaseFragmentVpAdapter circleVpAdapter;

    // ?????????ViewPager????????????
    private List<BaseFragment> baseFragmentList;

    // ??????????????????????????????????????????????????????
    private DefaultEnsureDialog needJoinCommunityDialog;

    private BaseFragment currentFragment;

    private BusinessDialog businessDialog;

    private Circle circle;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_detail;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.CIRCLE_DETAIL;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        circle = bundle.getParcelable(CIRCLE);
    }

    @Override
    protected void initView() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        setupPlaceholderLayout(R.id.empty_container);
        ImmersiveUtils.enterImmersiveFullTransparent(activity);
        Resources resources = activity.getResources();

        int statusBarHeight = WidgetUtils.getStatusBarHeight();
        int dp5 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_5DP);
        ConstraintLayout.LayoutParams backButtonLayoutParams = (ConstraintLayout.LayoutParams) mBinding.backButton.getLayoutParams();
        backButtonLayoutParams.setMargins(0, statusBarHeight + dp5, 0, 0);
        mBinding.backButton.setLayoutParams(backButtonLayoutParams);
//        mBinding.backButton.setPadding(0, statusBarHeight, 0, 0);

        mBinding.infoList.topLayout.setMinimumHeight(resources.getDimensionPixelOffset(R.dimen.DIMEN_44DP) + statusBarHeight);

        ViewGroup.LayoutParams layoutParams = mBinding.topPlaceHolder.getLayoutParams();
        layoutParams.height = resources.getDimensionPixelOffset(R.dimen.DIMEN_44DP) + statusBarHeight;
        mBinding.topPlaceHolder.setLayoutParams(layoutParams);

        mBinding.share.setVisibility(PluginManager.get(SharePlugin.class).isCanShare() ? View.VISIBLE : View.GONE);

        // ????????????Account????????????
        PluginManager.get(AccountPlugin.class).addDestinations(this);

        // ?????????????????????
        mBinding.infoList.circleFamousList.setLayoutManager(
                new GridLayoutManager(activity, 1, RecyclerView.HORIZONTAL, false));
        circleFamousHorizontalAdapter = new CircleFamousHorizontalAdapter(activity);
        mBinding.infoList.circleFamousList.setAdapter(circleFamousHorizontalAdapter);

        // ??????????????????????????????
        mBinding.infoList.tabLayout.setOnTabClickListener(position -> {
            if (mBinding.viewpager.getCurrentItem() == position && position == 0) {
                mBinding.infoList.tabLayout.updateLeftItemDrawable(0, null, null,
                        ContextCompat.getDrawable(activity, R.drawable.icon_arrow_up), null,
                        ResourcesUtils.getIntDimens(R.dimen.DIMEN_4DP));
                viewModel.showRecommendNewestDialog(mBinding.infoList.tabLayout, mBinding.shadow);
                return;
            }
            mBinding.viewpager.setCurrentItem(position);
        });

        // ??????????????????????????????appBar??????????????????????????????????????????
        mBinding.infoList.appBar.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i > -300) {
                mBinding.backButton.setImageResource(R.drawable.ic_back_white_78_78);
                mBinding.topPlaceHolder.setVisibility(GONE);
                mBinding.topCircleName.setVisibility(GONE);
                ImmersiveUtils.setStatusBarIconColor(this, false);
                if (PluginManager.get(SharePlugin.class).isCanShare()) {
                    mBinding.share.setVisibility(VISIBLE);
                }
            } else {
                if (mBinding.topPlaceHolder.getVisibility() == GONE) {
                    mBinding.topCircleName.setText(viewModel.getCircleName());
                    mBinding.backButton.setImageResource(R.drawable.ic_back_black_78_78);

                    mBinding.topPlaceHolder.setVisibility(VISIBLE);
                    mBinding.topCircleName.setVisibility(VISIBLE);
                    ImmersiveUtils.setStatusBarIconColor(this, true);
                    mBinding.share.setVisibility(GONE);
                }
            }
        });

        mBinding.refreshLayout.setEnableLoadMore(false);

        // ????????????????????????????????????
        mBinding.backButton.setOnClickListener(v -> activity.finish());

        // ????????????
        //        mBinding.share.setOnClickListener(v -> {}PluginManager.get(SharePlugin.class).showCreateBillDialog(activity));

        // ????????????
        mBinding.infoList.cover.setOnClickListener(v -> {
            CircleDetail circleDetail = viewModel.circleDetail.getValue();
            if (circleDetail == null || !circleDetail.admin) {
                return;
            }
            viewModel.showAvatarEntryDialog(activity);
        });

        TypefaceUtils.setNumberTypeface(mBinding.infoList.fansCount);
        mBinding.floatAudioView.setDelayedCollapse(false);
        mBinding.floatAudioView.tryShowView();
    }

    private void updateCircleBasicInfo(CircleDetail circleInfo) {

        Activity activity = getActivity();
        if (activity == null) return;

        mBinding.setModel(circleInfo);

        circleFamousHorizontalAdapter.updateGroupId(circleInfo.groupId);

        mBinding.infoList.introduction.setSingleLine(false);
        mBinding.infoList.introduction.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Drawable drawableLeft = ContextCompat.getDrawable(activity, R.drawable.ic_common_sound);
                if (drawableLeft == null) {
                    return;
                }
                drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());

                Drawable drawableRight = ContextCompat.getDrawable(activity, R.drawable.ic_common_arrow_right_6_10);
                if (drawableRight == null) {
                    return;
                }
                drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());

                int linCount = mBinding.infoList.introduction.getLineCount();
                if (linCount > 1) {
                    mBinding.infoList.introduction.setSingleLine();
                    mBinding.infoList.introduction.setCompoundDrawables(drawableLeft, null, drawableRight, null);
                    mBinding.infoList.introduction.setEnabled(true);
                } else {
                    mBinding.infoList.introduction.setCompoundDrawables(drawableLeft, null, null, null);
                    mBinding.infoList.introduction.setEnabled(false);
                }
                mBinding.infoList.introduction.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    // ??????????????????????????????
    private void updateCircleFamousInfo(CircleDetail circleInfo) {

        if (circleInfo.favoriteVO != null
                && circleInfo.favoriteVO.userList != null
                && !circleInfo.favoriteVO.userList.isEmpty()) {
            circleFamousHorizontalAdapter.refreshData(circleInfo.favoriteVO.userList);
        }
    }

    /**
     * ???????????????????????????????????????
     * 1?????????Banner????????????????????????????????????????????????????????????
     * 2????????????????????????tab??????????????????????????????/?????????????????????????????????????????????
     */
    private void updatePublishButton() {
        // ????????????tab????????????/?????????????????????????????????????????????????????????
        boolean needShowAddButton =
                (viewModel.showQAGroup.get() || viewModel.showQATeacher.get())
                        && viewModel.selectTabPosition == 0;
        mBinding.publish.setNeedClickRotate(needShowAddButton);
        if (needShowAddButton) {
            mBinding.publish.setImageResource(R.drawable.icon_publish_button_add);
            mBinding.publish.setOnClickListener(view -> viewModel.onMoreButtonClick(mBinding.publish, mBinding.publishAlign, mBinding.shadow));
        } else {
            mBinding.publish.setImageResource(R.drawable.icon_publish_button_write);
            mBinding.publish.setOnClickListener(view -> {
                viewModel.isWantToPublishQA = false;
                viewModel.onPublishClick(view);
            });
        }

        if (viewModel.selectTabPosition > 0) {
            mBinding.publish.setVisibility(viewModel.circleModuleList.getValue().get(
                    viewModel.selectTabPosition - 1).isTvProgram() ? GONE : VISIBLE);
        } else {
            mBinding.publish.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(CircleDetailViewModel.class);
        if (viewModel == null) {
            return;
        }

        if (circle == null) {
            return;
        }
        viewModel.setCircle(circle);
        mBinding.setViewModel(viewModel);

        // ??????CircleDetail??????
        viewModel.circleDetail.observe(this, this::dealCircleDetailUpdate);

        // ????????????Tab????????????
        viewModel.circleModuleList.observe(this, this::dealCircleModuleList);

        // ?????????????????????????????????????????????
        // ?????????????????????????????????????????????
        String userId = UserManager.getCurrentUser().getId();
        userId = TextUtils.isEmpty(userId) ? VISITOR : userId;
        CircleSharedPrefs.getInstance().putLong(userId + "_" + circle.getGroupId(), System.currentTimeMillis());

        viewModel.mBannerList.observe(
                this,
                model -> {
                    Activity activity = getActivity();
                    if (activity == null) return;
                    if (model == null) {
                        mBinding.infoList.bannerLayout.setVisibility(View.GONE);
                        return;
                    }
                    mBinding.infoList.bannerLayout.setVisibility(VISIBLE);
                    BannerUtils.initDefaultBanner(
                            model,
                            mBinding.infoList.banner,
                            getPid()
                    );
                }
        );

        viewModel.updatePublishButton.observe(this, o -> updatePublishButton());

        viewModel.showNeedJoinCommunityDialog.observe(this, o -> showNeedJoinCommunityDialog());

        viewModel.imagesBeanLiveData.observe(
                this,
                imagesBean -> {
                    if (viewModel == null || viewModel.circleDetail == null) {
                        return;
                    }
                    CircleDetail circleDetailValue = viewModel.circleDetail.getValue();
                    if (circleDetailValue == null) {
                        return;
                    }
                    Circle circle = new Circle();
                    circle.groupId = circleDetailValue.groupId;
                    circle.name = circleDetailValue.name;
                    businessDialog = new BusinessDialog(
                            getActivity(),
                            imagesBean,
                            StatPid.HOME,
                            circle
                    );
                    businessDialog.showImagesBeanData(imagesBean);
                }
        );
    }

    // ??????????????????????????????
    private void dealCircleDetailUpdate(CircleDetail circleDetail) {
        mBinding.refreshLayout.finishGVideoRefresh();
        if (circleDetail == null) {
            return;
        }

        if (circleDetail.broadcastDetail != null) {
            iniAudioView(circleDetail);
        } else {
            AudioLivePlayHelper.getInstance().closeByData(circleDetail);
            initQAView(circleDetail);
        }

        // ???????????????????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????
        if (!viewModel.userHasSelectSortType) {
            CircleDetailViewModel.sortType = circleDetail.sortType;
        }

        // ????????????????????????
        updateCircleBasicInfo(circleDetail);

        // ??????????????????????????????
        updateCircleFamousInfo(circleDetail);

        // ???????????? ???????????? ?????? ?????????????????????????????????????????????
        // ???????????????????????????????????????????????????
        // ???????????????????????????Fragment???????????????
        // ????????????????????????
        viewModel.getCircleModuleList();

    }

    // ????????????Tab????????????
    private void dealCircleModuleList(List<CircleModule> circleModuleList) {

        Activity activity = getActivity();
        if (activity == null || viewModel == null) return;

        List<String> resultList = new ArrayList<>();
        baseFragmentList = new ArrayList<>();

        String communityName = circle == null ? "??????" : circle.getName();

        // ??????????????????
        if (circle != null) {

            Bundle hotCircleArguments = viewModel.getCommonArgumentsSendToChild();

            // ???????????????sortType
            String moduleName;
            if (CircleDetailViewModel.sortType == DEFAULT_NEWEST_SORT) {
                moduleName = getString(R.string.space_newest);
            } else {
                moduleName = getString(R.string.space_hot_chat);
            }
            resultList.add(moduleName);
            hotCircleArguments.putString(EXTRA_PID, moduleName);
            CircleDetailListFragment hotNewestCircle = new CircleDetailListFragment();
            currentFragment = hotNewestCircle;
            hotCircleArguments.putLong(EXTRA_GROUP_ID, circle.groupId);
            hotNewestCircle.setArguments(hotCircleArguments);
            baseFragmentList.add(hotNewestCircle);
        }

        if (circleModuleList != null) {
            for (CircleModule circleModule : circleModuleList) {
                if (circleModule == null) {
                    continue;
                }
                resultList.add(circleModule.getName());

                CircleDetailListFragment circleDetailListFragment = new CircleDetailListFragment();
                Bundle arguments = viewModel.getCommonArgumentsSendToChild();
                arguments.putLong(EXTRA_GATHER_ID, circleModule.getId());
                arguments.putString(EXTRA_PID, circleModule.getName());
                arguments.putInt(EXTRA_TYPE, circleModule.getType());
                circleDetailListFragment.setArguments(arguments);
                baseFragmentList.add(circleDetailListFragment);
            }
        }

        mBinding.infoList.tabLayout.setSelectSize(16);
        mBinding.infoList.tabLayout.setUnSelectSize(14);
        mBinding.infoList.tabLayout.updateDataSourceWithStringList(resultList);
        mBinding.infoList.tabLayout.updateLeftItemDrawable(
                0,
                null,
                null,
                ContextCompat.getDrawable(activity, R.drawable.icon_arrow_down),
                null,
                ResourcesUtils.getIntDimens(R.dimen.DIMEN_4DP)
        );

        circleVpAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        circleVpAdapter.updateSource(baseFragmentList);
        mBinding.viewpager.setAdapter(circleVpAdapter);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewModel.selectTabPosition = position;
                mBinding.infoList.tabLayout.updateIndex(position);
                if (currentFragment != null) {
                    currentFragment.onTabPauseFragment();
                }
                currentFragment = baseFragmentList.get(position);
                currentFragment.onTabResumeFragment();
                updatePublishButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBinding.viewpager.setVisibility(View.VISIBLE);
        if (currentFragment == null) {
            currentFragment = baseFragmentList.get(0);
            currentFragment.onTabResumeFragment();
        }
    }

    @Override
    protected void loadData() {

        //super.loadData();
        // ?????????????????????
        listenEvent();

        // ??????????????????
        viewModel.checkNetworkAndLoginStatus();

        viewModel.loadCircleInfo();

    }

    @Override
    public void onReload(@NonNull View view) {
        viewModel.checkNetworkAndLoginStatus();
        viewModel.loadCircleInfo();
    }

    protected void listenEvent() {
        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(this, o -> viewModel.loadCircleInfo());

        GVideoEventBus.get(CirclePlugin.NEED_UPDATE_CIRCLE_INFO).observe(this, o -> viewModel.loadCircleInfo());

        GVideoEventBus.get(CirclePlugin.MODIFY_CIRCLE_INFO, ModifyCircleTextInfo.class).observe(this,
                modifyCircleTextInfo -> {
                    if (modifyCircleTextInfo == null) {
                        return;
                    }
                    switch (modifyCircleTextInfo.modifyType) {
                        case ModifyCircleTextInfoViewModel.ModifyType.CIRCLE_NAME:
                            viewModel.startUploadCircleName(modifyCircleTextInfo.modifyInfo);
                            break;
                        case ModifyCircleTextInfoViewModel.ModifyType.CIRCLE_INTRODUCTION:
                            viewModel.startUploadCircleIntroduction(modifyCircleTextInfo.modifyInfo);
                            break;
                    }
                });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(this, o -> viewModel.loadCircleInfo());

        // ?????????????????????????????????????????????????????????????????????????????????
        GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH, String.class).observe(
                this,
                startPublishFrom -> {
                    if (mBinding == null
                            || viewModel == null
                            || currentFragment == null
                            || !(currentFragment instanceof CircleDetailListFragment)
                            || (!TextUtils.equals(startPublishFrom, Constant.START_PUBLISH_FROM.HOME)
                            && !TextUtils.equals(startPublishFrom, Constant.START_PUBLISH_FROM.CIRCLE_DETAIL))) {
                        return;
                    }
                    mBinding.refreshLayout.scrollTo(0, 0);
                    ((CircleDetailListFragment) currentFragment).loadRefreshDataAndResetPage();
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
        if (helper != null) {
            helper.startAuto();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
        if (helper != null) {
            helper.stop();
        }
    }

    //----------------   ????????????   ----------------------
    private AutoScrollHelper helper;

    /**
     * ?????????????????????
     */
    private void initQAView(CircleDetail circleDetail) {
        if (circleDetail.answerDetailListVO != null) {
            GatherModel gatherModel = circleDetail.answerDetailListVO.getGather();
            if (gatherModel != null) {
                mBinding.infoList.layoutQa.setGather(gatherModel);
                if (circleDetail.answerDetailListVO.getMentorVoList() != null) {
                    initQABanner(circleDetail);
                } else {
                    if (helper == null) {
                        helper = new AutoScrollHelper();
                        helper.init(mBinding.infoList.layoutQa.recyclerQa);
                    }
                    if (circleDetail.answerDetailListVO.getAnswerList() != null
                            && circleDetail.answerDetailListVO.getAnswerList().size() > 0) {
                        helper.refreshData(circleDetail.answerDetailListVO.getAnswerList());
                        mBinding.infoList.layoutQa.recyclerQa.setVisibility(VISIBLE);
                        mBinding.infoList.layoutQa.placeEmpty.setVisibility(GONE);
                    } else {
                        mBinding.infoList.layoutQa.recyclerQa.setVisibility(GONE);
                        mBinding.infoList.layoutQa.placeEmpty.setVisibility(VISIBLE);
                    }
                }
            } else {
                if (helper != null) {
                    helper.stop();
                }
            }
        }
    }

    //---------  ????????????banner??????   ---------------------------------------------
    private void initQABanner(CircleDetail circleDetail) {
        List<AuthorModel> modelList = circleDetail.answerDetailListVO.getMentorVoList();
        if (modelList != null && modelList.size() > 1) {
            AviationBanner banner = mBinding.infoList.layoutQa.teacherBanner;
            QABannerAnswerAdapter adapter = new QABannerAnswerAdapter(modelList, getPageName());
            adapter.setCircle(circle);
            banner.setAdapter(adapter).setLoopTime(3500);
            BannerUtils.initDefaultIndicator(banner, false, IndicatorConfig.Direction.RIGHT, R.dimen.DIMEN_10DP, 0);
        }
    }

    //------------  ???????????? -------------------------------------------------
    private AnimationDrawable playAnim;
    //????????????
    private boolean isPlayingAudio = false;
    //????????????????????????????????????
    private boolean isCurrentDetailPlay = false;
    private AudioLivePlayHelper.PlayListener playListener;
    private String titleString;
    private boolean audioLiveEnd = false;

    private void iniAudioView(CircleDetail circleDetail) {

        SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper("circle_audio");
        AviationImageView imageView = mBinding.infoList.layoutCircleAudio.play;
        AviationImageView tips = mBinding.infoList.layoutCircleAudio.tips;
        TextView title = mBinding.infoList.layoutCircleAudio.audioTitle;

        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setSingleLine(true);
        title.setSelected(true);
        title.setFocusable(true);
        title.setFocusableInTouchMode(true);
        titleString = circleDetail.broadcastDetail.getName();
        initAudioTitle(title);

        imageView.setOnClickListener(view -> {
            tips.setVisibility(GONE);
            if (isCurrentDetailPlay && audioLiveEnd) {
                ToastUtils.showShort("???????????????");
                return;
            }
            if (!isPlayingAudio) {
                AudioLivePlayHelper.getInstance().setPlayData(circleDetail);
            } else {
                AudioLivePlayHelper.getInstance().pause();
            }
        });

        mBinding.infoList.layoutCircleAudio.authorAvatar.setOnClickListener(view -> {
            PluginManager.get(AccountPlugin.class).startPgcActivity(imageView,
                    circleDetail.broadcastDetail.getAuthor());
        });

        mBinding.infoList.layoutCircleAudio.authorName.setOnClickListener(view -> {
            mBinding.infoList.layoutCircleAudio.authorAvatar.performClick();
        });

        tips.setOnClickListener(v -> imageView.performClick());

        playListener = new AudioLivePlayHelper.PlayListener() {
            @Override
            public void setPlayData(CircleDetail circleDetail) {
                prefsWrapper.putBoolean("isShowTip", false);
                setAudioPlayView(true, imageView);
            }

            @Override
            public void play() {
                prefsWrapper.putBoolean("isShowTip", false);
                setAudioPlayView(true, imageView);
            }

            @Override
            public void pause() {
                setAudioPlayView(false, imageView);
                prefsWrapper.putBoolean("isShowTip", false);
            }

            @Override
            public void end() {
                if (System.currentTimeMillis() > circleDetail.broadcastDetail.getEndTime()) {
                    audioLiveEnd = true;
                    title.setText(SpannableStringUtils.setSpanColor(titleString + " ???????????????", "???????????????",
                            R.color.color_999999));
                }
                setAudioPlayView(false, imageView);
            }
        };

        isPlayingAudio = AudioLivePlayHelper.getInstance().isCurrentCirclePlaying(circleDetail);
        isCurrentDetailPlay = isPlayingAudio;
        //????????????????????????
        if (prefsWrapper.getBoolean("isShowTip", true)) {
            tips.setVisibility(VISIBLE);
        } else {
            tips.setVisibility(GONE);
        }
        //??????????????????????????????
        if (isPlayingAudio) {
            setAudioPlayView(true, imageView);
        }
        AudioLivePlayHelper.getInstance().setPlayListener(playListener);

        //??????????????????????????????????????????????????????????????????EventBus??????
        GVideoEventBus.get(Constant.EVENT_MSG.HAS_BACK_HOME).observe(this, o -> {
            tryCloseAudioLive();
        });

    }

    private void initAudioTitle(TextView title) {
        title.post(() -> {
            AsyncUtils.runOnUIThread(() -> {
                int w = title.getMeasuredWidth();
                if (w == 0) {
                    initAudioTitle(title);
                } else {
                    String contentText = titleString;
                    int num = (int) (w / title.getTextSize());
                    if (contentText.length() >= num - 5) {
                        titleString = StringUtils.showMaxLengthString(contentText, num - 5);
                    }
                    title.setText(SpannableStringUtils.setSpanColor(titleString + " ????????????", "????????????",
                            R.color.color_999999));
                }
            }, 100);

        });
    }

    private void setAudioPlayView(boolean isPlaying, ImageView imageView) {
        if (isPlaying) {
            imageView.setImageResource(R.drawable.ic_group_play_anim);
            playAnim = (AnimationDrawable) imageView.getDrawable();
            playAnim.start();
            isPlayingAudio = true;
            isCurrentDetailPlay = true;
        } else {
            if (isCurrentDetailPlay) {
                isPlayingAudio = false;
                if (playAnim != null) {
                    playAnim.stop();
                    imageView.clearAnimation();
                }
                imageView.setImageResource(R.drawable.ic_group_play_0);
            }
        }
    }

    private void tryCloseAudioLive() {
        if (playListener != null) {
            AudioLivePlayHelper.getInstance().removePlayListener(playListener);
            mBinding.floatAudioView.release();
            playListener = null;
            if (!isPlayingAudio) {
                //????????????????????????????????????????????????????????????????????????????????????
                AudioLivePlayHelper.getInstance().closeByData(viewModel.circleDetail.getValue());
            }
        }
    }

    @Override
    public void onDestroy() {
        tryCloseAudioLive();
        super.onDestroy();
    }

    @Override
    protected String getCommunityName() {
        if (viewModel != null) {
            return viewModel.getCircleName();
        }
        return super.getCommunityName();
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null || circle == null) {
            return null;
        }
        return circle.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (viewModel == null || circle == null) {
            return "";
        }
        return circle.tenantName;
    }

    private void showNeedJoinCommunityDialog() {
        Activity activity = getActivity();
        if (activity == null || viewModel == null) {
            return;
        }
        if (needJoinCommunityDialog == null) {
            needJoinCommunityDialog = new DefaultEnsureDialog(activity);
            needJoinCommunityDialog.init(
                    v -> {
                        needJoinCommunityDialog.dismiss();
                        if (viewModel.isWantToPublishQA) {
                            viewModel.onQaPublishClicked(mBinding.publish);
                        } else {
                            viewModel.onPublishClick(mBinding.publish);
                        }
                    },
                    ResourcesUtils.getString(R.string.publish_join_community_tip),
                    ResourcesUtils.getString(R.string.alright)
            );
            needJoinCommunityDialog.showJoinCommunityStyle();
        }
        needJoinCommunityDialog.show();
    }

}
