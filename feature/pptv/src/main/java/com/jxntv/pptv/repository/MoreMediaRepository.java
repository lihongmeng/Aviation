package com.jxntv.pptv.repository;

import android.text.TextUtils;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.pptv.model.Category;
import com.jxntv.pptv.model.CategoryResponse;
import com.jxntv.pptv.model.Media;
import com.jxntv.pptv.model.annotation.CategoryKey;
import com.jxntv.pptv.request.GetCategoryListRequest;
import com.jxntv.pptv.request.GetFilterListRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import java.util.Map;

public final class MoreMediaRepository extends BaseDataRepository {
  public Observable<List<CategoryResponse>> getCategoryList(Category category) {
    Observable<List<CategoryResponse>> response = new NetworkData<List<CategoryResponse>>(mEngine) {
        @Override protected BaseRequest<List<CategoryResponse>> createRequest() {
          GetCategoryListRequest request = new GetCategoryListRequest();
          if (category != null) {
            if (TextUtils.equals(category.getCategoryKey(), CategoryKey.TAG)) {
              request.setLabelType(1);
            } else if (TextUtils.equals(category.getCategoryKey(), CategoryKey.PLATE)){
              request.setLabelType(2);
            }
          }

          return request;
        }

        @Override protected void saveData(List<CategoryResponse> categoryResponses) {
          if (categoryResponses != null) {
            for (CategoryResponse cr : categoryResponses) {
              String ck = cr.getCategoryKey();
              for (Category c : cr.getCategories()) {
                c.setCategoryKey(ck);
              }
            }
          }
        }
      }.asObservable();

    return response.map(categoryResponses -> {
      if (category != null) {
        for (CategoryResponse cr : categoryResponses) {
          if (cr != null && cr.getCategories() != null && !cr.getCategories().isEmpty()) {
            Category startCategory = cr.getCategories().get(0);
            if (TextUtils.equals(cr.getCategoryKey(), category.getCategoryKey())) {
              for (Category c : cr.getCategories()) {
                if (TextUtils.equals(c.getId(), category.getId())
                    || TextUtils.equals(c.getName(), category.getName())) {
                  startCategory.setCheck(false);
                  c.setCheck(true);
                  break;
                }
              }
            } else {
              startCategory.setCheck(true);
            }
          }
        }
      }
      return categoryResponses;
    });
  }

  public Observable<ListWithPage<Media>> getMoreMediaList(Map<String, Category> selectedCategory, int pageNumber, int pageSize) {
    Observable<ListWithPage<Media>> response = new NetworkData<ListWithPage<Media>>(mEngine) {
        @Override protected BaseRequest<ListWithPage<Media>> createRequest() {
          GetFilterListRequest request = new GetFilterListRequest();
          if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Map.Entry<String, Category> entry : selectedCategory.entrySet()) {
              request.setQuery(entry.getKey(), entry.getValue().getId());
            }
          }
          request.setPageNumber(pageNumber);
          request.setPageSize(pageSize);
          return request;
        }

        @Override protected void saveData(ListWithPage<Media> mediaListWithPage) {
          if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
            for (Media media : mediaListWithPage.getList()) {
              media.updateInteract();
            }
          }
        }
      }.asObservable();
    return response;
  }
}
