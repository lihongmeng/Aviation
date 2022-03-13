package com.jxntv.watchtv.ui.select;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.SEND_VIDEO_DATA_TO_PLAY;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.watchtv.databinding.ItemSelectCollectionBinding;

@SuppressWarnings("rawtypes")
public class SelectCollectionAdapter extends BaseRecyclerAdapter<VideoModel, BaseRecyclerViewHolder> {

    public String currentProgramId;

    public SelectCollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemSelectCollectionBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder recommendViewHolder, int position) {
        VideoModel videoModel = mList.get(position);
        if (videoModel == null) {
            return;
        }
        ItemSelectCollectionBinding binding = (ItemSelectCollectionBinding) recommendViewHolder.getBinding();
        if (binding == null) {
            return;
        }
        binding.setVideomodel(videoModel);
        binding.executePendingBindings();

        if (!TextUtils.equals(videoModel.getId(), currentProgramId)){
            binding.maskBg.setVisibility(GONE);
            binding.maskWave.setVisibility(GONE);
            binding.maskText.setVisibility(GONE);
            binding.maskWave.applyAnimation();
        }else {
            binding.maskBg.setVisibility(VISIBLE);
            binding.maskWave.setVisibility(VISIBLE);
            binding.maskText.setVisibility(VISIBLE);
        }


        binding.root.setTag(position);
        binding.root.setOnClickListener(
                view -> {
                    Object object = view.getTag();
                    if (object == null) {
                        return;
                    }
                    int clickIndex = (int) object;
                    currentProgramId = mList.get(clickIndex).getId();
                    GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY).post(mList.get(clickIndex));
                    notifyDataSetChanged();
                });


    }

    public String getCurrentPlayVideoId(){
        int currentPlayIndex = 1;
        for (int i = 0;i < mList.size();i++){
            if (TextUtils.equals(mList.get(i).getId(), currentProgramId)){
                currentPlayIndex = i;
                break;
            }
        }
        return mList.get(currentPlayIndex).getId();
    }

    public void setCurrentProgramId(String programId){
        this.currentProgramId = programId;
        notifyDataSetChanged();
    }

}
