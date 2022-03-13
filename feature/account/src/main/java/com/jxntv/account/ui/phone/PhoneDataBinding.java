package com.jxntv.account.ui.phone;

import static com.jxntv.base.Constant.COUNTRY_CODE.CHINA;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.jxntv.account.R;
import com.jxntv.account.model.annotation.SmsCodeType;
import com.jxntv.utils.ResourcesUtils;

/**
 * 手机号界面数据绑定
 *
 * @since 2020-01-14 10:32
 */
@SuppressWarnings("FieldCanBeLocal")
public final class PhoneDataBinding {

    //<editor-fold desc="属性">
    @NonNull
    public ObservableField<String> title = new ObservableField<>();
    @NonNull
    public ObservableField<String> countryCode = new ObservableField<>(CHINA);
    @NonNull
    public ObservableField<String> phoneNumber = new ObservableField<>();
    @NonNull
    public ObservableInt tipVisibility = new ObservableInt(View.VISIBLE);
    @NonNull
    public ObservableBoolean enableNext = new ObservableBoolean();
    @NonNull
    public ObservableBoolean isCheck = new ObservableBoolean(false);
    //</editor-fold>

    //<editor-fold desc="构造函数">
    PhoneDataBinding() {
        countryCode.addOnPropertyChangedCallback(mPropertyChangedCallback);
        phoneNumber.addOnPropertyChangedCallback(mPropertyChangedCallback);
    }
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 设置类型
     *
     * @param type 类型
     */
    void setType(@SmsCodeType int type) {
        switch (type) {
            case SmsCodeType.REBIND_PHONE:
                title.set(ResourcesUtils.getString(R.string.fragment_phone_rebind));
                tipVisibility.set(View.GONE);
                break;
            case SmsCodeType.LOGIN:
            case SmsCodeType.SWITCH_ACCOUNT:
            default:
                title.set(ResourcesUtils.getString(R.string.fragment_phone_login));
                tipVisibility.set(View.VISIBLE);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="数据属性变化监听">
    private Observable.OnPropertyChangedCallback mPropertyChangedCallback =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (countryCode.equals(sender) || phoneNumber.equals(sender)) {
                        enableNext.set(enableNext());
                    }
                }
            };

    /**
     * 是否启用下一步按钮
     *
     * @return true : 启用
     */
    private boolean enableNext() {
        if (!isCheck.get() && tipVisibility.get() == View.VISIBLE) {
            return false;
        }
        return isNumberValid();
    }

    public void onCheckClick(View view) {
        isCheck.set(!isCheck.get());
        enableNext.set(enableNext());
    }

    public boolean isNumberValid(){
        String cc = countryCode.get();
        cc = cc == null ? null : cc.trim();
        String p = phoneNumber.get();
        p = p == null ? null : p.trim();
        if (CHINA.equals(cc)) {
            return !TextUtils.isEmpty(p) && p.length() == 11;
        } else {
            return !TextUtils.isEmpty(p) && TextUtils.isDigitsOnly(p);
        }
    }

    //</editor-fold>
}
