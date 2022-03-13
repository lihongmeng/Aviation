package com.jxntv.android.liteav.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jxntv.android.liteav.LiteavConstants;
import com.jxntv.android.liteav.R;
import com.jxntv.base.StaticParams;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.base.view.ScreenProjectionLayout;
import com.ruffian.library.widget.REditText;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * A view containing controls for a MediaPlayer. Typically contains the
 * buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress
 * slider. It takes care of synchronizing the controls with the state
 * of the MediaPlayer.
 * <p>
 * The way to use this class is to instantiate it programatically.
 * The MediaController will create a default set of controls
 * and put them in a window floating above your application. Specifically,
 * the controls will float above the view specified with setAnchorView().
 * The window will disappear if left idle for three seconds and reappear
 * when the user touches the anchor view.
 * <p>
 * Functions like show() and hide() have no effect when MediaController
 * is created in an xml layout.
 *
 * MediaController will hide and
 * show the buttons according to these rules:
 * <ul>
 * <li> The "previous" and "next" buttons are hidden until setPrevNextListeners()
 * has been called
 * <li> The "previous" and "next" buttons are visible but disabled if
 * setPrevNextListeners() was called with null listeners
 * <li> The "rewind" and "fastforward" buttons are shown unless requested
 * otherwise by using the MediaController(Context, boolean) constructor
 * with the boolean set to false
 * </ul>
 */
public class VideoControllerView extends FrameLayout {
  private static final String TAG = "VideoControllerView";

  protected IMediaPlayerControl mPlayer;
  private Context mContext;
  private ViewGroup mAnchor;
  protected View mRoot;
  protected ProgressBar mProgress;
  protected ProgressBar mSimpleProgress;
  protected TextView mCurrentTime;
  protected TextView mEndTime;
  private boolean mShowing;
  protected boolean mDragging;
  private static final int sDefaultTimeout = 6 * 1000;
  private static final int FADE_OUT = 1;
  private static final int SHOW_PROGRESS = 2;
  private boolean mUseFastForward;
  private boolean mFromXml;
  private boolean mListenersSet;
  private View.OnClickListener mNextListener, mPrevListener;
  private StringBuilder mFormatBuilder;
  private Formatter mFormatter;
  protected ImageView mPauseButton;
  private View mFfwdButton;
  private View mRewButton;
  private View mNextButton;
  private View mPrevButton;
  private ImageView mFullscreenButton;

  protected View screenProjection;
  protected ScreenProjectionLayout screenProjectionController;

  private Handler mHandler = new MessageHandler(this);

  public VideoControllerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mRoot = null;
    mContext = context;
    mUseFastForward = true;
    mFromXml = true;

