package com.jxntv.circle.fragment.fxa;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;

import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.R;
import com.jxntv.circle.model.FXAModel;
import com.jxntv.circle.model.FXAType;
import com.jxntv.utils.AppManager;
import com.jxntv.widget.GVideoRTextView;

/**
 * @author huangwei
 * date : 2021/7/30
 * desc : 放心爱
 **/
public class FXAViewModel extends BaseViewModel {

    public CheckThreadLiveData<FXAModel> dataLiveData = new CheckThreadLiveData<>();

    public CheckThreadLiveData<Boolean> canInput = new CheckThreadLiveData<>();
    private CircleRepository repository = new CircleRepository();

    public ObservableField<Boolean> hasCanInput = new ObservableField<>();
    public ObservableField<String> btnString = new ObservableField<>();
    @NonNull
    public ObservableField<String> inputNumber = new ObservableField<>();

    public FXAViewModel(@NonNull Application application) {
        super(application);
    }

    public void initData(String id) {

        repository.getFXAData(id).subscribe(new GVideoResponseObserver<FXAModel>() {

            @Override
            protected void onSuccess(@NonNull FXAModel model) {

                if (model == null || model.getMember() == null){
                    ToastUtils.showShort("您还未报名无法参加活动！");
                    AppManager.getAppManager().finishActivity();
                    return;
                }
                dataLiveData.setValue(model);
                hasCanInput.set(model.isCanClick() || model.getMember().getCurrentPart().getStatus() == 0);
                btnString.set(dataLiveData.getValue().statusString());
                if (model.getMember().getPairInfo()!=null){
                    inputNumber.set(model.getMember().getPairInfo().getSelectMemberCode());
                }
                canInput.setValue(model.isCanClick());
            }

            @Override
            public void onError(Throwable throwable) {
                showToast(throwable.getMessage());
            }
        });

    }

    public void btnClicked(View view) {

        if (dataLiveData.getValue().getMember().getCurrentPart().getPartType() == FXAType.SIGN) {
            repository.fxaSign(dataLiveData.getValue().getMember().getId()).subscribe(getResponseObserver(view));
        } else {
            if (TextUtils.isEmpty(inputNumber.get())){
                showToast("请输入号码");
                return;
            }
            repository.fxaPair(dataLiveData.getValue().getMember().getId(), inputNumber.get()).subscribe(getResponseObserver(view));
        }
    }

    public GVideoResponseObserver<Object> getResponseObserver(View view){
        return new GVideoResponseObserver<Object>() {
            @Override
            protected void onSuccess(@NonNull Object o) {
                dataLiveData.getValue().getMember().getCurrentPart().setStatus(1);
                dataLiveData.getValue().getMember().setSignInfo(new FXAModel.MemberBean.SignInfoBean());
                dataLiveData.getValue().getMember().setPairInfo(new FXAModel.MemberBean.PairInfoBean());
                hasCanInput.set(false);
                btnString.set(dataLiveData.getValue().statusString());
                canInput.setValue(false);
                if (view instanceof GVideoRTextView){
                    GVideoRTextView textView = (GVideoRTextView) view;
                    textView.setEnabled(false);
                    textView.getHelper().setBackgroundColorNormal(ContextCompat.getColor(view.getContext(), R.color.color_d8d8d8));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                showToast(throwable.getMessage());
            }
        };
    }
}


