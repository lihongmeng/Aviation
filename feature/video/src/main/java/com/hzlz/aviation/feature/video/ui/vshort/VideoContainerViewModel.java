package com.hzlz.aviation.feature.video.ui.vshort;

import static com.hzlz.aviation.kernel.base.Constant.PAGE_DEFAULT.PAGE_SIZE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.feature.video.repository.MediaRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

public class VideoContainerViewModel extends BaseViewModel {

    private static final boolean DEBUG = true;

    private static final String TAG = VideoContainerViewModel.class.getSimpleName();

    // MediaRepository
    private final MediaRepository mediaRepository = new MediaRepository();

    // 加载更多的视频数据
    public final MutableLiveData<ShortVideoListModel> feedLiveData = new MutableLiveData<>();

    public String mediaId;

    public int pageNumber;

    public VideoContainerViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(ShortVideoListModel videoListFromBundle) {
        if (videoListFromBundle == null || videoListFromBundle.list == null || videoListFromBundle.list.isEmpty()) {
            return;
        }
        VideoModel firstVideoModel = videoListFromBundle.list.get(0);
        String mediaId = "";
        if (firstVideoModel != null) {
            String url = firstVideoModel.getUrl();
            //没有可播放url时候，重新请求数据；
            if (TextUtils.isEmpty(url)) {
                mediaId = firstVideoModel.getId();
            }
        }
        if (!TextUtils.isEmpty(mediaId)) {
            mediaRepository.loadShortMedia(mediaId).subscribe(
                    new GVideoResponseObserver<ShortVideoListModel>() {
                        @Override
                        protected void onSuccess(@NonNull ShortVideoListModel listModel) {
                            feedLiveData.postValue(listModel);
                            pageNumber = Constant.PAGE_DEFAULT.FIRST_PAGE;
                            loadMore();
                        }

                        @Override
                        protected void onFailed(@NonNull Throwable throwable) {
                            super.onFailed(throwable);
                            pageNumber = Constant.PAGE_DEFAULT.FIRST_PAGE;
                            loadMore();
                        }
                    });
        } else {
            feedLiveData.postValue(videoListFromBundle);
            pageNumber = Constant.PAGE_DEFAULT.FIRST_PAGE;
            loadMore();
        }
    }

    public void loadMore() {
        PluginManager.get(FeedPlugin.class).loadMoreShortVideo(mediaId,pageNumber,PAGE_SIZE)
                .subscribe(new GVideoResponseObserver<ShortVideoListModel>() {
            @Override
            protected void onSuccess(@NonNull ShortVideoListModel shortVideoListModel) {
                if (DEBUG) {
                    Log.d(TAG, "loadMore model = " + shortVideoListModel);
                }
                feedLiveData.postValue(shortVideoListModel);
            }
        });
    }

}