    Log.i(TAG, TAG);
  }

  public View getFullscreenButton(){
    return mFullscreenButton;
  }

  public VideoControllerView(Context context, boolean useFastForward) {
    super(context);
    mContext = context;
    mUseFastForward = useFastForward;

    Log.i(TAG, TAG);
  }

  public VideoControllerView(Context context) {
    this(context, true);

    Log.i(TAG, TAG);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();
    if (mRoot != null) {
      initControllerView(mRoot);
    }
  }

  public void setMediaPlayer(IMediaPlayerControl player) {
    mPlayer = player;
    updatePausePlay();
    updateFullScreen();
  }

  /**
   * Set the view that acts as the anchor for the control view.
   * This can for example be a VideoView, or your Activity's main view.
   *
   * @param view The view to which to anchor the controller when it is visible.
   */
  public void setAnchorView(ViewGroup view) {
    mAnchor = view;

    FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    );

    removeAllViews();
    View v = makeControllerView();
    addView(v, frameParams);

    FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
        Gravity.BOTTOM
    );
    mAnchor.addView(this, tlp);
  }

  /**
   * Create the view that holds the widgets that control playback.
   * Derived classes can override this to create their own.
   *
   * @return The controller view.
   * @hide This doesn't work as advertised
   */
  protected View makeControllerView() {
    LayoutInflater inflate =
        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mRoot = inflate.inflate(R.layout.media_controller, null);

    initControllerView(mRoot);

    return mRoot;
  }

  protected void initControllerView(View v) {
    mPauseButton = v.findViewById(R.id.pause);
    if (mPauseButton != null) {
      mPauseButton.requestFocus();
      mPauseButton.setOnClickListener(mPauseListener);
    }

    mFullscreenButton = v.findViewById(R.id.fullscreen);
    if (mFullscreenButton != null) {
      mFullscreenButton.requestFocus();
      mFullscreenButton.setOnClickListener(mFullscreenListener);
    }

    mFfwdButton = v.findViewById(R.id.ffwd);
    if (mFfwdButton != null) {
      mFfwdButton.setOnClickListener(mFfwdListener);
      if (!mFromXml) {
        mFfwdButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
      }
    }

    mRewButton = v.findViewById(R.id.rew);
    if (mRewButton != null) {
      mRewButton.setOnClickListener(mRewListener);
      if (!mFromXml) {
        mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
      }
    }

    // By default these are hidden. They will be enabled when setPrevNextListeners() is called
    mNextButton = v.findViewById(R.id.next);
    if (mNextButton != null && !mFromXml && !mListenersSet) {
      mNextButton.setVisibility(View.GONE);
    }
    mPrevButton = v.findViewById(R.id.prev);
    if (mPrevButton != null && !mFromXml && !mListenersSet) {
      mPrevButton.setVisibility(View.GONE);
    }

    mProgress = v.findViewById(R.id.mediacontroller_progress);
    if (mProgress != null) {
      if (mProgress instanceof SeekBar) {
        SeekBar seeker = (SeekBar) mProgress;
        seeker.setOnSeekBarChangeListener(mSeekListener);
      }
      mProgress.setMax(100);
    }
    if (mSimpleProgress != null) {
      mSimpleProgress.setMax(100);
    }

    mEndTime = v.findViewById(R.id.time);
    mCurrentTime = v.findViewById(R.id.time_current);
    mFormatBuilder = new StringBuilder();
    mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    installPrevNextListeners();
  }

  /**
   * Show the controller on screen. It will go away
   * automatically after 3 seconds of inactivity.
   */
  public void show() {
    show(sDefaultTimeout);
  }

  /**
   * Disable pause or seek buttons if the stream cannot be paused or seeked.
   * This requires the control interface to be a MediaPlayerControlExt
   */
  private void disableUnsupportedButtons() {
    if (mPlayer == null) {
      return;
    }

    try {
      if (mPauseButton != null && !mPlayer.canPause()) {
        mPauseButton.setEnabled(false);
      }
      if (mRewButton != null && !mPlayer.canSeekBackward()) {
        mRewButton.setEnabled(false);
      }
      if (mFfwdButton != null && !mPlayer.canSeekForward()) {
        mFfwdButton.setEnabled(false);
      }
      if (mFullscreenButton != null && !mPlayer.canFullscreen()) {
        mFullscreenButton.setVisibility(View.GONE);
      }
    } catch (IncompatibleClassChangeError ex) {
      // We were given an old version of the interface, that doesn't have
      // the canPause/canSeekXYZ methods. This is OK, it just means we
      // assume the media can be paused and seeked, and so we don't disable
      // the buttons.
    }
  }

  /**
   * Show the controller on screen. It will go away
   * automatically after 'timeout' milliseconds of inactivity.
   *
   * @param timeout The timeout in milliseconds. Use 0 to show
   * the controller until hide() is called.
   */
  public void show(int timeout) {
    if (!mShowing && mAnchor != null) {
      setProgress();
      if (mPauseButton != null) {
        mPauseButton.requestFocus();
      }
      disableUnsupportedButtons();

      //FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
      //    ViewGroup.LayoutParams.MATCH_PARENT,
      //    ViewGroup.LayoutParams.MATCH_PARENT,
      //    Gravity.BOTTOM
      //);
      //mAnchor.addView(this, tlp);
      mRoot.setVisibility(View.VISIBLE);
      mShowing = true;
      if (mPlayer != null) {
        mPlayer.onShow();
      }
    }
    updatePausePlay();
    updateFullScreen();

    // cause the progress bar to be updated even if mShowing
    // was already true.  This happens, for example, if we're
    // paused with the progress bar showing the user hits play.
    mHandler.removeMessages(SHOW_PROGRESS);
    mHandler.sendEmptyMessage(SHOW_PROGRESS);

    Message msg = mHandler.obtainMessage(FADE_OUT);
    mHandler.removeMessages(FADE_OUT);
    if (timeout > 0) {
      mHandler.sendMessageDelayed(msg, timeout);
    }
  }

  public boolean isShowing() {
    return mShowing;
  }

  /**
   * Remove the controller from the screen.
   */
  public void hide() {
    if (mAnchor == null) {
      return;
    }

    try {
      //mAnchor.removeView(this);
      mRoot.setVisibility(View.GONE);
      mHandler.removeMessages(SHOW_PROGRESS);
    } catch (IllegalArgumentException ex) {
      Log.w("MediaController", "already removed");
    }
    mShowing = false;
    if (mPlayer != null) {
      mPlayer.onHide();
    }
  }

  public void requestRemove() {
    if (mAnchor != null) {
      try {
        mAnchor.removeView(this);
      } catch (IllegalArgumentException ex) {
        Log.w("MediaController", "already removed");
      }
    }
  }

  public void onReplayShow(boolean show) {

  }
  public void onPlayEnd(boolean showReplay) {

  }
  public void showTip(String tip, boolean isError) {

  }
  public void setTitleVisible(@LiteavConstants.TitleMode int mode) {

  }
  public void setShareVisible(@LiteavConstants.TitleMode int mode) {

  }
  public void setScreenProjectionVisible(@LiteavConstants.TitleMode int mode) {

  }
  public void updateTitle(String title, @TagHelper.GvideoTagType int tag) {

  }

  protected void toggleControllerView() {
    if (isShowing()) {
      hide();
    } else {
      show();
    }
  }

  protected String stringForTime(int timeMs) {
    int totalSeconds = timeMs;

    int seconds = totalSeconds % 60;
    int minutes = (totalSeconds / 60) % 60;
    int hours = totalSeconds / 3600;

    mFormatBuilder.setLength(0);
    if (hours > 0) {
      return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
    } else {
      return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }
  }

  private int setProgress() {
    if (mPlayer == null || mDragging) {
      return 0;
    }

    int position = mPlayer.getCurrentPosition();
    int duration = mPlayer.getDuration();
    if (mProgress != null) {
      if (duration > 0) {
        // use long to avoid overflow
        long pos = 100L * position / duration;
        mProgress.setProgress((int) pos);
      }
      int percent = mPlayer.getBufferPercentage();
      mProgress.setSecondaryProgress(percent);
    }
    if (mSimpleProgress != null) {
      if (duration > 0) {
        long pos = 100L * position / duration;
        mSimpleProgress.setProgress((int) pos);
      }
      int percent = mPlayer.getBufferPercentage();
      mSimpleProgress.setSecondaryProgress(percent);
    }

    if (mEndTime != null) {
      mEndTime.setText(stringForTime(duration));
    }
    if (mCurrentTime != null) {
      mCurrentTime.setText(stringForTime(position));
    }

    return position;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    show();
    return true;
  }

  @Override
  public boolean onTrackballEvent(MotionEvent ev) {
    show();
    return false;
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (mPlayer == null) {
      return true;
    }

    int keyCode = event.getKeyCode();
    final boolean uniqueDown = event.getRepeatCount() == 0
        && event.getAction() == KeyEvent.ACTION_DOWN;
    if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
        || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
        || keyCode == KeyEvent.KEYCODE_SPACE) {
      if (uniqueDown) {
        doPauseResume();
        show();
        if (mPauseButton != null) {
          mPauseButton.requestFocus();
        }
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
      if (uniqueDown && !mPlayer.isPlaying()) {
        mPlayer.start();
        updatePausePlay();
        show();
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
        || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
      if (uniqueDown && mPlayer.isPlaying()) {
        mPlayer.pause();
        updatePausePlay();
        show();
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        || keyCode == KeyEvent.KEYCODE_VOLUME_UP
        || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
      // don't show the controls for volume adjustment
      return super.dispatchKeyEvent(event);
    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
      if (uniqueDown) {
        hide();
      }
      return true;
    }

    show();
    return super.dispatchKeyEvent(event);
  }

  private View.OnClickListener mPauseListener = new View.OnClickListener() {
    public void onClick(View v) {
      doPauseResume();
      show();
    }
  };

  private final View.OnClickListener mFullscreenListener = v -> {
    doToggleFullscreen();
    show();
  };

  public void updatePausePlay() {
    if (mRoot == null || mPauseButton == null || mPlayer == null) {
      return;
    }

    if (mPlayer.isPlaying()) {
      mPauseButton.setImageResource(R.drawable.icon_video_pause);
    } else {
      mPauseButton.setImageResource(R.drawable.icon_video_play);
    }
  }

  /**
   * 视频暂停，更新底部按钮为播放状态
   */
  public void changePausePlay(){
    if (mRoot == null || mPauseButton == null || mPlayer == null) {
      return;
    }
    mPauseButton.setImageResource(R.drawable.icon_video_play);
  }

  public void updateFullScreen() {
    if (mRoot == null || mFullscreenButton == null || mPlayer == null) {
      return;
    }

    if (mPlayer.isFullScreen()) {
      mFullscreenButton.setImageResource(R.drawable.icon_video_fullscreen);
    } else {
      mFullscreenButton.setImageResource(R.drawable.icon_video_fullscreen);
    }
  }

  protected void doPauseResume() {
    if (mPlayer == null) {
      return;
    }

    if (mPlayer.isPlaying()) {
      mPlayer.pause();
    } else {
      mPlayer.start();
    }
    updatePausePlay();
  }

  private void doToggleFullscreen() {
    if (mPlayer == null) {
      return;
    }

    /*
     * 有两种情况
     * 全屏变非全屏，非全屏变全屏
     * 前者就锁定非全屏，不锁全屏
     * 后者则相反
     */
    boolean isFullScreen = mPlayer.isFullScreen();
    StaticParams.needTemplateLockFullScreen = !isFullScreen;
    StaticParams.needTemplateLockNotFullScreen = isFullScreen;

    mPlayer.toggleFullScreen();
  }

  // There are two scenarios that can trigger the seekbar listener to trigger:
  //
  // The first is the user using the touchpad to adjust the posititon of the
  // seekbar's thumb. In this case onStartTrackingTouch is called followed by
  // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
  // We're setting the field "mDragging" to true for the duration of the dragging
  // session to avoid jumps in the position in case of ongoing playback.
  //
  // The second scenario involves the user operating the scroll ball, in this
  // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
  // we will simply apply the updated position without suspending regular updates.
  private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
    public void onStartTrackingTouch(SeekBar bar) {
      show();

      mDragging = true;

      // By removing these pending progress messages we make sure
      // that a) we won't update the progress while the user adjusts
      // the seekbar and b) once the user is done dragging the thumb
      // we will post one of these messages to the queue again and
      // this ensures that there will be exactly one message queued up.
      mHandler.removeMessages(SHOW_PROGRESS);
    }

    public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
      if (mPlayer == null) {
        return;
      }

      if (!fromuser) {
        // We're not interested in programmatically generated changes to
        // the progress bar's position.
        return;
      }

      long duration = mPlayer.getDuration();
      long newposition = (long) (duration * progress / 100f);
      mPlayer.seekTo((int) newposition);
      if (mCurrentTime != null) {
        mCurrentTime.setText(stringForTime((int) newposition));
      }
    }

    public void onStopTrackingTouch(SeekBar bar) {
      mDragging = false;
      setProgress();
      updatePausePlay();
      show();

      // Ensure that progress is properly updated in the future,
      // the call to show() does not guarantee this because it is a
      // no-op if we are already showing.
      mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }
  };

  @Override
  public void setEnabled(boolean enabled) {
    if (mPauseButton != null) {
      mPauseButton.setEnabled(enabled);
    }
    if (mFfwdButton != null) {
      mFfwdButton.setEnabled(enabled);
    }
    if (mRewButton != null) {
      mRewButton.setEnabled(enabled);
    }
    if (mNextButton != null) {
      mNextButton.setEnabled(enabled && mNextListener != null);
    }
    if (mPrevButton != null) {
      mPrevButton.setEnabled(enabled && mPrevListener != null);
    }
    if (mProgress != null) {
      mProgress.setEnabled(enabled);
    }
    disableUnsupportedButtons();
    super.setEnabled(enabled);
  }

  private View.OnClickListener mRewListener = new View.OnClickListener() {
    public void onClick(View v) {
      if (mPlayer == null) {
        return;
      }

      int pos = mPlayer.getCurrentPosition();
      pos -= 5000; // milliseconds
      mPlayer.seekTo(pos);
      setProgress();

      show();
    }
  };

  private View.OnClickListener mFfwdListener = new View.OnClickListener() {
    public void onClick(View v) {
      if (mPlayer == null) {
        return;
      }

      int pos = mPlayer.getCurrentPosition();
      pos += 15000; // milliseconds
      mPlayer.seekTo(pos);
      setProgress();

      show();
    }
  };

  private void installPrevNextListeners() {
    if (mNextButton != null) {
      mNextButton.setOnClickListener(mNextListener);
      mNextButton.setEnabled(mNextListener != null);
    }

    if (mPrevButton != null) {
      mPrevButton.setOnClickListener(mPrevListener);
      mPrevButton.setEnabled(mPrevListener != null);
    }
  }

  public void setPrevNextListeners(View.OnClickListener next, View.OnClickListener prev) {
    mNextListener = next;
    mPrevListener = prev;
    mListenersSet = true;

    if (mRoot != null) {
      installPrevNextListeners();

      if (mNextButton != null && !mFromXml) {
        mNextButton.setVisibility(View.VISIBLE);
      }
      if (mPrevButton != null && !mFromXml) {
        mPrevButton.setVisibility(View.VISIBLE);
      }
    }
  }

  public void hideScreenProjectionLayout(){
    if(screenProjectionController==null){
      return;
    }
    screenProjectionController.setVisibility(GONE);
  }

  private static class MessageHandler extends Handler {
    private final WeakReference<VideoControllerView> mView;

    MessageHandler(VideoControllerView view) {
      mView = new WeakReference<VideoControllerView>(view);
    }

    @Override
    public void handleMessage(Message msg) {
      VideoControllerView view = mView.get();
      if (view == null || view.mPlayer == null) {
        return;
      }

      int pos;
      switch (msg.what) {
        case FADE_OUT:
          view.hide();
          break;
        case SHOW_PROGRESS:
          pos = view.setProgress();
          if (!view.mDragging && view.mShowing && view.mPlayer.isPlaying()) {
            msg = obtainMessage(SHOW_PROGRESS);
            sendMessageDelayed(msg, 100 - (pos % 100));
          }
          break;
      }
    }
  }
}