package com.jxntv.base.view.recyclerview;

import android.app.Application;
import android.text.TextUtils;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.view.recyclerview.interf.IBaseRecyclerView;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.utils.AsyncUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * recyclerVM基类
 *
 *
 * @since 2020.1.17
 */
public abstract class BaseRecyclerViewModel<MODEL> extends BaseViewModel
        implements RecyclerViewLoadListener<MODEL> {

    /** 数据更新类型：插入数据 */
    public static final int LOAD_DATA_TYPE_INSERT = 0;
    /** 数据更新类型：移除数据 */
    public static final int LOAD_DATA_TYPE_REMOVE = 1;
    /** 数据更新类型：更新数据  */
    public static final int LOAD_DATA_TYPE_UPDATE = 2;
    /** 数据更新类型：刷新数据 */
    public static final int LOAD_DATA_TYPE_REFRESH = 3;
    /** 数据更新类型：初始化数据 */
    public static final int LOAD_DATA_TYPE_INITIAL = 4;
    /** 数据更新类型：拉取更多数据 */
    public static final int LOAD_DATA_TYPE_LOAD_MORE = 5;

    @IntDef({LOAD_DATA_TYPE_INSERT, LOAD_DATA_TYPE_REMOVE, LOAD_DATA_TYPE_UPDATE, LOAD_DATA_TYPE_REFRESH,
            LOAD_DATA_TYPE_INITIAL, LOAD_DATA_TYPE_LOAD_MORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadType {
    }

    /** 对应的数据适配器 */
    protected BaseRecyclerAdapter<MODEL, ?> mAdapter;
    /** 绑定的数据模型 */
    protected IRecyclerModel<MODEL> mModel;
    /** 当前持有recyclerView的视图层 */
    protected IBaseRecyclerView mView;
    /** 当前加载页数 */
    protected int currPage = 1;
    /** 加载类型定义 */
    protected @LoadType int loadType;

    public BaseRecyclerViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(BaseRecyclerAdapter<MODEL, ?> adapter, IBaseRecyclerView view) {
        mAdapter = adapter;
        mView = view;
        mModel = createModel();
    }

    /**
     * 创建数据模型，交由子类实现
     *
     * @return 绑定的数据模型
     */
    protected abstract IRecyclerModel<MODEL> createModel();

    /**
     * 获取当前加载类型
     *
     * @return 加载类型
     */
    public int getLoadType() {
        return loadType;
    }

    /**
     * 删除指定position的数据
     *
     * @param position  数据位置
     */
    protected void remove(int position) {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_REMOVE;
        mModel.remove(position, this);
    }

    /**
     * 将数据插入到指定position
     *
     * @param position  数据位置
     * @param model     数据模型
     */
    protected void insert(int position, MODEL model) {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_INSERT;
        mModel.insert(position, model, this);
    }

    /**
     * 更新指定position的数据
     *
     * @param position  数据位置
     * @param model     数据模型
     */
    protected  void update(int position, MODEL model) {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_UPDATE;
        mModel.update(position, model, this);
    }


    /**
     * 初始化数据
     */
    public void initialData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_INITIAL;
        currPage = 1;
        mModel.initialData(this);
    }

    /**
     * 刷新数据
     */
    public void loadRefreshData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_REFRESH;
        currPage = 1;
        mModel.loadData(currPage, this);
    }

    /**
     * 上拉加载更多数据
     */
    public void loadMoreData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_LOAD_MORE;
        currPage++;
        mModel.loadData(currPage, this);
    }

    /** 请求之前检查是否已登陆 */
    protected boolean needCheckLoginBeforeLoad() {
        return false;
    }
    /** 请求执之前进行检查，网络连接、登陆等 */
    protected  boolean checkBeforeLoad() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            return false;
        }
        if (needCheckLoginBeforeLoad() && TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken())) {
            updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    public void loadSuccess(List<MODEL> list) {
        switch (loadType) {
            case LOAD_DATA_TYPE_INSERT:
            case LOAD_DATA_TYPE_REMOVE:
            case LOAD_DATA_TYPE_UPDATE:
            case LOAD_DATA_TYPE_REFRESH:
            case LOAD_DATA_TYPE_INITIAL: //第一次加载或者下拉刷新的数据
                mAdapter.refreshData(list);
                break;
            case LOAD_DATA_TYPE_LOAD_MORE:
                if (currPage > 1) {
                    //上拉加载的数据
                    mAdapter.loadMoreData(list);
                }
                break;
        }
        if (mAdapter.getItemCount() > 0) {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
        }
    }

    @Override
    public void loadFailure(Throwable throwable) {
        if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
            // 加载失败后的提示
            if (currPage > 1) {
                //加载失败需要回到加载之前的页数
                currPage--;
            }
        }
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.loadFailure(throwable);
            }
        });
    }

    @Override
    public void loadStart() {
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.loadStart(loadType);
            }
        });
    }

    @Override
    public void loadComplete() {
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                finishRefresh(loadType == LOAD_DATA_TYPE_REFRESH);
                if (getPlaceholderType() == PlaceholderType.LOADING) {
                    if (mAdapter.getItemCount() > 0) {
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    } else {
                        updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                    }
                }
            }
        });
    }

    protected void hasMoreData(Boolean moreData) {
        if (!moreData){
            currPage -- ;
            if (currPage < 0){
                currPage = 0;
            }
        }
        hasMoreData.setValue(moreData);
    }

    private MutableLiveData<Boolean> mFinishRefreshLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getFinishRefreshLiveData() {
        return mFinishRefreshLiveData;
    }
    private void finishRefresh(boolean scrollToTop) {
        mFinishRefreshLiveData.setValue(scrollToTop);
    }

    private MutableLiveData<Boolean> hasMoreData = new MutableLiveData<>();
    public LiveData<Boolean> getHasMoreDataLiveData() {
        return hasMoreData;
    }

}
