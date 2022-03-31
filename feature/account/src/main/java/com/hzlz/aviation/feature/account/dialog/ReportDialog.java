package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.DialogReportBinding;
import com.hzlz.aviation.feature.account.repository.ReportRepository;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

/**
 * 举报弹窗
 */
public class ReportDialog extends GVideoCenterDialog implements View.OnClickListener {

    /**
     * 持有的dataBind
     */
    private DialogReportBinding mLayoutBinding;
    private String[] dataRes = new String[]{"其他", "不实信息", "政治敏感", "违法犯罪", "金钱诈骗", "侵犯未成年",
            "垃圾广告", "抄袭侵权", "泄露隐私"};
    private String id;
    private int contentType;

    /**
     * 构造方法
     */
    public ReportDialog(@NonNull Context context,String id, int contentType) {
        super(context);
        this.id = id;
        this.contentType = contentType;
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_report, null, false);

        initView(context);
        setContentView(mLayoutBinding.getRoot(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(true);
        mLayoutBinding.close.setOnClickListener(view -> dismiss());

    }

    protected int getExpectWindowWidth() {
        return (int) (ScreenUtils.getScreenWidth(getContext()) * 0.75);
    }

    /**
     * 生成视图
     */
    private void initView(Context context) {
        int size = dataRes.length;
        for (int i = 1; i < size; i++) {
            AviationTextView item = new AviationTextView(context);
            item.setText(dataRes[i]);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            item.setTextColor(ContextCompat.getColor(context, R.color.color_7f7f7f));
            item.setTag(R.integer.bottom_sheet_item_dialog_tag, i);
            item.setOnClickListener(this);
            item.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    context.getResources().getDimensionPixelSize(R.dimen.DIMEN_42DP));
            mLayoutBinding.content.addView(item, itemParams);
            // 添加分割线
            View driver = new View(getContext());
            LinearLayout.LayoutParams driverParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    context.getResources().getDimensionPixelSize(R.dimen.DIMEN_D5P));
            driver.setBackgroundColor(ContextCompat.getColor(context, R.color.c_line02));
            mLayoutBinding.content.addView(driver, driverParams);
        }

        AviationTextView item = new AviationTextView(context);
        item.setText(dataRes[0]);
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        item.setTextColor(ContextCompat.getColor(context, R.color.color_7f7f7f));
        item.setTag(R.integer.bottom_sheet_item_dialog_tag, 0);
        item.setOnClickListener(this);
        item.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.DIMEN_42DP));
        mLayoutBinding.content.addView(item, itemParams);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag(R.integer.bottom_sheet_item_dialog_tag);
        if (tag instanceof Integer) {
            int index = (int) tag;
            report(index);
            dismiss();
        }
    }

    private void report(int type){

        new ReportRepository().report(type, contentType, id).subscribe(new BaseResponseObserver<Object>() {

            @Override
            protected void onRequestData(Object o) {
                ToastUtils.showShort("举报提交成功");
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                ToastUtils.showShort(throwable.getMessage());
            }
        });

    }
}
