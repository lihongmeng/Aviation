package com.jxntv.circle.fragment.qa;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.circle.CircleRepository;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;
import com.jxntv.network.response.ListWithPage;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/9/6
 * desc : 问答广场
 **/
public class QAGroupViewModel extends MediaPageViewModel {

    private CircleRepository repository = new CircleRepository();
    private String groupId;

    public QAGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {

        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {

                repository.getQAGroupList(groupId,page).subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {

                    @Override
                    protected boolean isShowPlaceholderLayout() {
                        return page == 1 && mAdapter.getItemCount() == 0;
                    }

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<MediaModel> list){
                        List<MediaModel> modelList = list.getList();
                        for (MediaModel model: modelList){
                            model.tabId = mTabId;
                            model.setPid(getPid());
                        }
                        loadSuccess(list.getList());
                        loadComplete();
                        hasMoreData(list.getPage().hasNextPage());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        updatePlaceholderLayoutType(PlaceholderType.ERROR);
                        ToastUtils.showShort(throwable.getMessage());
                    }
                });
            }
        };
    }


}


