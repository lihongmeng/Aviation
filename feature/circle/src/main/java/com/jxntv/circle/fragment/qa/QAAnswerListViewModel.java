package com.jxntv.circle.fragment.qa;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.view.recyclerview.BaseRecyclerHeaderFooterAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewModel;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.circle.CircleRepository;
import com.jxntv.network.response.ListWithPage;

/**
 * @author huangwei
 * date : 2021/9/6
 * desc : 回答者信息列表
 **/
public class QAAnswerListViewModel extends BaseRecyclerViewModel<AuthorModel> {

    private final CircleRepository repository = new CircleRepository();

    private Circle circle;

    public void setCircle(Circle circle){
        this.circle = circle;
    }

    public QAAnswerListViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected IRecyclerModel<AuthorModel> createModel() {
        return (page, loadListener) -> {

            repository.getQATeacherList(String.valueOf(circle.groupId), page)
                            .subscribe(new GVideoResponseObserver<ListWithPage<AuthorModel>>() {

                                @Override
                                protected boolean isShowPlaceholderLayout() {
                                    return page == 1 && ((BaseRecyclerHeaderFooterAdapter)mAdapter).getCount() == 0;
                                }

                                @Override
                                protected void onSuccess(@NonNull ListWithPage<AuthorModel> listWithPage) {
                                    loadSuccess(listWithPage.getList());
                                    hasMoreData(listWithPage.getPage().hasNextPage());
                                    loadComplete();
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    super.onError(throwable);
                                    if(page == 1){
                                        updatePlaceholderLayoutType(PlaceholderType.ERROR);
                                    }
                                    loadComplete();
                                }
                            });
        };
    }

}


