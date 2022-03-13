package com.jxntv.circle.adapter.qa;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.library.baseAdapters.BR;

import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.circle.QaGroupModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.utils.SpannableStringUtils;
import com.jxntv.base.utils.StringUtils;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.ItemDetailQaBinding;
import com.jxntv.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2021/8/30
 * desc : 圈子详情问答广场
 **/
public class CircleDetailQAAdapter extends BaseRecyclerAdapter<QaGroupModel, BaseRecyclerViewHolder> {

    public CircleDetailQAAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {

        ItemDetailQaBinding vh = ItemDetailQaBinding.inflate(mInflater, parent, false);

        return new BaseRecyclerViewHolder(vh);
    }


    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {
        QaGroupModel model = getRealPositionData(position);
        holder.getBinding().setVariable(BR.model, model);
        if (holder.getBinding() instanceof ItemDetailQaBinding){
            ItemDetailQaBinding binding = (ItemDetailQaBinding) holder.getBinding();
            setText(holder.itemView.getContext(),binding.requestName,model.getQuestionAuthorName());

            binding.requestAvatar.setOnClickListener(view -> {
                AuthorModel authorModel = new AuthorModel();
                authorModel.setId(model.getJid());
                authorModel.setType(AuthorType.UGC);
                PluginManager.get(AccountPlugin.class).startPgcActivity(binding.requestAvatar,authorModel);
            });
        }
        holder.itemView.setOnClickListener(v->{
            VideoModel videoModel = new VideoModel();
            videoModel.setId(model.getMediaId());
            PluginManager.get(VideoPlugin.class).startQADetailActivity(holder.itemView.getContext(), videoModel,null);
        });

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() > 3 ? Integer.MAX_VALUE : super.getItemCount();
    }

    private QaGroupModel getRealPositionData(int position){
        int p = position % mList.size();
        return mList.get(p);
    }

    private void setText(Context c, TextView view,final String text){

        view.post(() -> {
            String contentText = "@"+text;
            int w = view.getMeasuredWidth();
            int num = (int) (w / view.getTextSize());
            if (contentText.length() >= num - 3){
                contentText = StringUtils.showMaxLength(contentText,num - 4)+"...";
            }
            createTagTitle(c,view,contentText);
        });
    }


    public static void createTagTitle(Context context, TextView textView, String text) {
        if (context == null || textView == null) {
            return;
        }

        textView.setText(SpannableStringUtils.setSpanColor(text+" 的问题",
                text,R.color.color_666666));

    }

}
