package com.hzlz.aviation.feature.live.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.feature.live.ui.widget.MicroConnectAnchorView;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.live.R;

import java.util.HashMap;
import java.util.List;

/**
 * 对上麦观众列表View的管理类
 */
public class MicroConnectAnchorViewHelper {

    // 用于区分是大主播还是小主播
    public int liveType;

    // Index、MicroConnectAnchorView的键值对
    public HashMap<Integer, MicroConnectAnchorView> map = new HashMap<>();

    // 操作监听
    private OperationListener operationListener;

    private volatile static MicroConnectAnchorViewHelper singleInstance = null;

    private MicroConnectAnchorViewHelper() {
    }

    public static MicroConnectAnchorViewHelper getInstance() {
        if (singleInstance == null) {
            synchronized (MicroConnectAnchorViewHelper.class) {
                if (singleInstance == null) {
                    singleInstance = new MicroConnectAnchorViewHelper();
                }
            }
        }
        return singleInstance;
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()){
                PluginManager.get(AccountPlugin.class).startLoginActivity(v.getContext());
                return;
            }
            if (operationListener == null) {
                return;
            }
            MicroConnectAnchorView view = (MicroConnectAnchorView) v;
            if (view.anchorInfo == null) {
                Object object = v.getTag();
                if (object == null) {
                    return;
                }
                operationListener.onEmptyPositionClick((Integer) object);
            } else {
                operationListener.onAudienceClick(view.anchorInfo);
            }
        }
    };

    public void init(FrameLayout root, int liveType) {
        this.liveType = liveType;

        MicroConnectAnchorView first = root.findViewById(R.id.first);
        first.setTag(0);
        first.setOnClickListener(onClickListener);
        map.put(0, first);

        MicroConnectAnchorView second = root.findViewById(R.id.second);
        second.setOnClickListener(onClickListener);
        second.setTag(1);
        map.put(1, second);

        MicroConnectAnchorView third = root.findViewById(R.id.third);
        third.setOnClickListener(onClickListener);
        third.setTag(2);
        map.put(2, third);

        MicroConnectAnchorView four = root.findViewById(R.id.four);
        four.setOnClickListener(onClickListener);
        four.setTag(3);
        map.put(3, four);

        MicroConnectAnchorView five = root.findViewById(R.id.five);
        five.setOnClickListener(onClickListener);
        five.setTag(4);
        map.put(4, five);

        MicroConnectAnchorView six = root.findViewById(R.id.six);
        six.setOnClickListener(onClickListener);
        six.setTag(5);
        map.put(5, six);
    }

    public void updateDataSource(List<AnchorInfo> dataSource) {
        if (dataSource == null || dataSource.isEmpty()) {
            for (MicroConnectAnchorView view : map.values()) {
                view.init();
            }
            return;
        }
        for (AnchorInfo anchorInfo : dataSource) {
            if (anchorInfo == null) {
                continue;
            }
            MicroConnectAnchorView view = map.get(anchorInfo.index);
            if (view == null) {
                continue;
            }
            view.update(anchorInfo);
        }
    }

    public void updateDataSourceWithPosition(AnchorInfo anchorInfo) {
        if (anchorInfo == null) {
            return;
        }
        MicroConnectAnchorView view = map.get(anchorInfo.index);
        if (view == null) {
            return;
        }
        view.update(anchorInfo);
    }

    public AnchorInfo getItemDataWithUseId(String useId) {
        for (MicroConnectAnchorView view : map.values()) {
            if (view == null
                    || view.anchorInfo == null
                    || !TextUtils.equals(useId, view.anchorInfo.userid)) {
                continue;
            }
            return view.anchorInfo;
        }
        return null;
    }

    public View getChildAt(int index) {
        return map.get(index);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public interface OperationListener {
        void onAudienceClick(AnchorInfo anchorInfo);

        void onEmptyPositionClick(int position);
    }

}
