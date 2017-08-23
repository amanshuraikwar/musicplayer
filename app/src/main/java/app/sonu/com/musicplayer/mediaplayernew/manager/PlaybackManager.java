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

    private int mShuffleMode, mRepeatMode;

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

        mShuffleMode = 0;
        mRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
    }

    public void handlePlayRequest(){
        Log.d(TAG, "handlePlayRequest: mState=" + mPlayback.getState());
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {

            if (mPlayback.play(currentMusic)){
                mServiceCallback.onPlaybackStart();
            }
        }
    }

    public void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + mPlayback.getState());
        if (mPlayback.pause()) {
            mServiceCallback.onPlaybackStop();
        }
    }

    public void handleStopRequest(String withError) {
        Log.d(TAG, "handleStopRequest: mState=" + mPlayback.getState());
        if (withError != null) {
            Log.e(TAG, "handleStopRequest:error="+withError);
        }

        if (mPlayback.stop()) {
            mServiceCallback.onPlaybackStop();
            updatePlaybackState(withError);
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
        Log.d(TAG, "updatePlaybackState:called");
        Log.i(TAG, "updatePlaybackState:playbackState=" + mPlayback.getState());

        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null) {
            position = mPlayback.getCurrentPosition();
        }

        Log.i(TAG, "updatePlaybackState:position="+position);

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        int state = mPlayback.getState();

        if (state == Playback.CUSTOM_PLAYBACK_STATE_PAUSED) {
            state = PlaybackStateCompat.STATE_PAUSED;
        } else if (state == Playback.CUSTOM_PLAYBACK_STATE_PLAYING) {
            state = PlaybackStateCompat.STATE_PLAYING;
        } else if (state == Playback.CUSTOM_PLAYBACK_STATE_STOPPED) {
            state = PlaybackStateCompat.STATE_STOPPED;
        }

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
        switch (mRepeatMode) {
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                handlePlayRequest();
                break;
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                if (mQueueManager.isLastItemPlaying()) {
                    updatePlaybackState(null);
                } else {
                    mQueueManager.skipQueuePosition(1);
                    handlePlayRequest();
                }
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                mQueueManager.skipQueuePosition(1);
                handlePlayRequest();
                break;
        }
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
            if (!mPlayback.play()) {
                handlePlayRequest();
            }
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
            //todo implement
            super.onSkipToQueueItem(id);
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onSkipToNext() {
            if (mQueueManager.isLastItemPlaying()) {
                if (mRepeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                    mQueueManager.skipQueuePosition(1);
                }
            } else {
                mQueueManager.skipQueuePosition(1);
            }
            handlePlayRequest();
        }

        @Override
        public void onSkipToPrevious() {
            mQueueManager.skipQueuePosition(-1);
            handlePlayRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo((int) pos);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            Log.d(TAG, "onSetRepeatMode:called");
            Log.i(TAG, "onSetRepeatMode:repeatMode="+repeatMode);
            if (setRepeatMode(repeatMode)) {
                mServiceCallback.onRepeatModeChanged(repeatMode);
            }
        }

        //todo deprecated in 26.0.0-beta-2
        @Override
        public void onSetShuffleModeEnabled(boolean enabled) {
            Log.d(TAG, "onSetShuffleModeEnabled:called");
            Log.d(TAG, "onSetShuffleModeEnabled:enabled="+enabled);
            int mode = 0;
            if (enabled) {
                mode = 1;
            }

            if (setShuffleMode(mode)) {
                if (mQueueManager.getCurrentMusic() != null) {
                    if (enabled) {
                        mQueueManager.shuffleMusic(mQueueManager.getCurrentMusicMediaId());
                    } else {
                        mQueueManager.setQueueFromMediaId(
                                mQueueManager.getCurrentMusicMediaId(), true);
                    }
                }
                mServiceCallback.onShuffleModeChanged(mode);
            }
        }
    }

    private boolean setShuffleMode(int mode) {
        if (mShuffleMode != mode) {
            mShuffleMode = mode;
            return true;
        }

        return false;
    }

    private boolean setRepeatMode(int repeatMode) {
        if (mRepeatMode != repeatMode) {
            mRepeatMode = repeatMode;
            return true;
        }
        return false;
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);

        void onShuffleModeChanged(int mode);

        void onRepeatModeChanged(int mode);
    }
}


