package com.hzlz.aviation.feature.live.dialog;

import android.content.Context;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.live.adapter.AnchorInfoAdapter;
import com.hzlz.aviation.feature.live.callback.OnMicroConnectRequestListListener;
import com.hzlz.aviation.feature.live.databinding.DialogMicroConnectRequestListBinding;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.live.R;

/**
 * 主播端观众请求连麦列表
 */
public class MicroConnectRequestListDialog extends GVideoBottomSheetDialog {

    private DialogMicroConnectRequestListBinding mLayoutBinding;

    private OnMicroConnectRequestListListener onMicroConnectRequestListListener;

    private AnchorInfoAdapter anchorInfoAdapter;

    public MicroConnectRequestListDialog(Context context) {
        super(context);
        onCreateView();
    }

    /**
     * 设置连麦反馈监听
     */
    public void setConnectListener(OnMicroConnectRequestListListener listener){
        this.onMicroConnectRequestListListener = listener;

    }

    public void clear(){
        anchorInfoAdapter.clearList();
    }


    public void addData(AnchorInfo data){
        anchorInfoAdapter.updateAnchorInfo(data);
    }

    /**
     * 更新状态
     */
    public void updateAnchorInfo(AnchorInfo info) {
        anchorInfoAdapter.updateAnchorInfo(info);
    }

    /**
     * 设置无效申请
     * @param uerId  申请用户id
     */
    public void setInvalidAnchorInfo(String uerId){
        anchorInfoAdapter.setInvalidAnchorInfo(uerId);
    }

    private void onCreateView() {

        mLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_micro_connect_request_list,
                null, false);

        mLayoutBinding.close.setOnClickListener(v -> {
            if (onMicroConnectRequestListListener == null) {
                return;
            }
            onMicroConnectRequestListListener.onCloseClick();
        });

        anchorInfoAdapter = new AnchorInfoAdapter(getContext());
        mLayoutBinding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        mLayoutBinding.list.setAdapter(anchorInfoAdapter);

        anchorInfoAdapter.setOnClickListener((anchorInfo, position) -> {
            if (onMicroConnectRequestListListener == null) {
                return;
            }
            onMicroConnectRequestListListener.onAcceptClick(anchorInfo, position);
        });
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        setContentView(mLayoutBinding.getRoot(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
