package com.hzlz.aviation.feature.community.fragment.qa;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

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


