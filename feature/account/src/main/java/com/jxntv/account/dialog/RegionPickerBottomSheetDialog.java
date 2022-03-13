package com.jxntv.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.account.R;
import com.jxntv.account.databinding.DialogItemViewRegionPickerBinding;
import com.jxntv.account.model.RegionModel;
import com.jxntv.account.model.User;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.dialog.GVideoBottomSheetDialog;
import com.jxntv.utils.AsyncUtils;

import java.util.List;

import static android.view.View.GONE;

/**
 * 地区选择弹窗
 */
public final class RegionPickerBottomSheetDialog extends GVideoBottomSheetDialog {

    private DialogItemViewRegionPickerBinding binding;
    private RegionListener listener;
    private int provinceId = 0, cityId = 0;
    private String province, city;

    public RegionPickerBottomSheetDialog(@NonNull Context context) {
        super(context);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_item_view_region_picker, null, false);

        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        setContentView(binding.getRoot(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.DIMEN_332DP)));

        binding.provincePicker.setOnItemSelectedListener((picker, data, position) -> {
            provinceId = ((RegionModel) data).getId();
            province = ((RegionModel) data).getName();
            cityId = ((RegionModel) data).getChild().get(0).getId();
            city = ((RegionModel) data).getChild().get(0).getName();
            binding.cityPicker.setData(((RegionModel) data).getChild());
        });

        binding.cityPicker.setOnItemSelectedListener((picker, data, position) -> {
            cityId = ((RegionModel) data).getId();
            city = ((RegionModel) data).getName();
        });

        binding.cancel.setOnClickListener(view -> dismiss());

        binding.confirm.setOnClickListener(view -> {
            if (listener != null) {
                listener.change(provinceId, province, cityId, city);
            }
            dismiss();
        });
    }

    public void setData(List<RegionModel> regionModels, RegionListener listener) {
        this.listener = listener;
        binding.provincePicker.setData(regionModels);
        binding.cityPicker.setData(regionModels.get(0).getChild());
        province = regionModels.get(0).getName();
        city = regionModels.get(0).getChild().get(0).getName();
        User user = UserManager.getCurrentUser();
        int i = 0, j = 0;
        for (i = 0; i < regionModels.size(); i++) {
            if (regionModels.get(i).getId() == user.getProvinceId()) {
                provinceId = regionModels.get(i).getId();
                province = regionModels.get(i).getName();

                binding.cityPicker.setData(regionModels.get(i).getChild());
                for (j = 0; j < regionModels.size(); j++) {
                    if (regionModels.get(i).getChild().get(j).getId() == user.getCityId()) {
                        cityId = regionModels.get(i).getId();
                        city = regionModels.get(i).getName();
                        break;
                    }
                }
                break;
            }
        }
        int finalI = i;
        int finalJ = j;
        //需要数据加载后才能设置位置，在此延迟处理
        AsyncUtils.runOnUIThread(() -> {
            binding.provincePicker.setSelectedItemPosition(finalI, true);
            binding.cityPicker.setSelectedItemPosition(finalJ, true);
        },100);

    }

    public interface RegionListener {
        void change(int provinceId, String province, int cityId, String city);
    }

}
