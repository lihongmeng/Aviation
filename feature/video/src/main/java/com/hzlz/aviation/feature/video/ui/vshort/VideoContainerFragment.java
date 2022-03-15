package com.hzlz.aviation.feature.video.ui.vshort;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoContainerFragmentBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 竖向滑动切换视频的容器
 */
public class VideoContainerFragment extends BaseFragment<VideoContainerFragmentBinding> {

    // VideoContainerViewModel
    private VideoContainerViewModel viewModel;

    // 当前展示的Fragment
    private VideoShortFragment currentFragment;

    // 滑动切换的ViewPager
    private VideoVerticalViewPager videoVerticalViewPager;

    // 滑动切换的ViewPager的适配器
    private FragmentStatePagerAdapter adapter;

    // 视频数据数组
    private final List<VideoModel> videoModelList = new ArrayList<>();

    // 当前播放的视频在视频数据数组中的索引
    private int currentPosition;

    // 手势处理器
    private GestureDetector gestureDetector;

    private float actionDownY;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    public boolean darkImmersive() {
        return false;
    }

    @Override
    public int statusBarColor() {
        return Color.BLACK;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_container_fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        Bundle arguments = getArguments();
        ShortVideoListModel shortVideoListModel =
                arguments == null ? null : arguments.getParcelable(Constant.EXTRA_VIDEO_SHORT_MODELS);

        if (shortVideoListModel == null) {
            currentPosition = 0;
        } else {
            try {
                currentPosition = Integer.parseInt(shortVideoListModel.cursor);
            } catch (Exception e) {
                currentPosition = 0;
            }
        }
        gestureDetector = new GestureDetector(getActivity(), onGestureListener);
        videoVerticalViewPager = mBinding.verticalViewPager;
        adapter = new FragmentStatePagerAdapter(
                getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT
        ) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return VideoShortFragment.newInstance(
                        videoModelList.get(position),
                        getArguments(),
                        position == currentPosition
                );
            }

            @Override
            public int getCount() {
                return videoModelList.size();
            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position,
                                       @NonNull Object object) {
                currentFragment = (VideoShortFragment) object;
                super.setPrimaryItem(container, position, object);
            }
        };
        videoVerticalViewPager.setOffscreenPageLimit(1);
        videoVerticalViewPager.setAdapter(adapter);
        videoVerticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Bundle arguments = getArguments();
                currentPosition = position;
                if (position > videoModelList.size() - 3) {
                    viewModel.pageNumber++;
                    viewModel.loadMore();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        videoVerticalViewPager.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        videoVerticalViewPager.setCurrentItem(currentPosition);

        videoVerticalViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (currentPosition != 0) {
                    return false;
                }
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        actionDownY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (y - actionDownY < 200) {
                            return false;
                        }
                        finishActivity();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void bindViewModels() {
        Bundle arguments = getArguments();
        ShortVideoListModel shortVideoListModel =
                arguments == null ? null : arguments.getParcelable(Constant.EXTRA_VIDEO_SHORT_MODELS);
        Bundle videoBundleData =
                arguments == null ? null : arguments.getBundle(Constants.EXTRA_VIDEO_EXTRAS);
        viewModel = bingViewModel(VideoContainerViewModel.class);
        if (shortVideoListModel != null) {
            viewModel.mediaId = shortVideoListModel.getFirstMediaId();
        }
        viewModel.init(shortVideoListModel);
    }

    @Override
    protected void loadData() {
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).observe(
                this,
                s -> {
                    if (!isVisible()) return;
                    PluginManager.get(AccountPlugin.class)
                            .startLoginActivity(mBinding.verticalViewPager.getContext());
                });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.SUPPORT_SLIDE, Boolean.class).observe(
                this,
                slide -> {
                    if (!isVisible()) return;
                    if (videoVerticalViewPager != null) {
                        videoVerticalViewPager.setSlide(slide);
                    }
                });

        viewModel.feedLiveData.observe(
                this,
                shortVideoListModel -> {
                    if (shortVideoListModel == null) {
                        return;
                    }
                    if (shortVideoListModel.loadMore) {
                        Set<String> idSet = new HashSet<>();
                        for (VideoModel v : videoModelList) {
                            idSet.add(v.getId());
                        }
                        if (shortVideoListModel.list != null) {
                            for (VideoModel video : shortVideoListModel.list) {
                                if (video == null) {
                                    continue;
                                }
                                if (!idSet.contains(video.getId())) {
                                    videoModelList.add(video);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        videoModelList.clear();
                        for (VideoModel temp : shortVideoListModel.list) {
                            if (temp == null) {
                                continue;
                            }
                            videoModelList.add(temp);
                        }
                        adapter.notifyDataSetChanged();

                        int cursorIndex;
                        try {
                            cursorIndex = Integer.parseInt(shortVideoListModel.cursor);
                        } catch (Exception e) {
                            cursorIndex = currentPosition;
                        }
                        videoVerticalViewPager.setCurrentItem(cursorIndex);
                    }

                });
    }

    private final GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (currentFragment != null) {
                        currentFragment.onRootViewSingleTapUp(e);
                    }
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (currentFragment != null) {
                        currentFragment.onRootViewDoubleTap(e);
                    }
                    return super.onDoubleTap(e);
                }
            };


    // public void setSlide(boolean isSlide) {
    //     if (mVideoVerticalViewPager != null) {
    //         mVideoVerticalViewPager.setSlide(isSlide);
    //     }
    // }

}
