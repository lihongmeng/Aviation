package com.jxntv.pptv.adapter;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.pptv.BR;
import com.jxntv.pptv.R;
import com.jxntv.pptv.databinding.AdapterCategoryBinding;
import com.jxntv.pptv.model.Category;
import com.jxntv.pptv.model.CategoryResponse;
import java.util.List;

public final class CategoryAdapter extends BaseDataBindingAdapter<CategoryResponse>
  implements CategoryDetailAdapter.Listener {
  private final Application mApp;
  @Nullable private Listener mListener;
  public void setListener(Listener listener) {
    mListener = listener;
  }
  public CategoryAdapter(Application application) {
    mApp = application;
    mEnablePlaceholder = false;
  }

  @Override protected int getItemLayoutId() {
    return R.layout.adapter_category;
  }

  @Override protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    CategoryResponse category = mDataList.get(position);
    CategoryDetailAdapter adapter = createDetailAdapter();
    adapter.setListener(this);
    adapter.replaceData(category.getCategories());
    holder.bindData(BR.adapter, adapter);
    holder.bindData(BR.layoutManager, createDetailLayoutManager());
    holder.bindData(BR.position, category.getModelPosition());

    //滑动到可见位置
    if (holder.binding instanceof AdapterCategoryBinding) {
      int startPos = 0;
      for (int i = 0; i < category.getCategories().size(); i++) {
        Category c = category.getCategories().get(i);
        if (c.isCheck()) {
          startPos = i;
          break;
        }
      }
      if (startPos > 0) {
        final int ss = startPos;
        ((AdapterCategoryBinding) holder.binding).recyclerView.post(
            () -> ((AdapterCategoryBinding) holder.binding).recyclerView.smoothScrollToPosition(ss));
      }
    }
  }

  private CategoryDetailAdapter createDetailAdapter() {
    return new CategoryDetailAdapter();
  }

  private RecyclerView.LayoutManager createDetailLayoutManager() {
    final LinearLayoutManager llm = new LinearLayoutManager(mApp,
        RecyclerView.HORIZONTAL, false);
    return llm;
  }

  @Override
  public void onItemRootViewClicked(@NonNull View v, @NonNull CategoryDetailAdapter adapter,
      int itemAdapterPosition) {
    List<Category> list = adapter.getData();
    Category current = list.get(itemAdapterPosition);
    for (Category c : list) {
      c.setCheck(c == current);
    }
    if (mListener != null) {
      Category category = adapter.getData().get(itemAdapterPosition);
      mListener.onItemRootViewClicked(v, category);
    }
  }

  public interface Listener {
    void onItemRootViewClicked(@NonNull View v, Category category);
  }
}
