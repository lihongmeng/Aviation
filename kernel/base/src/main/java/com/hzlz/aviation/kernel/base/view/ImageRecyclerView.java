package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.adapter.ImageFallAdapter;
import com.hzlz.aviation.kernel.base.adapter.ImageNineAdapter;
import com.hzlz.aviation.kernel.base.decoration.GapItemDecoration;
import com.hzlz.aviation.kernel.base.model.video.ImageModel;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/4/6
 * desc : 9宫格图片recycler
 **/
public class ImageRecyclerView extends GVideoLinearLayout {

    private int gapDp;
    private int imageType;
    private int showStyle;
    private Context context;

    public interface ImageSize {
        int normal = 0;
        int small = 1;
    }

    public interface ShowStyle {
        int NINE = 0;
        int FALL = 1;
    }

    public ImageRecyclerView(Context context) {
        this(context, null);
    }

    public ImageRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);
        gapDp = ResourcesUtils.getIntDimens(R.dimen.DIMEN_7DP);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageRecyclerView);
            imageType = array.getInteger(R.styleable.ImageRecyclerView_imageSize, 0);
            showStyle = array.getInteger(R.styleable.ImageRecyclerView_show_style, 0);
            if (imageType == ImageSize.small) {
                gapDp = ResourcesUtils.getIntDimens(R.dimen.DIMEN_5DP);
            }
            array.recycle();
        }
    }


    /**
     * 设置多张图片数据
     *
     * @param imageData    图片
     * @param oriImageData 原图地址
     * @param imageList    图片(有图片大小信息)
     */
    @BindingAdapter(value = {"imageData", "oriImageData", "imageList"}, requireAll = false)
    public static void setImagesData(ImageRecyclerView recyclerView, @NonNull List<String> imageData,
                                     List<String> oriImageData, List<ImageModel> imageList) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.setImagesData(imageData, oriImageData, imageList);
    }

    @BindingAdapter(value = {"imageData", "oriImageData"}, requireAll = false)
    public static void setImagesData(ImageRecyclerView recyclerView, @NonNull List<String> imageData, List<String> oriImageData) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.setImagesData(imageData, oriImageData, null);
    }


    public void setImagesData(@NonNull List<String> imageData, List<String> oriImageData) {
        setImagesData(imageData,oriImageData,null);
    }

    /**
     * 设置多张图片数据
     *
     * @param imageData    展示图片
     * @param oriImageData 原图片地址
     * @param imageModelList 附带图片大小数据地址
     */
    public void setImagesData(@NonNull List<String> imageData, List<String> oriImageData, List<ImageModel> imageModelList) {
        removeAllViews();
         if (imageData == null || imageData.size() == 0) {
            return;
        }

        GVideoRecyclerView recyclerView;
        View view = getChildAt(0);
        if (view == null) {
            removeAllViews();
            recyclerView = new GVideoRecyclerView(context);
            addView(recyclerView, 0);
            //去重
            if (recyclerView.getItemDecorationCount() == 0) {
                GapItemDecoration decoration = new GapItemDecoration();
                decoration.setHorizontalGap(gapDp);
                decoration.setVerticalGap(gapDp);
                decoration.setFistRowTopGap(gapDp);
                recyclerView.addItemDecoration(decoration);
            }
        } else {
            recyclerView = (GVideoRecyclerView) view;
        }

        if (showStyle == ShowStyle.NINE) {
            ImageNineAdapter imageNineAdapter = new ImageNineAdapter(context, imageType);
            if (imageData.size() == ImageSize.small) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setPadding(0, 0, 0, 0);
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                recyclerView.setPadding(-gapDp, 0, 0, 0);
            }
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(imageNineAdapter);
            imageNineAdapter.clearList();
            imageNineAdapter.setOriImageData(oriImageData);
            imageNineAdapter.addData(getImageModelList(imageModelList,imageData));
        } else if (showStyle == ShowStyle.FALL) {
            ImageFallAdapter imageFallAdapter = new ImageFallAdapter(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setPadding(0, 0, 0, 0);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(imageFallAdapter);
            imageFallAdapter.clearList();
            imageFallAdapter.setOriImageData(oriImageData);
            imageFallAdapter.addData(getImageModelList(imageModelList,imageData));
        }
    }

    private List<ImageModel> getImageModelList(List<ImageModel> imageModelList,List<String> imageData){
        if (imageModelList == null || imageModelList.size() == 0){
            imageModelList = new ArrayList<>();
            for (int i = 0; i< imageData.size(); i++ ){
                ImageModel imageModel = new ImageModel();
                imageModel.setUrl(imageData.get(i));
                imageModelList.add(imageModel);
            }
        }
        return imageModelList;
    }

}
