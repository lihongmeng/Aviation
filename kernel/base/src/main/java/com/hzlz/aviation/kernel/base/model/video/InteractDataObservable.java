package com.hzlz.aviation.kernel.base.model.video;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import java.util.HashMap;
import java.util.Map;

/**
 * VideoModel中可变数据监听器。
 * 对于关注、喜欢、评论数等可变数据，在数据层进行通知。不依赖广播上层处理。
 */
public class InteractDataObservable {
  private InteractDataObservable() {}
  private static class INNER {
    private static final InteractDataObservable INSTANCE = new InteractDataObservable();
  }
  public static final InteractDataObservable getInstance() {
    return INNER.INSTANCE;
  }

  private Map<String, ObservableBoolean> mFavoriteObservableMap = new HashMap<>();

  ObservableBoolean getFavorObservable(String mediaId) {
    ObservableBoolean favorObservable = mFavoriteObservableMap.get(mediaId);
    if (favorObservable == null) {
      favorObservable = new ObservableBoolean();
      mFavoriteObservableMap.put(mediaId, favorObservable);
    }
    return favorObservable;
  }
  public void setFavorite(String mediaId, boolean favorite) {
    getFavorObservable(mediaId).set(favorite);
  }

  boolean containsFavorite(String mediaId) {
    return mFavoriteObservableMap.containsKey(mediaId);
  }

  void removeFavorite(String mediaId) {
    //mFavoriteChangeMap.remove(mediaId);
    //mFavoriteObservableMap.remove(mediaId);
  }


  private Map<String, ObservableBoolean> mFollowObservableMap = new HashMap<>();
  public ObservableBoolean getFollowObservable(String authorId) {
    ObservableBoolean followObservable = mFollowObservableMap.get(authorId);
    if (followObservable == null) {
      followObservable = new ObservableBoolean();
      mFollowObservableMap.put(authorId, followObservable);
    }
    return followObservable;
  }
  public void setFollow(String authorId, boolean follow) {
    getFollowObservable(authorId).set(follow);
  }
  boolean containsFollow(String authorId) {
    return mFollowObservableMap.containsKey(authorId);
  }
  void removeFollow(String authorId) {
    //mFollowChangeMap.remove(authorId);
    //mFollowObservableMap.remove(authorId);
  }

  private Map<String, ObservableInt> mCommentObservableMap = new HashMap<>();
  public ObservableInt getCommentObservable(String mediaId) {
    ObservableInt commentObservable = mCommentObservableMap.get(mediaId);
    if (commentObservable == null) {
      commentObservable = new ObservableInt();
      mCommentObservableMap.put(mediaId, commentObservable);
    }
    return commentObservable;
  }
  public void setCommentCount(String mediaId, int commentCount) {
    getCommentObservable(mediaId).set(commentCount);
  }
  boolean containsComment(String mediaId) {
    return mCommentObservableMap.containsKey(mediaId);
  }
  void removeComment(String mediaId) {

  }


  private Map<String, ObservableInt> mFavorCountObservableMap = new HashMap<>();
  public ObservableInt getFavorCountObservable(String mediaId) {
    ObservableInt favorCountObservable = mFavorCountObservableMap.get(mediaId);
    if (favorCountObservable == null) {
      favorCountObservable = new ObservableInt();
      mFavorCountObservableMap.put(mediaId, favorCountObservable);
    }
    return favorCountObservable;
  }
  public void setFavorCount(String mediaId, int favorCount) {
    getFavorCountObservable(mediaId).set(favorCount);
  }
  boolean containsFavorCount(String mediaId) {
    return mFavorCountObservableMap.containsKey(mediaId);
  }


  private Map<String, ObservableBoolean> mJoinCircleObservableMap = new HashMap<>();
  public ObservableBoolean getJoinCircleObservable(String groupId) {
    ObservableBoolean observableBoolean = mJoinCircleObservableMap.get(groupId);
    if (observableBoolean == null) {
      observableBoolean = new ObservableBoolean();
      mJoinCircleObservableMap.put(groupId, observableBoolean);
    }
    return observableBoolean;
  }
  public ObservableBoolean getJoinCircleObservable(long groupId) {
    return getJoinCircleObservable(String.valueOf(groupId));
  }

  public void setJoinCircle(long groupId, boolean isJoin) {
    getJoinCircleObservable(String.valueOf(groupId)).set(isJoin);
  }
  public void setJoinCircle(String groupId, boolean isJoin) {
    getJoinCircleObservable(groupId).set(isJoin);
  }

  private final Map<String, ObservableInt> mPraiseCountObservableMap = new HashMap<>();
  public ObservableInt getPraiseCountObservable(String commentId) {
    ObservableInt praiseCommentObservable = mPraiseCountObservableMap.get(commentId);
    if (praiseCommentObservable == null) {
      praiseCommentObservable = new ObservableInt();
      mPraiseCountObservableMap.put(commentId, praiseCommentObservable);
    }
    return praiseCommentObservable;
  }
  public void setPraiseCountObservable(String commentId, int praiseCount) {
    int count = getPraiseCountObservable(commentId).get();
    //因为后台不会返回数量，暂时本地处理数据
    if (praiseCount == -1){
      getPraiseCountObservable(commentId).set((count+1));
    }else if (praiseCount == -2){
      int c = Math.max((count- 1), 0);
      getPraiseCountObservable(commentId).set(c);
    }else {
      getPraiseCountObservable(commentId).set(praiseCount);
    }
  }


  private final Map<String, ObservableBoolean> isPraiseCommentObservableMap = new HashMap<>();
  public ObservableBoolean isPraiseCommentObservable(String commentId) {
    ObservableBoolean isPraise = isPraiseCommentObservableMap.get(commentId);
    if (isPraise == null) {
      isPraise = new ObservableBoolean();
      isPraiseCommentObservableMap.put(commentId, isPraise);
    }
    return isPraise;
  }
  public void setPraiseCommentObservable(String commentId, boolean isPraise) {
    isPraiseCommentObservable(commentId).set(isPraise);
  }

}
