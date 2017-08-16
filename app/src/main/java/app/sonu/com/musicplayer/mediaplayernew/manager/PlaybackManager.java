package app.sonu.com.musicplayer.mediaplayernew.manager;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;
import app.sonu.com.musicplayer.mediaplayernew.playback.Playback;

/**
 * Created by sonu on 28/7/17.
 */

public class PlaybackManager implements Playback.Callback {

    private static final String TAG = PlaybackManager.class.getSimpleName();

    private MusicProvider mMusicProvider;
    private QueueManager mQueueManager;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;

    public PlaybackManager(PlaybackServiceCallback serviceCallback,
                           MusicProvider musicProvider,
                           QueueManager queueManager,
                           Playback playback) {
        mMusicProvider = musicProvider;
        mServiceCallback = serviceCallback;
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mPlayback.setCallback(this);
    }

    private void handlePlayRequest(){
        Log.d(TAG, "handlePlayRequest: mState=" + mPlayback.getState());
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentMusic);
        }
    }

    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + mPlayback.getState());
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * Update the current media player state, optionally showing an error message.
     *
     * @param error if not null, error message to present to the user.
     */
    public void updatePlaybackState(String error) {
        Log.d(TAG, "updatePlaybackState:playback state=" + mPlayback.getState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null) {
            position = mPlayback.getCurrentPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(0, error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        // Set the activeQueueItemId if the current index is valid.
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        //todo understand
        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    //implements playback.callback
    @Override
    public void onCompletion() {
        mQueueManager.skipQueuePosition(1);
        handlePlayRequest();
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        Log.d(TAG, "onPlaybackStatusChanged:state="+state);
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayback.play();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "onPlayFromMediaId:called");
            Log.d(TAG, "onPlayFromMediaId:mediaId="+mediaId);
            Log.d(TAG, "onPlayFromMediaId:bundle="+extras);

            mQueueManager.setQueueFromMediaId(mediaId);
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onSkipToNext() {
            mQueueManager.skipQueuePosition(1);
            handlePlayRequest();
        }

        @Override
        public void onSkipToPrevious() {
            mQueueManager.skipQueuePosition(-1);
            handlePlayRequest();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
        }

        //todo deprecated in 26.0.0-beta-2
        @Override
        public void onSetShuffleModeEnabled(boolean enabled) {
            Log.d(TAG, "onSetShuffleModeEnabled:called");
            Log.d(TAG, "onSetShuffleModeEnabled:enabled="+enabled);
            if (enabled) {
                mQueueManager.setShuffleMode(1);
                mServiceCallback.onShuffleModeChanged(1);
            } else {
                mQueueManager.setShuffleMode(0);
                mServiceCallback.onShuffleModeChanged(0);
            }
        }
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);

        void onShuffleModeChanged(int mode);
    }
}
