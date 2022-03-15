package com.hzlz.aviation.kernel.network.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有分页的列表数据
 *
 *
 * @since 2020-03-02 08:04
 */
public final class ListWithPage<T> {
  //<editor-fold desc="属性">
  /*@NotNull*/
  private List<T> mList;
  /*@NotNull*/
  private APIPage mPage;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public ListWithPage(/*@NotNull*/List<T> list,/*@NotNull*/ APIPage page) {
    if (list == null || page == null) {
      throw new NullPointerException();
    }
    mList = list;
    mPage = page;
  }
  //</editor-fold>

  //<editor-fold desc="Getter">

  /*@NotNull*/
  public List<T> getList() {
    if (mList == null) {
      mList = new ArrayList<>();
    }
    return mList;
  }

  /*@NotNull*/
  public APIPage getPage() {
    if (mPage == null) {
      throw new NullPointerException();
    }
    return mPage;
  }

  //</editor-fold>
}
