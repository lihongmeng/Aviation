package com.hzlz.aviation.feature.live.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.HERALD;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.LIVING;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.REVIEW;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.live.holder.PlayingLiveHolder;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

public class PlayingLiveAdapter extends RecyclerView.Adapter<PlayingLiveHolder> {

    private Activity activity;
    private List<MediaModel> dataSource;
    private Listener listener;

    public PlayingLiveAdapter(Activity activity) {
        this.activity = activity;
        dataSource = new ArrayList<>();
    }

    public void updateDataSource(List<MediaModel> list) {
        dataSource.clear();
        if (dataSource != null) {
            dataSource.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayingLiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayingLiveHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_home_live_living_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingLiveHolder holder, int position) {
        MediaModel mediaModel = dataSource.get(position);
        if (mediaModel == null) {
            return;
        }

        ImageLoaderManager.loadImage(holder.cover, mediaModel.getCoverUrl(), false);

        String title = mediaModel.getTitle();
        title = TextUtils.isEmpty(title) ? "" : title;
        holder.title.setText(title);

        int liveBroadcastStatus = mediaModel.getLiveBroadcastStatus();
        String liveBroadcastStatusStr = mediaModel.getLiveBroadcastStatusStr();
        switch (liveBroadcastStatus) {
            case LIVING:
                showLiving(holder, liveBroadcastStatusStr);
                hideLiveStartTime(holder);
                break;
            case HERALD:
                showLiveHerald(holder, liveBroadcastStatusStr);
                showLiveStartTime(holder, mediaModel.liveBroadcastStartTime);
                break;
            case REVIEW:
                showLiveReview(holder, liveBroadcastStatusStr);
                hideLiveStartTime(holder);
                break;
            default:
                hideLiveStartTime(holder);
                hideLiveType(holder);
        }

        holder.divider.setVisibility((position == getItemCount() - 1) ? GONE : VISIBLE);

        holder.root.setTag(mediaModel);
        holder.root.setOnClickListener(view -> {
            Object object = view.getTag();
            if (object == null) {
                return;
            }
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(activity, (MediaModel) object, null);
        });

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private void hideLiveType(PlayingLiveHolder holder) {
        holder.liveTypeBackground.setVisibility(GONE);
        holder.wave.setVisibility(GONE);
        holder.liveType.setVisibility(GONE);
    }

    private void showLiving(PlayingLiveHolder holder, String content) {
        holder.liveTypeBackground.setBackgroundResource(R.drawable.shape_gradient_f36486_e4344e_180_corners_2dp);
        holder.liveTypeBackground.setVisibility(VISIBLE);

        holder.wave.applyAnimation();
        holder.wave.setVisibility(VISIBLE);

        content = TextUtils.isEmpty(content) ? activity.getString(R.string.live) : content;
        holder.liveType.setText(content);
        holder.liveType.setVisibility(VISIBLE);
    }

    private void showLiveHerald(PlayingLiveHolder holder, String content) {
        holder.liveTypeBackground.setBackgroundResource(R.drawable.shape_solid_eba647_corners_2dp);
        holder.liveTypeBackground.setVisibility(VISIBLE);

        holder.wave.setVisibility(GONE);

        content = TextUtils.isEmpty(content) ? activity.getString(R.string.live_herald) : content;
        holder.liveType.setText(content);
        holder.liveType.setVisibility(VISIBLE);

    }

    private void showLiveReview(PlayingLiveHolder holder, String content) {
        holder.liveTypeBackground.setBackgroundResource(R.drawable.shape_solid_45a4e5_corners_2dp);
        holder.liveTypeBackground.setVisibility(VISIBLE);

        holder.wave.setVisibility(GONE);

        content = TextUtils.isEmpty(content) ? activity.getString(R.string.live_review) : content;
        holder.liveType.setText(content);
        holder.liveType.setVisibility(VISIBLE);
    }

    private void hideLiveStartTime(PlayingLiveHolder holder) {
        holder.heraldBackground.setVisibility(GONE);
        holder.heraldIcon.setVisibility(GONE);
        holder.heraldTime.setVisibility(GONE);
        holder.heraldTimeRight.setVisibility(GONE);
    }

    private void showLiveStartTime(PlayingLiveHolder holder, Long liveBroadcastStartTime) {
        if (liveBroadcastStartTime == null) {
            hideLiveStartTime(holder);
        } else {
            holder.heraldTime.setVisibility(VISIBLE);
            holder.heraldBackground.setVisibility(VISIBLE);
            holder.heraldIcon.setVisibility(VISIBLE);
            holder.heraldTimeRight.setVisibility(VISIBLE);
            holder.heraldTime.setText(DateUtils.changeTimeLongToString(liveBroadcastStartTime, "MM/dd HH:mm"));
        }
    }

    public interface Listener {
        void onFollowCancelClick(View view, AuthorModel author);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * 单独更新数据源中的一项
     *
     * @param target   目标数据
     * @param isFollow 想要更改的数据
     */
    public void updateSingle(AuthorModel target, boolean isFollow) {
        if (target == null || dataSource.isEmpty()) {
            return;
        }
        String targetId = target.getId();
        if (TextUtils.isEmpty(targetId)) {
            return;
        }
        for (int index = 0; index < dataSource.size(); index++) {
            MediaModel mediaModel = dataSource.get(index);
            if (mediaModel == null) {
                return;
            }
            AuthorModel author = mediaModel.getAuthor();
            if (author == null || !targetId.equals(author.getId())) {
                continue;
            }
            author.setFollow(isFollow);
            notifyItemChanged(index);
            return;
        }
    }

    public void updateSingleOnlyData(AuthorModel target, boolean isFollow) {
        if (target == null || dataSource.isEmpty()) {
            return;
        }
        String targetId = target.getId();
        if (TextUtils.isEmpty(targetId)) {
            return;
        }
        for (int index = 0; index < dataSource.size(); index++) {
            MediaModel mediaModel = dataSource.get(index);
            if (mediaModel == null) {
                return;
            }
            AuthorModel author = mediaModel.getAuthor();
            if (author == null || !targetId.equals(author.getId())) {
                continue;
            }
            author.setFollow(isFollow);
            return;
        }
    }

}
