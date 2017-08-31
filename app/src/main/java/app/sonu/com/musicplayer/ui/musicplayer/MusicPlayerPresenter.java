package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
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
import app.sonu.com.musicplayer.mediaplayernew.playback.Playback;
import app.sonu.com.musicplayer.mediaplayernew.util.MediaIdHelper;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerPresenter extends BasePresenter<MusicPlayerMvpView>
        implements MusicPlayerMvpPresenter, MediaBrowserManager.MediaBrowserCallback{

    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<Integer> mQueueIndexUpdatedSubject;
    private PublishSubject<Integer> mMusicPlayerPanelPublishSubject;

    private Context mContext;

    private Disposable mQueueIndexUpdatedDisposable;

    //todo temp
    private PlaybackStateCompat mLastPlaybackState;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    displayMetadata(metadata);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    mLastPlaybackState = state;
                    updatePlaybackState();
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    Log.d(TAG, "onQueueChanged:called");
                    updateQueue(queue);
                }

                @Override
                public void onQueueTitleChanged(CharSequence title) {
                    Log.d(TAG, "onQueueTitleChanged:called");
                    super.onQueueTitleChanged(title);
                }

                @Override
                public void onShuffleModeChanged(boolean enabled) {
                    Log.d(TAG, "onShuffleModeChanged:called");
                    updateShuffleMode(enabled);
                    Log.w(TAG,"sssss="+mMediaBrowserManager.getMediaController().isShuffleModeEnabled()+"");
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.d(TAG, "onRepeatModeChanged:called");
                    updateRepeatMode(repeatMode);
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    super.onSessionEvent(event, extras);
                }
            };

    private void displayMetadata(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        mMvpView.displaySong(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE),
                metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE),
                getFormattedDuration(
                        metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)),
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        mMvpView.updateDuration(
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    private void updatePlaybackState() {
        if (mLastPlaybackState == null) {
            return;
        }
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mMvpView.showPauseIcon();
            mMvpView.scheduleSeekbarUpdate();
        } else if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();

            //to set the seekbar to correct position if somehow paused at
            //a different position than shown on the ui
            updateProgress();
        } else if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_NONE
                || mLastPlaybackState.getState() == PlaybackStateCompat.STATE_ERROR
                || mLastPlaybackState.getState() == PlaybackStateCompat.STATE_STOPPED){
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();
            mMvpView.resetSeekbar();
        }
    }

    private void updateShuffleMode(boolean enabled) {
        if (enabled) {
            mMvpView.setShuffleModeEnabled();
        } else {
            mMvpView.setShuffleModeDisabled();
        }
    }

    private void updateRepeatMode(int repeatMode) {
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

    private void updateQueue(List<MediaSessionCompat.QueueItem> queue) {
        if (queue == null) {
            return;
        }

        mMvpView.displayQueue(queue);
    }

    public MusicPlayerPresenter(DataManager dataManager,
                                MediaBrowserManager browserManager,
                                PublishSubject<Integer> queueIndexUpdatedSubject,
                                PublishSubject<Integer> musicPlayerPanelPublishSubject) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mQueueIndexUpdatedSubject = queueIndexUpdatedSubject;
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
        mQueueIndexUpdatedDisposable.dispose();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        this.mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);

        mQueueIndexUpdatedDisposable = mQueueIndexUpdatedSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (!mMvpView.updateQueueIndex(integer)) {
                    updateQueue(mMediaBrowserManager.getMediaController().getQueue());
                }
            }
        });
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

        Log.w(TAG, "shuffle mode got on click="
                +mMediaBrowserManager.getMediaController().isShuffleModeEnabled());

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

    @Override
    public void onQueueItemClick(MediaSessionCompat.QueueItem item) {
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getDescription().getMediaId(), null);
    }

    //media browser implementations
    @Override
    public void onMediaBrowserConnected() {
        mMediaBrowserManager.subscribeMediaBrowser();
    }

    @Override
    public void onMediaBrowserConnectionSuspended() {

    }

    @Override
    public void onMediaBrowserConnectionFailed() {

    }

    @Override
    public void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items) {
        mLastPlaybackState = mMediaBrowserManager.getMediaController().getPlaybackState();
        updatePlaybackState();
        displayMetadata(mMediaBrowserManager.getMediaController().getMetadata());
        updateQueue(mMediaBrowserManager.getMediaController().getQueue());
        updateShuffleMode(mMediaBrowserManager.getMediaController().isShuffleModeEnabled());
        updateRepeatMode(mMediaBrowserManager.getMediaController().getRepeatMode());

        Bundle b = mMediaBrowserManager.getMediaController().getPlaybackState().getExtras();
        if (b != null) {
            mMvpView.updateQueueIndex(b.getInt(Playback.PLAYBACK_STATE_EXTRA_CURRENT_QUEUE_INDEX));
        }
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {

    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {

    }
}
