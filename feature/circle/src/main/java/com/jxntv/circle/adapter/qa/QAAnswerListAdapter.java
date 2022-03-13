package com.jxntv.circle.adapter.qa;

import android.content.Context;
import android.view.ViewGroup;

import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.view.recyclerview.BaseRecyclerHeaderFooterAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.circle.databinding.LayoutQaAnswerListItemBinding;
import com.jxntv.circle.fragment.qa.QAAnswerMessageViewModel;


/**
 * @author huangwei
 * date : 2022/1/5
 * desc :
 **/
public class QAAnswerListAdapter extends BaseRecyclerHeaderFooterAdapter<AuthorModel, BaseRecyclerViewHolder> {

    private String pid;

    public QAAnswerListAdapter(Context context, String pid) {
        super(context);
        this.pid = pid;
    }

    private Circle circle;

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        LayoutQaAnswerListItemBinding v = LayoutQaAnswerListItemBinding.inflate(mInflater, parent, false);
        return new BaseRecyclerViewHolder(v);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        if (holder.getBinding() instanceof LayoutQaAnswerListItemBinding) {
            LayoutQaAnswerListItemBinding binding = (LayoutQaAnswerListItemBinding) holder.getBinding();
            binding.content.setAuthorModel(mList.get(position));
            binding.content.setAuthorObservable(mList.get(position).getObservable());
            binding.content.setViewModel(new QAAnswerMessageViewModel(circle, pid));
        }
    }
}
