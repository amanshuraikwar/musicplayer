package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.mediaplayer.MediaPlayerCallbacks;
import app.sonu.com.musicplayer.mediaplayer.MediaPlayerService;
import app.sonu.com.musicplayer.mediaplayer.MediaPlayerServiceNew;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.mediaplayernew.util.MediaIdHelper;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerPresenter extends BasePresenter<MusicPlayerMvpView>
        implements MusicPlayerMvpPresenter, MediaBrowserManager.MediaBrowserCallback{

    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();
    private PublishSubject<MediaBrowserCompat.MediaItem> selectedSongSubject;
    private PublishSubject<MediaBrowserCompat.MediaItem> playSongSubject;
    private PublishSubject<Float> musicPlayerSlideSubject;
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<Integer> mMusicPlayerPanelPublishSubject;

    private Context mContext;

    //todo temp
    private PlaybackStateCompat mLastPlaybackState;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    mMvpView.displaySong(
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE),
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE),
                            getFormattedDuration(
                                    metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)),
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
                    mMvpView.updateDuration(
                            metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    mLastPlaybackState = state;
                    if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        mMvpView.showPauseIcon();
                        mMvpView.scheduleSeekbarUpdate();
                    } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
                        mMvpView.showPlayIcon();
                        mMvpView.stopSeekbarUpdate();

                        //to set the seekbar to correct position if somehow paused at
                        //a different position than shown on the ui
                        updateProgress();
                    } else if (state.getState() == PlaybackStateCompat.STATE_NONE
                            || state.getState() == PlaybackStateCompat.STATE_ERROR
                            || state.getState() == PlaybackStateCompat.STATE_STOPPED){
                        mMvpView.showPlayIcon();
                        mMvpView.stopSeekbarUpdate();
                        mMvpView.resetSeekbar();
                    }
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    Log.d(TAG, "onQueueChanged:called");
                    mMvpView.displayQueue(queue);
                }

                @Override
                public void onQueueTitleChanged(CharSequence title) {
                    Log.d(TAG, "onQueueTitleChanged:called");
                    super.onQueueTitleChanged(title);
                }

                @Override
                public void onShuffleModeChanged(boolean enabled) {
                    Log.d(TAG, "onShuffleModeChanged:called");
                    if (enabled) {
                        mMvpView.setShuffleModeEnabled();
                    } else {
                        mMvpView.setShuffleModeDisabled();
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.d(TAG, "onRepeatModeChanged:called");
                    switch (repeatMode) {
                        case PlaybackStateCompat.REPEAT_MODE_NONE:
                            mMvpView.setRepeatModeNone();
                            break;
                        case PlaybackStateCompat.REPEAT_MODE_ALL:
                            mMvpView.setRepeatModeAll();
                            break;
                        case PlaybackStateCompat.REPEAT_MODE_ONE:
                            mMvpView.setRepeatModeOne();
                    }
                }
            };

    public MusicPlayerPresenter(DataManager dataManager,
                                PublishSubject<MediaBrowserCompat.MediaItem> selectedSongSubject,
                                PublishSubject<MediaBrowserCompat.MediaItem> playSongSubject,
                                PublishSubject<Float> musicPlayerSlideSubject,
                                MediaBrowserManager browserManager,
                                PublishSubject<Integer> musicPlayerPanelPublishSubject) {
        super(dataManager);
        this.selectedSongSubject = selectedSongSubject;
        this.playSongSubject = playSongSubject;
        this.musicPlayerSlideSubject = musicPlayerSlideSubject;
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mMusicPlayerPanelPublishSubject = musicPlayerPanelPublishSubject;
    }

    private String getFormattedDuration(@NonNull long duration) {
        String formattedDuration = "";

        formattedDuration += duration/60000;
        formattedDuration += ":";
        formattedDuration += String.format("%02d", (duration/1000)%60);

        return formattedDuration;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {
//        selectedSongSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(MediaBrowserCompat.MediaItem value) {
//                Log.d(TAG, "selectedSongSubject:onNext:called");
//                mMvpView.displaySong(value);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//
//        playSongSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(MediaBrowserCompat.MediaItem value) {
//                mMvpView.showPauseIcon();
////                playAudio();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

//        musicPlayerSlideSubject.subscribe(new Observer<Float>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Float value) {
//                mMvpView.setMiniBarOpacity(1-value);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        this.mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);
    }

    @Override
    public void onCreateView() {
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onDestroy() {
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void playPauseButtonOnClick() {
        int pbState = mMediaBrowserManager.getMediaController().getPlaybackState().getState();
        Log.i(TAG, "playPauseButtonOnClick:pbState="+pbState+PlaybackStateCompat.STATE_PLAYING);
        if (pbState == PlaybackStateCompat.STATE_PLAYING) {
            mMediaBrowserManager.getMediaController().getTransportControls().pause();
        } else {
            mMediaBrowserManager.getMediaController().getTransportControls().play();
        }
    }

    @Override
    public void skipNextButtonOnClick() {
        Log.d(TAG, "skipNextIb onClick:called");
        mMediaBrowserManager.getMediaController().getTransportControls().skipToNext();
    }

    @Override
    public void skipPreviousButtonOnClick() {
        Log.d(TAG, "skipPreviousIb onClick:called");
        mMediaBrowserManager.getMediaController().getTransportControls().skipToPrevious();
    }

    @Override
    public void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mMvpView.setSeekBarPosition((int) currentPosition);
        mMvpView.setElapsedTime((int) currentPosition);
    }

    @Override
    public void onSeekbarStopTrackingTouch(int progress) {
        Log.d(TAG, "onSeekbarStopTrackingTouch:called progress="+progress);
        mMediaBrowserManager.getMediaController()
                .getTransportControls().seekTo(progress);
    }

    @Override
    public void onCollapseIvClick() {
        mMusicPlayerPanelPublishSubject.onNext(-1);
    }

    @Override
    public void onShuffleButtonClick() {
        if (mMediaBrowserManager.getMediaController().isShuffleModeEnabled()) {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleModeEnabled(false);
        } else {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleModeEnabled(true);
        }
    }

    @Override
    public void onRepeatButtonClick() {
        int curRepeatMode = mMediaBrowserManager.getMediaController().getRepeatMode();
        switch (curRepeatMode) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                break;
        }
    }

    //media browser implementations
    @Override
    public void onMediaBrowserConnected() {
//        mMediaBrowserManager.subscribeMediaBrowser();
    }

    @Override
    public void onMediaBrowserConnectionSuspended() {

    }

    @Override
    public void onMediaBrowserConnectionFailed() {

    }

    @Override
    public void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items) {

    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {

    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {

    }
}
