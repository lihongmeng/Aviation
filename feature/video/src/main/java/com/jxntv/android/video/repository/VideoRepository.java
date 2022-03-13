package com.jxntv.android.video.repository;

import com.jxntv.android.video.Constants;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.schedule.GVideoSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;

@Deprecated
public class VideoRepository extends BaseDataRepository {
  public Observable<ShortVideoListModel> loadShortVideoModel(boolean loadMore) {
    return Observable.create((ObservableOnSubscribe<ShortVideoListModel>) e -> {
      AuthorModel author = AuthorModel.Builder.anAuthorModel()
          .withId("1")
          .withName("金牌调解室")
          .withAvatar(Constants.AVATAR_URL)
          .withIntro("江西广播电台金牌社会节目")
          .build();
      ArrayList<String> urls = createVideoList();
      ArrayList<VideoModel> videos = new ArrayList<>();
      for (int i = 0; i < urls.size(); i++) {
        String url = urls.get(i);
        ArrayList<String> mediaUrls = new ArrayList<>();
        mediaUrls.add(url);
        VideoModel vm = VideoModel.Builder.aVideoModel()
            .withAuthor(author)
            .withTitle(i + ":走遍世界第一季-一起探寻贝加尔湖 一起再探寻贝加尔湖-走遍世界第一季-一起探寻贝加尔湖 一起再探寻贝加尔湖;")
            .withId("" + i)
            .withMediaType(MediaType.SHORT_VIDEO)
            .withMediaUrls(mediaUrls)
            .withCoverUrl(Constants.COVER_URL)
            .withReviews(9999)
            .build();
        videos.add(vm);
      }

      ShortVideoListModel fm = ShortVideoListModel.Builder.aFeedModel()
          .withLoadMore(loadMore)
          .withCursor("")
          .withList(videos)
          .build();

      e.onNext(fm);
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static ShortVideoListModel create() {
    AuthorModel author = AuthorModel.Builder.anAuthorModel()
        .withId("1")
        .withName("金牌调解室")
        .withAvatar(Constants.AVATAR_URL)
        .withIntro("江西广播电台金牌社会节目")
        .build();
    ArrayList<String> urls = createVideoList();
    ArrayList<VideoModel> videos = new ArrayList<>();
    for (int i = 0; i < urls.size(); i++) {
      String url = urls.get(i);
      ArrayList<String> mediaUrls = new ArrayList<>();
      mediaUrls.add(url);
      VideoModel vm = VideoModel.Builder.aVideoModel()
          .withAuthor(author)
          .withTitle(i + ":走遍世界第一季-一起探寻贝加尔湖 一起再探寻贝加尔湖-走遍世界第一季-一起探寻贝加尔湖 一起再探寻贝加尔湖;")
          .withId("" + i)
          .withMediaType(MediaType.SHORT_VIDEO)
          .withMediaUrls(mediaUrls)
          .withCoverUrl(Constants.COVER_URL)
          .withReviews(9999)
          .build();
      videos.add(vm);
    }

    ShortVideoListModel fm = ShortVideoListModel.Builder.aFeedModel()
        .withLoadMore(false)
        .withCursor("")
        .withList(videos)
        .build();

    return fm;
  }

  public static ArrayList<String> createVideoList() {
    ArrayList<String> l = new ArrayList<>();
    l.add("https://cdn.v.pdnews.cn/photo_video/YzhiMDk5NmI1NTg2MDE0ZWJlODU1NTUxOGFmZmU5MzM=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZGRjNmNkYzhjMzg4ZTAxMTdmN2E0ZWQ0NTFmNDdlOTI=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/MWI2YmIzZTQ2Nzc1NTY5YmY5ZGEyODhlNTZjZmYwZWQ=.mp4");


    l.add("https://cdn.v.pdnews.cn/photo_video/NDIzYmZiZTUwYTBhOTExZjQ4OGE4M2EyMTNiNDMzZDI=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/MDhlZjllMDBkNGZhZjUyNjY1MWFmMDViYmEyNjVmZmI=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZjdmYjA2ZGZiNDY3ZTEyNDYxMzhhNDg3OTE3MjVkNmI=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZTRmNGYzMWUzY2FjNzY5NjU3YzQ3MmFiMjA5ZDNmODQ=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/Y2JkOWFiZWFiOWJhMzYyZDFkMzcyNjQwYzEwNzVhZWI=.mp4");

    l.add("https://cdn.v.pdnews.cn/photo_video/ZDgwM2JkODY0YWVkNjg1YThjNzQxNGEzOGIxOWVmODg=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/NzhiNGNjN2Q4NjBkODg0MzkyZGY2MDllYTcxODNhMjU=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZDZiNzZlNTdkODIwODRjYWM1NGY4YWY4MzU2MTg0YTA=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/MGUzMWI4ZDJhMmY3NmVlY2VlMmU5YWNmOTkzMDRiZDE=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZjMzMDAwMzI1ZWIyZjMzOGQ2ZTE1YWUyOTdmN2VlMGQ=.mp4");

    l.add("https://cdn.v.pdnews.cn/photo_video/ODc0ZWZkNzAzODIxOGUzYTI3NzhiMWEzYmUxYmVjOTc=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/MjEwYzhlMmY1NWIxN2EyODY5YjQ1YzJmYzU1ZjIzNGY=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ZmJkYjcyNDNkNTUzZTI2ZWY1NmUxODAxZDVlYjM4NzQ=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ODA1YzJiMWVkY2E4ZmI0YThjMmQ5OWU5OGU0YWIwYjM=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/ODg1MzRjNzczOWFiNGY3Yjk4NTdmNzFjZGJhNDhkNTQ=.mp4");

    l.add("https://cdn.v.pdnews.cn/photo_video/YzA3MjU1MTIyNjJhOGY3NzFlNzdmZDQ1MTQwMGQ5NWU=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/NThiYTExYTViNTQyMmFmNzMzOTgwZTEzMDc2ODYyMjY=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/NDY3NDdhZWI3MGFkODNmYzM3NWIxMjU3ZTE5OGQ2N2E=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/OGQ4Yjk2YmQ3NTc5NzA4MmNmNGVhNDBmNDU5NGQwNWU=.mp4");
    l.add("https://cdn.v.pdnews.cn/photo_video/MDI0MDk5ZDVkOGNmZGMxOWJjYmU1ZGEyZTVlMjYwMzY=.mp4");




    return l;
  }
}
