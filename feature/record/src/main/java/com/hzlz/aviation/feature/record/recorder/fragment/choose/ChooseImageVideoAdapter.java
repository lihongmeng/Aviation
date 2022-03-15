package com.hzlz.aviation.feature.record.recorder.fragment.choose;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzlz.aviation.feature.record.R;
import com.hzlz.aviation.feature.record.databinding.LayoutChooseItemBinding;
import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 选择视频页面适配器
 */
public class ChooseImageVideoAdapter extends RecyclerView.Adapter<DataBindingViewHolder<LayoutChooseItemBinding>> {

    /**
     * 常量-千
     */
    private static final int ONE_THOUSAND = 1000;
    /**
     * 1分钟转化秒数常量
     */
    private static final int SECOND_IN_ONE_MINUTE = 60;
    /**
     * 1小时转化秒数常量
     */
    private static final int SECOND_IN_ONE_HOUR = 3600;
    /**
     * 默认时间
     */
    private static final String DEFAULT_TIME = "00:00";
    /**
     * 1分钟内时间格式
     */
    private static final String TIME_FORMAT_IN_ONE_MINUTE = "00:%02d";
    /**
     * 1小时内时间格式
     */
    private static final String TIME_FORMAT_IN_ONE_HOUR = "%02d:%02d";
    /**
     * 大于1小时内时间格式
     */
    private static final String TIME_FORMAT_ONE_HOUR_MORE = "%02d:%02d:%02d";
    /**
     * 持有的搜索模型
     */
    private final List<ImageVideoEntity> dataSource = new ArrayList<>();

    /**
     * 不能选择的数据
     */
    private ArrayList<String> canNotSelectList = new ArrayList<>();
    /**
     * 当前页面的item大小，需要根据屏幕适配
     */
    private int itemSize = (ScreenUtils.getScreenWidth(GVideoRuntime.getAppContext())
            - 3 * GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.choose_num_item_interval))
            / 4;
    private boolean isVideoType = true;

    public void updateCanNotSelectList(ArrayList<String> canNotSelectList) {
        this.canNotSelectList = canNotSelectList;
        notifyDataSetChanged();
    }

    private ChooseImageVideoViewModel chooseImageVideoViewModel;

    public void setChooseImageVideoViewModel(ChooseImageVideoViewModel model){
        this.chooseImageVideoViewModel = model;
    }

    @NonNull
    @Override
    public DataBindingViewHolder<LayoutChooseItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DataBindingViewHolder<LayoutChooseItemBinding> holder =
                new DataBindingViewHolder<>(parent, R.layout.layout_choose_item);
        // 根据屏幕大小适配item
        ViewGroup.LayoutParams params = holder.binding.itemContainer.getLayoutParams();
        if (params.width != itemSize || params.height != itemSize) {
            params.width = itemSize;
            params.height = itemSize;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingViewHolder<LayoutChooseItemBinding> holder, int position) {
        ImageVideoEntity entity = dataSource.get(position);

        if (entity.isVideo) {
            holder.binding.itemVideoTime.setText(formatSeconds(entity.duration / ONE_THOUSAND));
            holder.binding.itemVideoTime.setVisibility(View.VISIBLE);
        } else {
            holder.binding.itemVideoTime.setVisibility(View.GONE);
        }

        if (entity.isTakePhoto){
            holder.binding.chooseItemImg.setVisibility(View.GONE);
            holder.binding.chooseItemText.setVisibility(View.GONE);
        }else {
            if (entity.selectPosition <= 0) {
                holder.binding.chooseItemImg.setVisibility(View.VISIBLE);
                holder.binding.chooseItemText.setVisibility(View.GONE);
            } else {
                holder.binding.chooseItemText.setVisibility(View.VISIBLE);
                holder.binding.chooseItemText.setText(String.valueOf(entity.selectPosition));
                holder.binding.chooseItemImg.setVisibility(View.GONE);
            }
        }

        holder.binding.chooseItemImg.setOnClickListener(
                v -> {
                    if (!entity.isVideo && !VideoChooseHelper.getInstance().checkCanSelectImage()){
                        ToastUtils.showShort("超过最大数量限制");
                        return;
                    }
                    VideoChooseHelper.getInstance().setPreviewVideoEntity(entity);
                    VideoChooseHelper.getInstance().addVideoEntity(entity);
                }
        );

        holder.binding.chooseItemText.setOnClickListener(v -> {
            boolean success = VideoChooseHelper.getInstance().removeVideoEntity(entity);
            if (success) {
                notifyDataSetChanged();
            }
        });

        holder.binding.itemImage.setOnClickListener(v -> {

            if (entity.isTakePhoto){
                if (chooseImageVideoViewModel!=null){
                    chooseImageVideoViewModel.takePhoto();
                }
            }else {
                VideoChooseHelper.getInstance().setPreviewVideoEntity(entity);
                NavController controller = Navigation.findNavController(holder.binding.getRoot());
                controller.navigate(R.id.previewVideoFragment);
            }
        });

        if (!entity.isTakePhoto) {
            Context context = holder.itemView.getContext();
            Glide.with(holder.itemView.getContext())
                    .load(Uri.fromFile(new File(entity.path)))
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_choose_default))
                    .into(holder.binding.itemImage);
        }else {
            holder.binding.itemImage.setImageResource(R.drawable.ic_take_photo);
        }
    }

    /**
     * 更新数据list
     *
     * @param list 视频模型列表
     */
    void updateList(List<ImageVideoEntity> list) {
        dataSource.clear();

        if (list == null || list.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        if (canNotSelectList == null || canNotSelectList.isEmpty()) {
            dataSource.addAll(list);
            notifyDataSetChanged();
            return;
        }

        for (ImageVideoEntity imageVideoEntity : list) {
            if (imageVideoEntity == null
                    || canNotSelectList.contains(imageVideoEntity.path)) {
                continue;
            }
            dataSource.add(imageVideoEntity);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    /**
     * 转化时间显示类型
     *
     * @param seconds 视频长度：秒数
     */
    private String formatSeconds(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = DEFAULT_TIME;
        } else if (seconds < SECOND_IN_ONE_MINUTE) {
            standardTime = String.format(
                    Locale.getDefault(),
                    TIME_FORMAT_IN_ONE_MINUTE,
                    seconds % SECOND_IN_ONE_MINUTE
            );
        } else if (seconds < SECOND_IN_ONE_HOUR) {
            standardTime = String.format(
                    Locale.getDefault(),
                    TIME_FORMAT_IN_ONE_HOUR,
                    seconds / SECOND_IN_ONE_MINUTE,
                    seconds % SECOND_IN_ONE_MINUTE
            );
        } else {
            standardTime = String.format(
                    Locale.getDefault(),
                    TIME_FORMAT_ONE_HOUR_MORE,
                    seconds / SECOND_IN_ONE_HOUR,
                    seconds % SECOND_IN_ONE_HOUR / SECOND_IN_ONE_MINUTE,
                    seconds % SECOND_IN_ONE_HOUR
            );
        }
        return standardTime;
    }
}
