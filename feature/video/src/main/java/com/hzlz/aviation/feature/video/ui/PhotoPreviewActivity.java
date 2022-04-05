package com.hzlz.aviation.feature.video.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.utils.ImageDownloadLocalUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.ActivityPhotoViewBinding;
import com.ruffian.library.widget.RTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 全屏图片查看
 */
public class PhotoPreviewActivity extends BaseActivity<ActivityPhotoViewBinding> {

    private int currentPosition;
    private ArrayList<String> imageUrls;
    private ImageDownloadLocalUtils utils;

    public boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void initView() {
        imageUrls = getIntent().getStringArrayListExtra(Constants.EXTRA_IMG_URL);
        utils = new ImageDownloadLocalUtils(this);

        if (imageUrls == null || imageUrls.size() == 0) {
            finish();
        } else {
            SamplePagerAdapter adapter = new SamplePagerAdapter(imageUrls);
            mBinding.viewPager.setAdapter(adapter);

            //数量大于1显示指示器
            if (imageUrls.size() > 1) {
                for (int i = 0; i < imageUrls.size(); i++) {
                    RTextView textView = new RTextView(PhotoPreviewActivity.this);
                    int dp7 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_7DP);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp7, dp7);

                    if (i != 0) {
                        layoutParams.leftMargin = getResources().getDimensionPixelOffset(R.dimen.DIMEN_5DP);
                    }
                    textView.getHelper().setBackgroundColorNormal(getResources().getColor(R.color.color_525566));
                    textView.getHelper().setCornerRadius(dp7);
                    textView.setLayoutParams(layoutParams);
                    mBinding.imageIndicator.addView(textView, i);
                }
            }

            mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    setSelectIndicator(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        currentPosition = getIntent().getIntExtra(Constants.EXTRA_IMG_SELECT_POSITION, 0);
        setSelectIndicator(currentPosition);
        mBinding.viewPager.setCurrentItem(currentPosition,false);

        mBinding.download.setOnClickListener(view -> {
            utils.setDownLoadUrl(imageUrls.get(currentPosition));
            utils.showSaveDialog();
        });
    }

    private void setSelectIndicator(int position) {
        for (int i = 0; i < mBinding.imageIndicator.getChildCount(); i++) {
            if (i == position) {
                ((RTextView) mBinding.imageIndicator.getChildAt(i)).getHelper().setBackgroundColorNormal(getResources().getColor(R.color.color_fc284d));
            } else {
                ((RTextView) mBinding.imageIndicator.getChildAt(i)).getHelper().setBackgroundColorNormal(getResources().getColor(R.color.color_525566));
            }
        }

        if(imageUrls==null||imageUrls.isEmpty()){
            return;
        }
        if (new File(imageUrls.get(position)).exists()){
            mBinding.download.setVisibility(View.GONE);
        }else {
            mBinding.download.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        ActivityCompat.finishAfterTransition(this);
    }

    private class SamplePagerAdapter extends PagerAdapter {

        private List<String> images;

        public SamplePagerAdapter(List<String> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.media_default_cover_bg)
                    .error(R.drawable.ic_placeholder_empty);
            Glide.with(container.getContext())
                    .load(images.get(position))
                    .apply(requestOptions)
                    .into(photoView);

            photoView.setOnClickListener(v -> {
                ((Activity) container.getContext()).onBackPressed();
            });

            photoView.setOnLongClickListener(view -> {
                if (!new File(imageUrls.get(currentPosition)).exists()){
                    utils.setDownLoadUrl(images.get(position));
                    utils.showSaveDialog();
                }
                return false;
            });

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}