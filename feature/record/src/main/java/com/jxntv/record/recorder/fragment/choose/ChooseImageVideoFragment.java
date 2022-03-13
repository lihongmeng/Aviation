package com.jxntv.record.recorder.fragment.choose;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.decoration.GapItemDecoration;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.record.R;
import com.jxntv.record.RecordPluginImpl;
import com.jxntv.record.databinding.FragmentChooseImageVideoBinding;
import com.jxntv.record.recorder.helper.VideoChooseHelper;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

import java.util.ArrayList;

import static com.jxntv.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_ADD;
import static com.jxntv.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_REPLACE;
import static com.jxntv.record.recorder.Constants.SELECT_IMAGE_TYPE.SINGLE_PLACE;

/**
 * 选择视频页面
 */

public class ChooseImageVideoFragment extends BaseFragment<FragmentChooseImageVideoBinding> {

    /**
     * 当前fragment持有的view model
     */
    private ChooseImageVideoViewModel mChooseImageVideoViewModel;
    /**
     * 当前fragment持有的adapter
     */
    private ChooseImageVideoAdapter mChooseImageVideoAdapter;
    /**
     * 选择视频总数量text格式
     */
    private String mSelectNumFormat = GVideoRuntime.getAppContext().getString(R.string.choose_video_num);
    /**
     * 选择视频总时长text格式
     */
    private String mSelectTimeFormat = GVideoRuntime.getAppContext().getString(R.string.choose_video_time);

    /**
     * 不能选择的数据
     */
    private ArrayList<String> canNotSelectList = new ArrayList<>();

    /**
     * 是否想要选择video
     */
    private boolean isVideo = false;

    /**
     * 启动此页面的操作类型
     */
    private int operationType;

    /**
     * 如果是单项替换，需要替换的位置
     */
    private int singleOperationIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_choose_image_video;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ImmersiveUtils.enterImmersive(this, Color.WHITE, true);
    }

    private void initVars() {
        Bundle arguments = getArguments();
        operationType = BATCH_REPLACE;
        singleOperationIndex = -1;
        if (arguments != null) {
            isVideo = arguments.getBoolean(RecordPluginImpl.INTENT_IS_VIDEO_TYPE, false);
            operationType = arguments.getInt(RecordPluginImpl.INTENT_SELECT_IMAGE_OPERATION_TYPE, BATCH_REPLACE);
            singleOperationIndex = arguments.getInt(RecordPluginImpl.INTENT_SINGLE_OPERATION_INDEX, -1);

            canNotSelectList = arguments.getStringArrayList(RecordPluginImpl.INTENT_CAN_NOT_SELECT_LIST);

            switch (operationType) {
                case SINGLE_PLACE:
                    VideoChooseHelper.IMAGE_NUM_LIMIT = 1;
                    break;
                case BATCH_ADD:
                    if (canNotSelectList == null || canNotSelectList.isEmpty()) {
                        VideoChooseHelper.IMAGE_NUM_LIMIT = VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;
                    } else {
                        VideoChooseHelper.IMAGE_NUM_LIMIT = VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM - canNotSelectList.size();
                        if (VideoChooseHelper.IMAGE_NUM_LIMIT <= 0) {
                            VideoChooseHelper.IMAGE_NUM_LIMIT = VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;
                        }
                    }
                    break;
                case BATCH_REPLACE:
                default:
                    VideoChooseHelper.IMAGE_NUM_LIMIT = VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;
            }


        }
    }

    @Override
    protected void initView() {
        initVars();

        Context context = getContext();
        if (context == null) {
            return;
        }
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        GapItemDecoration decoration = new GapItemDecoration();
        decoration.setVerticalGap((int) ResourcesUtils.getDimens(R.dimen.choose_num_item_interval));
        decoration.setHorizontalGap((int) ResourcesUtils.getDimens(R.dimen.choose_num_item_interval));
        mBinding.recyclerView.addItemDecoration(decoration);
        mChooseImageVideoAdapter = new ChooseImageVideoAdapter();
        mChooseImageVideoAdapter.updateCanNotSelectList(canNotSelectList);
        mBinding.recyclerView.setAdapter(mChooseImageVideoAdapter);

        mBinding.backChoose.setOnClickListener(v -> {
            boolean success = Navigation.findNavController(v).popBackStack();
            if (!success) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        mBinding.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooseImageVideoViewModel.onItemFinishClick(getActivity());
            }
        });
    }

    @Override
    protected void bindViewModels() {
        if (!isVideo) {
            mSelectNumFormat = GVideoRuntime.getAppContext().getString(R.string.choose_image_num);
            mBinding.selectNumTime.setVisibility(View.GONE);
        }
        mChooseImageVideoViewModel = bingViewModel(ChooseImageVideoViewModel.class);
        mChooseImageVideoViewModel.setIsVideo(isVideo);
        mChooseImageVideoAdapter.setChooseImageVideoViewModel(mChooseImageVideoViewModel);

        // ===============================================================
        // 因为通过EventBus解耦，需要在功能流程中一直携带operationType、singleReplaceIndex....
        // 所以这里只是做个中转，对选择图片的功能没什么影响

        // 记录上一层以什么目的启动图片选择
        mChooseImageVideoViewModel.setOperationType(operationType);

        // 记录上一层想要单张替换哪个位置
        mChooseImageVideoViewModel.setSingleOperationIndex(singleOperationIndex);

        //================================================================

        mBinding.setViewModel(mChooseImageVideoViewModel);
        mChooseImageVideoViewModel.selectItemNum.observe(
                this,
                num -> {
                    if (num == null || num < 0) {
                        return;
                    }
                    mBinding.selectNumText.setVisibility(View.VISIBLE);
                    mBinding.selectNumText.setText(String.format(mSelectNumFormat, num));
                    mChooseImageVideoAdapter.notifyDataSetChanged();
                }
        );
        mChooseImageVideoViewModel.selectItemTime.observe(
                this,
                time -> {
                    if (time != null && time > 0) {
                        mBinding.selectNumTime.setVisibility(View.VISIBLE);
                        mBinding.selectNumTime.setText(String.format(mSelectTimeFormat, time));
                        mBinding.selectNumTime.setTextColor(ResourcesUtils.getColor(
                                VideoChooseHelper.getInstance().checkSelectAvailable() ? R.color.t_color01 : R.color.t_color06));
                    } else {
                        mBinding.selectNumTime.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    protected void loadData() {
        VideoChooseHelper.getInstance().clearSelectList();
        mChooseImageVideoViewModel.loadMediaVideo().observe(
                this,
                list -> mChooseImageVideoAdapter.updateList(list)
        );
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.SELECT_IMAGE_VIDEO;
    }

}
