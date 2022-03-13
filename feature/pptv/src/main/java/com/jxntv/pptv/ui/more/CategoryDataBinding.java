package com.jxntv.pptv.ui.more;

import android.app.Application;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.pptv.adapter.CategoryAdapter;
import com.jxntv.pptv.model.CategoryResponse;
import java.util.List;

public final class CategoryDataBinding {
  private final CategoryAdapter mCategoryAdapter;;
  private final Application mApp;
  public CategoryDataBinding(Application application, CategoryAdapter.Listener listener) {
    mApp = application;
    mCategoryAdapter = new CategoryAdapter(mApp);
    mCategoryAdapter.setListener(listener);
  }

  void setData(List<CategoryResponse> categoryResponses) {
    mCategoryAdapter.replaceData(categoryResponses);
  }

  public RecyclerView.Adapter getYearAdapter() {
    return mCategoryAdapter;
  }

  public RecyclerView.LayoutManager getYearLayoutManager() {
    final LinearLayoutManager llm = new LinearLayoutManager(mApp);
    return llm;
  }
}
