package com.hzlz.aviation.feature.video.ui.detail.recommend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.ui.detail.DetailAdapter;
import com.hzlz.aviation.feature.video.ui.viewholder.RecommendViewHolder;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.base.tag.TagTextHelper;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.feature.video.databinding.VideoSuperRecommendItemBinding;

public class RecommendAdapter extends DetailAdapter<RecommendModel, RecommendViewHolder> {

    private StatFromModel mStat;

    public RecommendAdapter(Context context) {
        super(context);
    }

    public void setStat(StatFromModel stat) {
        mStat = stat;
    }

    @Override
    public RecommendViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new RecommendViewHolder(VideoSuperRecommendItemBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(RecommendViewHolder recommendViewHolder, int position) {
        RecommendModel recommendModel = mList.get(position);
        if (recommendModel == null) {
            return;
        }
        VideoSuperRecommendItemBinding binding=recommendViewHolder.getBinding();
        if (binding==null){
            return;
        }
        binding.setRecommend(recommendModel);
        binding.setAdapter(this);
        binding.executePendingBindings();
        TagTextHelper.createTagTitle(
                mContext,
                binding.tag,
                "",
                recommendModel.tagType
        );
        statAdvert(recommendModel, false);
    }

    public void onItemClick(View v, RecommendModel recommendModel) {
        mActionLiveData.postValue(new ActionModel<>(recommendModel, ACTION_ITEM));
        statAdvert(recommendModel, true);
    }

    private void statAdvert(RecommendModel recommendModel, boolean click) {
        String extendId = recommendModel.extendId;
        String extendName = recommendModel.title;
        String extendShowType = String.valueOf(recommendModel.extendType);
        String place = StatConstants.DS_KEY_PLACE_ADVERT;
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStat);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_SHOW_TYPE, extendShowType);
        ds.addProperty(StatConstants.DS_KEY_PLACE, place);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withPid(mStat != null ? mStat.pid : "")
                .withEv(StatConstants.EV_ADVERT)
                .withDs(ds.toString())
                .withType(click ? StatConstants.TYPE_CLICK_C : StatConstants.TYPE_SHOW_E)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

}
