package com.jxntv.android.video.ui.detail;

import com.jxntv.network.response.ListWithPage;

import java.util.List;

public class DetailModelList<CommentModel> {
    public final List<CommentModel> list;
    public final boolean hasMore;

    public DetailModelList(List<CommentModel> list, boolean hasMore) {
        this.list = list;
        this.hasMore = hasMore;
    }

    public DetailModelList(ListWithPage<CommentModel> listWithPage) {
        this.list = listWithPage.getList();
        this.hasMore = listWithPage.getPage().hasNextPage();
    }

    public boolean isListEmpty() {
        return list == null || list.isEmpty();
    }


}
