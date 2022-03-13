package com.jxntv.search.repository;

import androidx.annotation.NonNull;

import com.jxntv.media.MediaPageSource;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.search.api.SearchApiConstants;
import com.jxntv.search.model.SearchAuthorModel;
import com.jxntv.search.model.SearchDetailModel;
import com.jxntv.search.model.SearchModel;
import com.jxntv.search.model.SearchType;
import com.jxntv.search.request.SearchAuthorRequest;
import com.jxntv.search.request.SearchMediaRequest;

import io.reactivex.rxjava3.core.Observable;

/**
 * 搜索仓库类
 */
public class SearchRepository extends BaseDataRepository {

    /**
     * 全量搜索
     *
     * @param query
     * @param hintQuery 预制文案查询
     * @return
     */
    public Observable<SearchModel> searchAll(@NonNull String query,String hintQuery) {
        int pageNum = 1;
        int pageCount = SearchApiConstants.DEFAULT_PAGE_COUNT;
        return Observable.zip(
                searchMedia(query, SearchType.CATEGORY_ALL, pageNum, pageCount),
                searchMedia(query, SearchType.CATEGORY_AUTHORS, pageNum, pageCount),
                searchMedia(query, SearchType.CATEGORY_COMMUNITY, pageNum, pageCount),
                searchMedia(query, SearchType.CATEGORY_PROGRAM, pageNum, pageCount),
                searchMedia(query, SearchType.CATEGORY_NEWS, pageNum, pageCount),
                searchMedia(query, SearchType.CATEGORY_MOMENT, pageNum, pageCount),
//                searchMedia(query, SearchType.CATEGORY_SHORT_VIDEO, pageNum, pageCount),
//                searchMedia(query, SearchType.CATEGORY_SERIES, pageNum, pageCount),
//                searchMedia(query, SearchType.CATEGORY_AUDIO, pageNum, pageCount),
                (searchDetailModelSearchResponse,
                 searchDetailModelSearchResponse2,
                 searchDetailModelSearchResponse3,
                 searchDetailModelSearchResponse4,
                 searchDetailModelSearchResponse5,
                 searchDetailModelSearchResponse6
                ) -> {
                    SearchModel model = new SearchModel();
                    model.query = query;
                    model.hintQuery = hintQuery;

                    model.mAll = searchDetailModelSearchResponse.getList();
                    model.hasMoreAll = searchDetailModelSearchResponse.getPage().hasNextPage();
                    model.mAuthors = searchDetailModelSearchResponse2.getList();
                    model.hasMoreAuthors = searchDetailModelSearchResponse.getPage().hasNextPage();
                    model.mCommunities = searchDetailModelSearchResponse3.getList();
                    model.hasMoreCommunities = searchDetailModelSearchResponse.getPage().hasNextPage();
                    model.mProgrammes = searchDetailModelSearchResponse4.getList();
                    model.mNews = searchDetailModelSearchResponse5.getList();
                    model.mMoment = searchDetailModelSearchResponse6.getList();
                    return model;
                });
    }

    /**
     * 搜索作者
     *
     * @param query
     * @return
     */
    public Observable<ListWithPage<SearchAuthorModel>> searchAuthor(String query, int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<SearchAuthorModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<SearchAuthorModel>> createRequest() {
                SearchAuthorRequest request = new SearchAuthorRequest(query);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<SearchAuthorModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (SearchAuthorModel author : mediaListWithPage.getList()) {
                        author.updateInteract();
                    }
                }
            }
        }.asObservable();
    }



    /**
     * 搜索资源
     */
    public Observable<ListWithPage<SearchDetailModel>> searchMedia(String query, int category, int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<SearchDetailModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<SearchDetailModel>> createRequest() {
                SearchMediaRequest request = new SearchMediaRequest(query, category);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<SearchDetailModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (SearchDetailModel detailModel : mediaListWithPage.getList()) {
                        detailModel.updateInteract();
                    }
                }
            }
        }.asObservable();
    }
}
