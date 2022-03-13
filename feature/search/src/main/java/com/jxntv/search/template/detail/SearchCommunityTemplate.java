package com.jxntv.search.template.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ViewDataBinding;

import com.jxntv.base.StaticParams;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.InteractDataObservable;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.search.BR;
import com.jxntv.search.R;
import com.jxntv.search.databinding.SearchCommunityBinding;
import com.jxntv.search.databinding.SearchResultDataBinding;
import com.jxntv.search.model.ISearchModel;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.template.SearchBaseTemplate;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * 搜索社区类模板
 */
public class SearchCommunityTemplate extends SearchBaseTemplate {

    /**
     * 搜索社区类类模板binding
     */
    private SearchCommunityBinding mBinding;
    /**
     * 搜索关注类模板通用处理data binding
     */
    private SearchResultDataBinding mDataBinding;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     * @param parent  父容器
     */
    public SearchCommunityTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_community,
                parent, false);
    }

    @Override
    public void update(ISearchModel searchModel) {
        if (!(searchModel instanceof SearchDetailModel)) {
            return;
        }
        SearchDetailModel model = (SearchDetailModel) searchModel;

        if (mDataBinding == null) {
            mDataBinding = new SearchResultDataBinding();
        }
        mBinding.setVariable(BR.authorBind, mDataBinding);
        mBinding.setVariable(BR.model, model);
        mBinding.executePendingBindings();

        model.getJoinCircleObservable().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (sender == model.getJoinCircleObservable()) {
                    setFollowButton(model.getJoinCircleObservable().get());
                }
            }
        });

        mBinding.buttonFollow.setTag(model);
        mBinding.buttonFollow.setOnClickListener(view -> {

            Object object=view.getTag();
            if(object==null){
                return;
            }
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (!accountPlugin.hasLoggedIn()) {
                accountPlugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(((SearchDetailModel)object).getPid()),
                        ResourcesUtils.getString(R.string.follow)
                );
                return;
            }

            if (model.getJoinCircleObservable().get() || InteractDataObservable.getInstance().getJoinCircleObservable(model.getId()).get()) {
                mDataBinding.onCommunityClick(mBinding.buttonFollow, model);
            } else {
                long id = Long.parseLong(model.getId());
                Circle circle = new Circle();
                circle.groupId = id;
                circle.setName(model.getTitle());
                circle.tenantId = model.tenantId;
                circle.tenantName = model.tenantName;
                circle.setLabels(model.getLabels());
                PluginManager.get(CirclePlugin.class).joinCircle(circle,StatPid.SEARCH,true).subscribe(aBoolean -> {
                    if (aBoolean){
                        mDataBinding.onCommunityClick(mBinding.buttonFollow, model);
                    }
                });
            }
        });

        setFollowButton(model.getJoinCircleObservable().get());

        super.update(searchModel);
    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.textViewName;
    }

    @Override
    protected TextView getTagTextView() {
        return null;
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    private void setFollowButton(boolean isFollow) {
        Context context = mBinding.getRoot().getContext();
        int followColor = ContextCompat.getColor(context, R.color.color_e4344e);
        int normalColor = ContextCompat.getColor(context, R.color.color_7f7f7f);
        Drawable drawable = mBinding.getRoot().getContext().getResources().getDrawable(R.drawable.ic_add_red);
        int dp10 = mBinding.getRoot().getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_10DP);
        drawable.setBounds(0, 0, dp10, dp10);
        if (isFollow) {
            mBinding.buttonFollow.getHelper().setBorderColorNormal(normalColor);
            mBinding.buttonText.setTextColor(normalColor);
            mBinding.buttonText.setText(R.string.enter_community);
            mBinding.buttonText.setCompoundDrawables(null, null, null, null);
        } else {
            mBinding.buttonFollow.getHelper().setBorderColorNormal(followColor);
            mBinding.buttonText.setTextColor(followColor);
            mBinding.buttonText.setText(R.string.join_community);
            mBinding.buttonText.setCompoundDrawables(drawable, null, null, null);
        }

    }

    @Override
    public void mute(boolean value) {

    }
}
