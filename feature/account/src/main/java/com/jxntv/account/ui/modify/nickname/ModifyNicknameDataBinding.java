package com.jxntv.account.ui.modify.nickname;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jxntv.account.R;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.utils.ResourcesUtils;

/**
 * 修改昵称数据绑定
 *
 * @since 2020-01-21 16:54
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ModifyNicknameDataBinding {
    //<editor-fold desc="属性">
    private int mMaxNicknameLength;
    @NonNull
    public ObservableField<String> nickname = new ObservableField<>();
    @NonNull
    public ObservableField<String> counter = new ObservableField<>();
    public ObservableBoolean counterMax = new ObservableBoolean();

    @NonNull
    private CheckThreadLiveData<Boolean> mModifyNicknameLiveData = new CheckThreadLiveData<>(false);

    @Nullable
    private String mOldNickname;

    public boolean confirmEnable;

    //</editor-fold>

    //<editor-fold desc="构造函数">
    ModifyNicknameDataBinding() {
        mMaxNicknameLength = ResourcesUtils.getInt(R.integer.max_nickname_length);
        counter.set("0/" + mMaxNicknameLength);
        nickname.addOnPropertyChangedCallback(mPropertyChangedCallback);
    }
    //</editor-fold>

    //<editor-fold desc="API">

    void setNickname(@Nullable String nickname) {
        mOldNickname = nickname;
        this.nickname.set(nickname);
    }

    @NonNull
    CheckThreadLiveData<Boolean> getModifyNicknameLiveData() {
        return mModifyNicknameLiveData;
    }
    //</editor-fold>

    //<editor-fold desc="数据属性监听">
    @NonNull
    private Observable.OnPropertyChangedCallback mPropertyChangedCallback =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (nickname.equals(sender)) {
                        updateCounter();
                        checkWeatherCanModifyNickname();
                    }
                }
            };

    private void updateCounter() {
        String nn = nickname.get();
        int length = nn == null ? 0 : nn.length();
        counter.set(length + "/" + mMaxNicknameLength);
        counterMax.set(length >= mMaxNicknameLength);
    }

    private void checkWeatherCanModifyNickname() {
        String n = nickname.get();
        mModifyNicknameLiveData.setValue(!TextUtils.isEmpty(n) && !n.equals(mOldNickname) && confirmEnable);
    }
    //</editor-fold>
}
