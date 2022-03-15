package com.hzlz.aviation.feature.community.adapter.qa;

import android.view.ViewGroup;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.LayoutQaAnswerMessageBinding;
import com.hzlz.aviation.feature.community.fragment.qa.QAAnswerMessageViewModel;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.feature.community.BR;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * @author huangwei
 * date : 2022/1/5
 * desc : 问答banner,老师内容
 **/
public class QABannerAnswerAdapter extends BannerAdapter<AuthorModel, DataBindingViewHolder> {

    private Circle circle;

    private String pid;

    public void setCircle(Circle circle){
        this.circle = circle;
    }

    public QABannerAnswerAdapter(List<AuthorModel> datas, String pid) {
        super(datas);
        this.pid = pid;
    }

    @Override
    public DataBindingViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(parent, R.layout.layout_qa_answer_message);
    }

    @Override
    public void onBindView(DataBindingViewHolder holder, AuthorModel data, int position, int size) {
        holder.binding.setVariable(BR.authorModel,data);
        holder.binding.setVariable(BR.authorObservable, data.getObservable());
        holder.binding.setVariable(BR.viewModel, new QAAnswerMessageViewModel(circle, circle.name + "运营位"));
        if (holder.binding instanceof LayoutQaAnswerMessageBinding) {
            LayoutQaAnswerMessageBinding binding = (LayoutQaAnswerMessageBinding) holder.binding;
            binding.userTag.setMaxLines(1);
        }
    }

}
