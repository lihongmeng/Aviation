package com.hzlz.aviation.feature.community.viewmodel;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COMMUNITY_NAME;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.GATHER_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.IS_QA;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TENANT_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TENANT_NAME;
import static com.hzlz.aviation.kernel.base.Constant.CircleSortType.DEFAULT_NEWEST_SORT;
import static com.hzlz.aviation.kernel.base.Constant.CircleSortType.DEFAULT_RECOMMEND_SORT;
import static com.hzlz.aviation.kernel.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.hzlz.aviation.kernel.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_GALLERY;
import static com.hzlz.aviation.kernel.base.plugin.CirclePlugin.CIRCLE_HOTTEST_FRAGMENT_UPDATE;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.account.model.annotation.AvatarEntry;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.FileRepository;
import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.dialog.CircleIntroductionDialog;
import com.hzlz.aviation.feature.community.view.PublishButton;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureDialog;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleCommentImage;
import com.hzlz.aviation.kernel.base.model.circle.CircleDetail;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.circle.QaGroupListModel;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.base.view.ViewPagerTab;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CircleDetailViewModel extends BaseViewModel {

    // ???CircleDetailFragment???????????????????????????????????????
    private Circle circle;

    // ?????????????????????????????????Uri
    private Uri imageUri;

    // CircleRepository
    private final CircleRepository circleRepository = new CircleRepository();

    // FileRepository
    private final FileRepository fileRepository = new FileRepository();

    // ?????????????????????????????????????????????
    public CheckThreadLiveData<CircleDetail> circleDetail = new CheckThreadLiveData<>();

    // ???????????????????????????????????????
    public CheckThreadLiveData<List<CircleModule>> circleModuleList = new CheckThreadLiveData<>();

    // ???????????????????????????????????????????????????
    private GVideoBottomSheetItemDialog mAvatarEntryDialog;

    // ??????????????????????????????????????????????????????
    private DefaultEnsureDialog adminExitTipDialog;

    // ??????????????????????????????
    private DefaultEnsureCancelDialog exitTipDialog;

    // TODO: 2021/8/3
    // ?????????????????????
    // ??????????????????????????????Bundle??????????????????????????????????????????????????????????????????
    // ???????????????????????????static?????????????????????????????????
    public static int sortType = DEFAULT_RECOMMEND_SORT;

    // ?????????????????????????????????
    public boolean userHasSelectSortType;

    // ???????????????????????????Banner
    public CheckThreadLiveData<BannerModel> mBannerList = new CheckThreadLiveData<>();

    //????????????banner
    public ObservableBoolean showBanner = new ObservableBoolean();
    //????????????????????????
    public ObservableBoolean showQAGroup = new ObservableBoolean();
    //??????????????????????????????
    public ObservableBoolean showQATeacher = new ObservableBoolean();
    //????????????????????????
    public ObservableBoolean showAudio = new ObservableBoolean();
    //??????????????????
    public ObservableBoolean showTopic = new ObservableBoolean();
    //????????????????????????
    public ObservableField<TopicDetail> showTopicDetail = new ObservableField<>();

    public ObservableBoolean hasJoinCircle = new ObservableBoolean();

    // ?????????????????????????????????????????????
    public ObservableField<String> circleBackground = new ObservableField<>();

    // ??????????????????
    public CheckThreadLiveData<Boolean> updatePublishButton = new CheckThreadLiveData<>();

    // ??????Fragment????????????????????????????????????
    public CheckThreadLiveData<Boolean> showNeedJoinCommunityDialog = new CheckThreadLiveData<>();

    // ??????Fragment????????????
    public CheckThreadLiveData<BannerModel.ImagesBean> imagesBeanLiveData =new CheckThreadLiveData<>();

    // ??????????????????????????????tab??????
    public int selectTabPosition = 0;

    public boolean isWantToPublishQA = false;

    public CircleDetailViewModel(@NonNull Application application) {
        super(application);
        sortType = DEFAULT_RECOMMEND_SORT;
    }

    public void setCircle(Circle circle) {
        if (circle != null) {
            hasJoinCircle = InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId);
        }
        this.circle = circle;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
        }
    }

    public void loadCircleInfo() {
        if (circle == null) {
            return;
        }
        circleRepository.circleDetail(circle.groupId)
                .subscribe(new GVideoResponseObserver<CircleDetail>() {
                    @Override
                    protected void onSuccess(@NonNull CircleDetail netData) {
                        circleDetail.setValue(netData);
                        circle.update(netData);
                        hasJoinCircle.set(netData.join);
                        if (netData.backgroundImage != null && TextUtils.isEmpty(circleBackground.get())) {
                            circleBackground.set(netData.backgroundImage.url);
                        }
                        getBannerList();
                        loadBusinessDialog();
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        circleDetail.setValue(null);
                        updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
                    }
                });
    }

    public void loadBusinessDialog(){
        if (imagesBeanLiveData == null||circleDetail==null) {
            return;
        }
        CircleDetail circleDetailValue=circleDetail.getValue();
        if(circleDetailValue==null){
            return;
        }
        PluginManager.get(HomePlugin.class)
                .getBannerTopList(circleDetailValue.groupId)
                .subscribe(new GVideoResponseObserver<BannerModel>() {

                    @Override
                    protected void onSuccess(@NonNull BannerModel bannerModel) {
                        if (!BannerModel.isDataValid(bannerModel)) {
                            imagesBeanLiveData.setValue(null);
                            return;
                        }
                        BannerModel.ImagesBean imagesBean = bannerModel.getImages().get(0);
                        if (imagesBean == null) {
                            imagesBeanLiveData.setValue(null);
                            return;
                        }
                        imagesBeanLiveData.setValue(imagesBean);
                        imagesBeanLiveData = null;
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });
    }

    public void getCircleModuleList() {
        circleRepository.circleModuleList(circle.groupId)
                .subscribe(new GVideoResponseObserver<List<CircleModule>>() {
                    @Override
                    protected void onSuccess(@NonNull List<CircleModule> netList) {
                        circleModuleList.setValue(netList);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        circleModuleList.setValue(new ArrayList<>());
                        super.onFailed(throwable);
                    }
                });
    }

    /**
     * ????????????
     */
    public void joinCircle(View v, final boolean isNeedTip) {

        if (!UserManager.hasLoggedIn()) {
            AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
            if (plugin != null) {
                plugin.startLoginActivity(v.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPid()),
                        ResourcesUtils.getString(R.string.join_circle)
                );
            }
            return;
        }
        if (circle == null) {
            return;
        }
        circleRepository.joinCircle(circle.groupId, "")
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        Context context = v.getContext();
                        if (context == null) {
                            return;
                        }

                        if (!isNeedTip) {
                            showToast(ResourcesUtils.getString(R.string.join_success));
                        } else {
                            showNeedJoinCommunityDialog.setValue(true);
                        }

                        hasJoinCircle.set(true);

                        GVideoEventBus.get(
                                Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE,
                                CircleJoinStatus.class
                        ).post(new CircleJoinStatus(circle.groupId, true));

                        loadCircleInfo();

                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.CIRCLE_DETAIL),
                                null
                        );
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.CIRCLE_DETAIL),
                                throwable.getMessage()
                        );
                        showToast(ResourcesUtils.getString(R.string.join_failed));
                    }
                });

    }

    public void exitCircle(
            long groupId,
            String groupName,
            Long tenantId,
            String tenantName,
            DefaultEnsureCancelDialog dialog,
            List<String> labels
    ) {
        new CircleRepository()
                .exitCircle(groupId)
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
                               @Override
                               protected void onRequestData(@NonNull Object result) {
                                   showToast(ResourcesUtils.getString(R.string.already_exit_this_circle));
                                   hasJoinCircle.set(false);
                                   GVideoSensorDataManager.getInstance().unFollowCommunity(
                                           groupId,
                                           groupName,
                                           tenantId,
                                           tenantName,
                                           labels,
                                           null
                                   );

                                   GVideoEventBus.get(
                                           Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE,
                                           CircleJoinStatus.class
                                   ).post(new CircleJoinStatus(circle.groupId, false));

                                   loadCircleInfo();
                                   dialog.dismiss();
                               }

                               @Override
                               protected void onRequestError(Throwable throwable) {
                                   showToast(throwable.getMessage());
                                   GVideoSensorDataManager.getInstance().unFollowCommunity(
                                           groupId,
                                           groupName,
                                           tenantId,
                                           tenantName,
                                           labels,
                                           throwable.getMessage()
                                   );
                                   dialog.dismiss();
                               }
                           }
                );
    }

    /**
     * ??????
     *
     * @param context ?????????
     */
    public void takePicture(@NonNull Context context) {
        PermissionManager
                .requestPermissions(context, new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        doTakePicture(context);
                    }

                    @Override
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission) {
                        showToast("??????????????????");
                    }
                }, Manifest.permission.CAMERA);
    }

    /**
     * ??????
     *
     * @param context ?????????
     */
    public void doTakePicture(@NonNull Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int requestCode = -1;
        requestCode = REQUEST_CODE_FROM_CAMERA;
        File photo = new File(context.getExternalCacheDir(), "cover.jpg");
        imageUri = FileProvider.getUriForFile(
                context, FilePlugin.AUTHORITY, photo
        );
        if (imageUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, requestCode);
        }
    }

    /**
     * ?????????????????????
     */
    public void selectPictureFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // ??????
            case REQUEST_CODE_FROM_CAMERA:
                if (imageUri != null) {
                    startUpdateCover();
                }
                break;
            // ??????
            case REQUEST_CODE_FROM_GALLERY:
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    startUpdateCover();
                }
                break;
            default:
                break;
        }
    }

    public void startUpdateCover() {
        fileRepository.doUploadImageFile(FileRepository.FILE_BUSINESS_TYPE_USER_HEADER_PIC, imageUri, null)
                .subscribe(new GVideoResponseObserver<String>() {
                    @Override
                    protected boolean isShowNetworkDialog() {
                        return true;
                    }

                    @Override
                    protected int getNetworkDialogTipTextResId() {
                        return R.string.is_uploading;
                    }

                    @Override
                    protected void onSuccess(@NonNull String ossId) {
                        Circle result = new Circle();
                        result.setGroupId(circle.groupId);
                        result.setImageVO(new CircleCommentImage(ossId, imageUri.getPath()));
                        circleRepository.updateCircleInfo(result)
                                .subscribe(new BaseResponseObserver<Object>() {
                                    @Override
                                    protected void onRequestData(Object o) {
                                        loadCircleInfo();
                                    }

                                    @Override
                                    protected void onRequestError(Throwable throwable) {
                                        showToast(R.string.modify_failed);
                                    }
                                });
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        showToast(R.string.modify_failed);
                    }
                });
    }

    public void startUploadCircleName(String circleName) {
        Circle result = new Circle();
        result.name = circleName;
        result.groupId = circle.groupId;
        uploadInfoReal(result);
    }

    public void startUploadCircleIntroduction(String circleIntroduction) {
        Circle result = new Circle();
        result.introduction = circleIntroduction;
        result.groupId = circle.groupId;
        uploadInfoReal(result);
    }

    private void uploadInfoReal(Circle result) {
        circleRepository.updateCircleInfo(result)
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        loadCircleInfo();
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        showToast(R.string.modify_failed);
                    }
                });
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     *
     * @param alignPositionView   ???????????????????????????????????????
     * @param alignPopupWidthView ?????????????????????????????????????????????View????????????????????????alignPositionView???????????????
     * @param shadow              ??????????????????
     */
    public void onMoreButtonClick(PublishButton alignPositionView, View alignPopupWidthView, View shadow) {
        Context context = alignPositionView.getContext();
        if (context == null) {
            return;
        }
        shadow.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_publish_more, null);
        PopupWindow window = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        window.setOnDismissListener(() -> {
            shadow.setVisibility(View.INVISIBLE);
            alignPositionView.startRotate();
        });

        view.setOnClickListener(v -> {
            window.dismiss();
            shadow.setVisibility(View.INVISIBLE);
        });

        int[] positionArray = new int[]{0, 0};
        alignPositionView.getLocationInWindow(positionArray);

        ConstraintLayout rootView = view.findViewById(R.id.root);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rootView.getLayoutParams());
        layoutParams.setMargins(
                positionArray[0] - ResourcesUtils.getIntDimens(R.dimen.DIMEN_53DP),
                positionArray[1] - WidgetUtils.getStatusBarHeight() - ResourcesUtils.getIntDimens(R.dimen.DIMEN_135DP),
                0,
                0
        );
        rootView.setLayoutParams(layoutParams);

        rootView.findViewById(R.id.moment_click).setOnClickListener(v -> {
            window.dismiss();
            shadow.setVisibility(View.INVISIBLE);
            onPublishClick(alignPositionView);
        });

        rootView.findViewById(R.id.question_click).setOnClickListener(v -> {
            window.dismiss();
            shadow.setVisibility(View.INVISIBLE);
            onQaPublishClicked(alignPositionView);
        });

        // ????????????????????????????????????View????????????????????????showPopupWindow??????????????????View
        showPopupWindow(window, alignPopupWidthView);
    }

    private void showPopupWindow(PopupWindow window, View alignView) {
        window.setWidth(alignView.getWidth());
        // ????????????
        window.setAnimationStyle(R.style.newest_hottest_window_anim);
        // ??????????????????
        window.setBackgroundDrawable(new ColorDrawable(ResourcesUtils.getColor(R.color.color_000000_0)));
        // ????????????????????????
        window.setFocusable(true);
        // ??????????????????????????????????????????
        window.setOutsideTouchable(true);
        //sdk > 21 ?????? ????????????????????????????????????
        window.setClippingEnabled(false);
        // ??????popupwindow?????????
        window.update();
        // ????????????????????????????????????????????????????????????
        // window.showAsDropDown(view, 0, 20);
        window.showAsDropDown(alignView);
    }

    public void onPublishClick(View view) {
        isWantToPublishQA = false;
        if (isCanPublish(view)) {
            dealPublishClick(view);
        }
    }

    private void dealPublishClick(View view) {
        showNetworkDialog("????????????...");
        circleRepository.canPublish(circle.groupId, null)
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        hideNetworkDialog();
                        long gatherId = -1;

                        List<CircleModule> circleModuleListValue = circleModuleList.getValue();

                        if (circleModuleListValue != null
                                && selectTabPosition > 0
                                && selectTabPosition <= circleModuleListValue.size()) {
                            gatherId = circleModuleListValue.get(selectTabPosition - 1).getId();
                        }
                        Bundle bundle=new Bundle();
                        bundle.putParcelable(CIRCLE, circle);
                        bundle.putLong(GATHER_ID, gatherId);
                        bundle.putBoolean(IS_QA, false);
                        PluginManager.get(RecordPlugin.class).startPublishFragmentUseActivity(view.getContext(), bundle);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        hideNetworkDialog();
                        showToast(throwable.getMessage());
                    }
                });
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        selectTabPosition = 0;
        loadCircleInfo();
        getBannerList();
    }

    public void showRecommendNewestDialog(ViewPagerTab tabLayout, View shadow) {
        Context context = tabLayout.getContext();
        if (context == null) {
            return;
        }
        shadow.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_recommend_newest, null);
        PopupWindow window = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        window.setOnDismissListener(() -> shadow.setVisibility(View.INVISIBLE));

        view.setOnClickListener(v -> dismissPhoneWindow(window, tabLayout, shadow));

        int[] positionArray = new int[]{0, 0};
        tabLayout.getLocationInWindow(positionArray);

        ConstraintLayout rootView = view.findViewById(R.id.root);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rootView.getLayoutParams());
        lp.setMargins(
                ResourcesUtils.getIntDimens(R.dimen.DIMEN_15DP),
                positionArray[1] - WidgetUtils.getStatusBarHeight(),
                0,
                0
        );
        rootView.setLayoutParams(lp);

        AviationTextView newest = rootView.findViewById(R.id.newest);
        AviationTextView hottest = rootView.findViewById(R.id.hottest);
        AviationImageView newestCheck = rootView.findViewById(R.id.newest_check);
        AviationImageView hottestCheck = rootView.findViewById(R.id.hottest_check);

        if (sortType == DEFAULT_NEWEST_SORT) {
            newest.setTextColor(ResourcesUtils.getColor(R.color.color_333333));
            hottest.setTextColor(ResourcesUtils.getColor(R.color.color_999999));
            newestCheck.setVisibility(View.VISIBLE);
            hottestCheck.setVisibility(View.GONE);
        } else {
            newest.setTextColor(ResourcesUtils.getColor(R.color.color_999999));
            hottest.setTextColor(ResourcesUtils.getColor(R.color.color_333333));
            newestCheck.setVisibility(View.GONE);
            hottestCheck.setVisibility(View.VISIBLE);
        }

        newest.setOnClickListener(v -> {
            sortType = DEFAULT_NEWEST_SORT;
            GVideoEventBus.get(CIRCLE_HOTTEST_FRAGMENT_UPDATE).post(sortType);
            tabLayout.updateLeftItemText(0, ResourcesUtils.getString(R.string.space_newest));
            dismissPhoneWindow(window, tabLayout, shadow);
            userHasSelectSortType = true;
        });

        hottest.setOnClickListener(v -> {
            sortType = DEFAULT_RECOMMEND_SORT;
            GVideoEventBus.get(CIRCLE_HOTTEST_FRAGMENT_UPDATE).post(sortType);
            tabLayout.updateLeftItemText(0, ResourcesUtils.getString(R.string.space_hot_chat));
            dismissPhoneWindow(window, tabLayout, shadow);
            userHasSelectSortType = true;
        });

        showPopupWindow(window, tabLayout);
    }

    public void dismissPhoneWindow(PopupWindow window, ViewPagerTab tabLayout, View shadow) {
        window.dismiss();
        shadow.setVisibility(View.INVISIBLE);
        tabLayout.updateLeftItemDrawable(
                0,
                null,
                null,
                ContextCompat.getDrawable(tabLayout.getContext(), R.drawable.icon_arrow_down),
                null,
                ResourcesUtils.getIntDimens(R.dimen.DIMEN_4DP)
        );
    }

    public void showAvatarEntryDialog(Context context) {
        if (context == null) {
            return;
        }
        if (mAvatarEntryDialog == null) {
            mAvatarEntryDialog = new GVideoBottomSheetItemDialog.Builder(context)
                    .addItem(R.string.fragment_modify_avatar_entry_gallery)
                    .addItem(R.string.fragment_modify_avatar_entry_camera)
                    .cancel(R.string.dialog_back)
                    .itemSelectedListener(listener)
                    .build();
        }
        if (!mAvatarEntryDialog.isShowing()) {
            mAvatarEntryDialog.show();
        }
    }

    public void showAdminCanNotExitCircleDialog(Context context) {
        if (context == null) {
            return;
        }
        if (adminExitTipDialog == null) {
            adminExitTipDialog = new DefaultEnsureDialog(context);
            adminExitTipDialog.init(
                    v -> adminExitTipDialog.dismiss(),
                    ResourcesUtils.getString(R.string.admin_exit_circle_tip),
                    ""
            );
        }
        adminExitTipDialog.show();
    }

    /**
     * ????????????
     */
    public void showExitWillLostDataDialog(View view) {
        Context context = view.getContext();
        CircleDetail circleDetailValue = circleDetail.getValue();
        if (context == null || circleDetailValue == null) {
            return;
        }
        if (circleDetailValue.admin) {
            //???????????????????????????
            showAdminCanNotExitCircleDialog(context);
        } else {

            Long tenantId = circle == null ? null : circle.tenantId;
            String tenantName = circle == null ? null : circle.tenantName;

            if (exitTipDialog == null) {
                exitTipDialog = new DefaultEnsureCancelDialog(context);
                exitTipDialog.init(
                        v -> exitTipDialog.dismiss(),
                        v -> exitCircle(
                                circleDetailValue.groupId,
                                circleDetailValue.name,
                                tenantId,
                                tenantName,
                                exitTipDialog,
                                circleDetailValue.labels),
                        "",
                        ResourcesUtils.getString(R.string.exit_circle_tip)
                );
            }
            exitTipDialog.show();
        }
    }

    private final GVideoBottomSheetItemDialog.OnItemSelectedListener listener = (dialog, position) -> {
        if (position == AvatarEntry.CAMERA - 1) {
            takePicture(dialog.getContext());
        } else if (position == AvatarEntry.GALLERY - 1) {
            selectPictureFromGallery();
        }
    };

    private void getBannerList() {
        PluginManager.get(FeedPlugin.class).getBannerList((int) circle.groupId)
                .subscribe(new GVideoResponseObserver<BannerModel>() {
                    @Override
                    protected void onSuccess(@NonNull BannerModel bannerModel) {
                        mBannerList.setValue(BannerModel.isDataValid(bannerModel) ? bannerModel : null);
                        setOperateView();
                        updatePublishButton.setValue(true);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        mBannerList.setValue(null);
                        setOperateView();
                        updatePublishButton.setValue(true);
                    }
                });
    }

    /**
     * ?????????????????????
     */
    private void setOperateView() {
        if (circleDetail.getValue() == null) {
            return;
        }
        if (circleDetail.getValue().broadcastDetail != null) {
            showAudio.set(true);
            showBanner.set(false);
            showQAGroup.set(false);
            showTopic.set(false);
            return;
        }
        showAudio.set(false);
        if (mBannerList.getValue() != null) {
            boolean show = BannerModel.isDataValid(mBannerList.getValue());
            showBanner.set(show);
            if (show) {
                showQAGroup.set(false);
                return;
            }
        }
        CircleDetail circleDetailValue = circleDetail.getValue();
        boolean isShowQa = false;
        QaGroupListModel answerDetailListVO = circleDetailValue.answerDetailListVO;
        if (answerDetailListVO != null) {
            isShowQa = answerDetailListVO.hasValidData();
        }
        if (isShowQa) {
            showQAGroup.set(answerDetailListVO.isQAGroupType());
            showQATeacher.set(!showQAGroup.get());
        }
        if (isShowQa) {
            showTopic.set(false);
            return;
        }
        if (circleDetailValue.topicDetail != null) {
            showTopic.set(true);
            showTopicDetail.set(circleDetailValue.topicDetail);
        } else {
            showTopic.set(false);
        }
    }

    public String getCircleName() {
        CircleDetail circleDetailValue = circleDetail.getValue();
        if (circleDetailValue == null) {
            if(circle==null){
                return "";
            }
            return TextUtils.isEmpty(circle.name) ? "" : circle.name;
        }
        return TextUtils.isEmpty(circleDetailValue.name) ? "" : circleDetailValue.name;
    }

    /**
     * ????????????????????????
     */
    public void onCircleFamousClicked(View v) {
        if (circleDetail.getValue() == null) {
            return;
        }
        new CirclePluginImpl().navigationToCircleFamousList(v, circleDetail.getValue().groupId);
    }

    public void onIntroductionClicked(View view) {
        if (circleDetail.getValue() == null) {
            return;
        }
        CircleIntroductionDialog dialog = new CircleIntroductionDialog(view.getContext());
        dialog.setData(circleDetail.getValue().introduction);
        dialog.show();
    }

    /**
     * ????????????
     */
    public void onQaPublishClicked(View view) {
        isWantToPublishQA = true;
        if (isCanPublish(view)) {
            PluginManager.get(CirclePlugin.class).navigationQAAnswerListFragment(view, circle, circle.name);
        }
    }

    /**
     * ???????????????????????????
     */
    public void onQAGroupALlClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CIRCLE, circle);
        bundle.putString(Constant.EXTRA_FROM_PID, circle.name);
        CirclePlugin circlePlugin=PluginManager.get(CirclePlugin.class);
        if (showQATeacher.get()) {
            circlePlugin.navigationQAAnswerListFragment(view, circle, circle.name);
        } else {
            circlePlugin.startQAGroupActivity(view, bundle);
        }
    }

    public void onTopicItemClicked(View view) {
        PluginManager.get(CirclePlugin.class).startTopicDetailWithActivity(view.getContext(),
                circle, showTopicDetail.get());
    }

    private boolean isCanPublish(View view) {
        if (!UserManager.hasLoggedIn()) {
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (accountPlugin != null) {
                accountPlugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPid()),
                        ResourcesUtils.getString(R.string.publish)
                );
            }
            return false;
        }
        // ?????????????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????
        if (!circle.isJoin()) {
            joinCircle(view, true);
            return false;
        }
        return true;
    }

    public void onShareClicked(View v) {
        if (circleDetail.getValue() == null) {
            return;
        }
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setTitle(circleDetail.getValue().name)
                .setDescription(circleDetail.getValue().introduction)
                .setImage(circleDetail.getValue().cover.url)
                .setUrl(circleDetail.getValue().shareUrl)
                .setShowCreateBill(false)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false,
                true, shareDataModel, null);
    }

    public Bundle getCommonArgumentsSendToChild() {
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_NAME, getCircleName());
        if (circle != null) {
            bundle.putLong(TENANT_ID, circle.tenantId == null ? -1 : circle.tenantId);
            bundle.putString(TENANT_NAME, circle.tenantName);
        }
        return bundle;
    }

}
