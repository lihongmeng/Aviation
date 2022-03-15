package com.hzlz.aviation.feature.search.page;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.search.api.SearchApiConstants;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.repository.SearchResultRepository;
import com.hzlz.aviation.feature.search.utils.SearchUtils;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * search  page页面数据模型
 */
public class SearchShortVideoViewModel extends SearchPageViewModel {
    private int mDetailPage = 1;

    public SearchShortVideoViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 加载更多短视频/音频数据,用于外部调用，无ui相关处理
     *
     * @param refresh 进入详情页首次加载数据时会清空掉历史cursor
     * @return 对应的observable
     */
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return Observable.create(new ObservableOnSubscribe<ShortVideoListModel>() {
            @Override
            public void subscribe(ObservableEmitter<ShortVideoListModel> e) {
                mSearchRepository.searchMedia(mQuery, mCategory, mDetailPage,
                    SearchApiConstants.DEFAULT_PAGE_COUNT)
                    .subscribe(new GVideoResponseObserver<ListWithPage<SearchDetailModel>>() {
                        @Override protected void onSuccess(
                            @NonNull
                                ListWithPage<SearchDetailModel> searchModel) {
                            mDetailPage += (searchModel.getPage().hasNextPage() ? 1 : 0);
                            e.onNext(handleSearchModel(searchModel));
                        }

                        @Override public void onFailed(Throwable throwable) {
                            e.onError(throwable);
                        }
                    });
            }
        });
    }


    /**
     * 处理请求数据
     *
     * @param searchModel  对应的返回数据
     * @return 生成的短视频列表数据模型
     */
    private ShortVideoListModel handleSearchModel(ListWithPage<SearchDetailModel> searchModel) {
        if (searchModel == null) {
            return null;
        }
        List<VideoModel> list = SearchUtils.buildVideoModelList(searchModel.getList());
        SearchResultRepository.getInstance().updateShortVideo(list);
        //mAdapter.loadMoreData(searchModel.shortVideo.list);
        return ShortVideoListModel.Builder.aFeedModel().withList(list).withLoadMore(true).build();
    }

    @Override public void loadSuccess(List<SearchDetailModel> list) {
        List<VideoModel> sList = SearchUtils.buildVideoModelList(list);
        SearchResultRepository.getInstance().updateShortVideo(sList);
        super.loadSuccess(list);
    }

}
