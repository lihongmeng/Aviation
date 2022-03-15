package com.hzlz.aviation.feature.share.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.share.adapter.ShareSlideAdapter;
import com.hzlz.aviation.feature.share.data.ShareDataManager;
import com.hzlz.aviation.feature.share.model.ShareItemModel;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.share.R;
import com.hzlz.aviation.feature.share.databinding.LayoutShareDialogBinding;

import java.util.List;

/**
 * 分享dialog
 */
public class ShareDialog extends GVideoBottomSheetDialog {

    /**
     * 是否为暗黑模式
     */
    private boolean mIsDarkMode;

    /**
     * 持有的dataBind
     */
    private LayoutShareDialogBinding mLayoutBinding;

    /**
     * 是否显示other
     */
    private boolean isHideMore;

    /**
     * 构造方法
     */
    public ShareDialog(
            Context context,
            boolean isDarkMode,
            boolean isHideMore,
            ShareDataModel model,
            StatFromModel stat
    ) {
        super(context);
        mIsDarkMode = isDarkMode;
        this.isHideMore = isHideMore;
        mLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_share_dialog,
                null,
                false
        );
        mLayoutBinding.setBinding(new ShareDataBind(mIsDarkMode));
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        initItem(context, model, stat);
        setContentView(
                mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    /**
     * 初始化内部按钮
     *
     * @param context 上下文
     */
    private void initItem(Context context, ShareDataModel model, StatFromModel stat) {
        if (model.isShowShare()) {
            mLayoutBinding.shareRecycler.setFocusable(false);
            mLayoutBinding.shareRecycler.setFocusableInTouchMode(false);
            mLayoutBinding.shareRecycler.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            ShareSlideAdapter adapter = new ShareSlideAdapter(context, mIsDarkMode, new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
            mLayoutBinding.shareRecycler.setAdapter(adapter);
            List<ShareItemModel> list = ShareDataManager.getDefaultShareModel(context, model, stat);
            if (list !=null && list.size()>0){
                adapter.refreshData(list);
            }else {
                mLayoutBinding.shareRecycler.setVisibility(View.GONE);
                mLayoutBinding.shareTitle.setVisibility(View.GONE);
                mLayoutBinding.shareInterval.setVisibility(View.GONE);
            }
        } else {
            mLayoutBinding.shareRecycler.setVisibility(View.GONE);
            mLayoutBinding.shareTitle.setVisibility(View.GONE);
            mLayoutBinding.shareInterval.setVisibility(View.GONE);
        }

        if (!isHideMore) {
            mLayoutBinding.moreRecycler.setVisibility(View.VISIBLE);
            mLayoutBinding.moreRecycler.setFocusable(false);
            mLayoutBinding.moreRecycler.setFocusableInTouchMode(false);
            mLayoutBinding.moreRecycler.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            ShareSlideAdapter moreAdapter = new ShareSlideAdapter(context, mIsDarkMode, new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
            mLayoutBinding.moreRecycler.setAdapter(moreAdapter);

            List<ShareItemModel> data = ShareDataManager.getDefaultOtherModel(context, model, stat);
            if (data.size() == 0){
                mLayoutBinding.moreRecycler.setVisibility(View.GONE);
                mLayoutBinding.shareInterval.setVisibility(View.GONE);
            }else {
                mLayoutBinding.moreRecycler.setVisibility(View.VISIBLE);
                moreAdapter.refreshData(ShareDataManager.getDefaultOtherModel(context, model, stat));
            }
        } else {
            mLayoutBinding.moreRecycler.setVisibility(View.GONE);
        }

        mLayoutBinding.shareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
