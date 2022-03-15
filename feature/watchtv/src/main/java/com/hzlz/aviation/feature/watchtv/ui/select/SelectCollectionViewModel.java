package com.hzlz.aviation.feature.watchtv.ui.select;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SEND_GROUP_INFO;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SEND_VIDEO_DATA_TO_PLAY;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.ArrayList;
import java.util.List;

public class SelectCollectionViewModel extends BaseRecyclerViewModel<VideoModel> {

    public String columnId,programId;

    private int minPage = -1;

    private boolean insertData = false;

    // 第一次拉取的时候需要把第一条发给上层
    public boolean needSendFirstVideoDataOnce;

    // 需要将GroupInfo广播出去
    public boolean needSendGroupInfo;

    // 需要将GroupInfo发送出去对应的 VideoModel Id
    public String needSendGroupInfoVideoId;

    private WatchTvRepository repository = new WatchTvRepository();

    public SelectCollectionViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshData(){
        if (minPage > 1){
            //如果下拉请求数据时当前不是第一页，就请求上一页数据并插入对应位置
            currPage = currPage - 2;
            insertData = true;
            loadMoreData();
        }else {
            loadRefreshData();
        }
    }

    @Override
    protected IRecyclerModel<VideoModel> createModel() {
        return (page, loadListener) -> {
            repository.getColumnAllList(columnId,(needSendFirstVideoDataOnce?programId:null),page).subscribe(new GVideoResponseObserver<ListWithPage<VideoModel>>() {

                @Override
                protected boolean isShowPlaceholderLayout() {
                    return page == 1 && mAdapter.getItemCount() == 0;
                }

                @Override
                protected void onSuccess(@NonNull ListWithPage<VideoModel> listWithPage) {
                    //todo 数据插入加载需要修改
                    loadSuccess(listWithPage.getList());
                    currPage = listWithPage.getPage().getPageNum();
                    minPage = minPage == -1 ? listWithPage.getPage().getPageNum() : Math.min(listWithPage.getPage().getPageNum(), minPage);
                    hasMoreData(listWithPage.getPage().hasNextPage());
                    initListData(listWithPage.getList());
                }
            });
        };
    }

    @Override
    public void loadSuccess(List<VideoModel> list) {
        switch (loadType) {
            case LOAD_DATA_TYPE_INSERT:
            case LOAD_DATA_TYPE_REMOVE:
            case LOAD_DATA_TYPE_UPDATE:
            case LOAD_DATA_TYPE_REFRESH:
            case LOAD_DATA_TYPE_INITIAL: //第一次加载或者下拉刷新的数据
                mAdapter.refreshData(list);
                break;
            case LOAD_DATA_TYPE_LOAD_MORE:
                if (currPage > 1){
                    if (insertData){
                        mAdapter.addMoreData(list);
                    }else {
                        mAdapter.loadMoreData(list);
                    }
                }
                break;
        }
        insertData = false;
        if (mAdapter.getItemCount() > 0) {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
        }
    }

    private void initListData(List<VideoModel> list){
        if (list == null) {
            list = new ArrayList<>();
        }
        if (needSendFirstVideoDataOnce && !list.isEmpty() && mAdapter instanceof SelectCollectionAdapter) {
            needSendFirstVideoDataOnce = false;
            boolean hasData = false;
            if (!TextUtils.isEmpty(programId)){
                for (int i=0; i<list.size();i++){
                    if (TextUtils.equals(list.get(i).getId(),programId)){
                        GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY).post(list.get(i));
                        ((SelectCollectionAdapter) mAdapter).setCurrentProgramId(list.get(i).getId());
                        hasData = true;
                        break;
                    }
                }
            }
            if (!hasData){
                GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY).post(list.get(0));
                ((SelectCollectionAdapter) mAdapter).setCurrentProgramId(list.get(0).getId());
            }
        }

        if (needSendGroupInfo) {
            needSendGroupInfo = false;

            for (VideoModel videoModel : list) {
                if (videoModel == null) {
                    continue;
                }
                String id = videoModel.getId();
                if (TextUtils.isEmpty(id) || !id.equals(needSendGroupInfoVideoId)) {
                    continue;
                }
                GVideoEventBus.get(SEND_GROUP_INFO).post(videoModel.getGroupInfo());
            }
            needSendGroupInfoVideoId = "";
        }
    }

    public void navigateToVideoFragment(View view, VideoModel videoModel, String fromPid) {
        GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY).post(videoModel);
    }

}
