package app.sonu.com.musicplayer.mediaplayernew.manager;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;
import app.sonu.com.musicplayer.mediaplayernew.playback.Playback;
import app.sonu.com.musicplayer.mediaplayernew.playlistssource.PlaylistsSource;
import app.sonu.com.musicplayer.util.QueueHelper;

/**
 * Created by sonu on 28/7/17.
 * this class handles actual playback of the music according to queue
 * it also handles shuffle and repeat actions on playing queue
 * @author amanshu
 */
public class PlaybackManager implements Playback.Callback {

    private static final String TAG = PlaybackManager.class.getSimpleName();

    private MusicProvider mMusicProvider;
    private QueueManager mQueueManager;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    private PlaylistsManager mPlaylistsManager;

    private int mShuffleMode, mRepeatMode;

    public PlaybackManager(PlaybackServiceCallback serviceCallback,
                           MusicProvider musicProvider,
                           QueueManager queueManager,
                           Playback playback,
                           PlaylistsManager playlistsManager) {
        mMusicProvider = musicProvider;
        mServiceCallback = serviceCallback;
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mPlayback.setCallback(this);
        mPlaylistsManager = playlistsManager;

        mShuffleMode = 0;
        mRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
    }

    @SuppressWarnings("WeakerAccess")
    public void handlePlayRequest(){
        Log.d(TAG, "handlePlayRequest:called");
        Log.i(TAG, "handlePlayRequest:mState=" + Playback.states[mPlayback.getState()]);

        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            if (mPlayback.play(currentMusic)){
                // tell service that playback started
                mServiceCallback.onPlaybackStart();
            }
        }
    }

    public void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest:called");
        Log.i(TAG, "handlePauseRequest:mState=" + Playback.states[mPlayback.getState()]);
        if (mPlayback.pause()) {
            mServiceCallback.onPlaybackStop();
        }
    }

    public void handleStopRequest() {
        Log.d(TAG, "handleStopRequest:");
        Log.i(TAG, "handleStopRequest:mState=" + Playback.states[mPlayback.getState()]);

        if (mPlayback.stop()) {
            mServiceCallback.onPlaybackStop();
        }
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * Update the current media player state, optionally showing an error message.
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

        // setting appropriate state
        if (state == Playback.CUSTOM_PLAYBACK_STATE_PAUSED) {
            state = PlaybackStateCompat.STATE_PAUSED;
        } else if (state == Playback.CUSTOM_PLAYBACK_STATE_PLAYING) {
            state = PlaybackStateCompat.STATE_PLAYING;
        } else if (state == Playback.CUSTOM_PLAYBACK_STATE_STOPPED) {
            state = PlaybackStateCompat.STATE_STOPPED;
        } else {
            state = PlaybackStateCompat.STATE_NONE;
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

        Bundle b = new Bundle();
        b.putInt(Playback.PLAYBACK_STATE_EXTRA_CURRENT_QUEUE_INDEX,
                mQueueManager.getmCurrentQueueIndex());

        // Set the activeQueueItemId if the current index is valid.
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());

            // todo temp
            Log.w(TAG, "updatePlaybackState:"+QueueHelper.getMusicIdOf(currentMusic));
            Log.w(TAG, "updatePlaybackState:"+mPlaylistsManager.isSongInFavourites(QueueHelper.getMediaIdOf(currentMusic)));

            b.putBoolean(PlaylistsSource.PLAYBACK_STATE_EXTRA_IS_IN_FAVORITES,
                    mPlaylistsManager.isSongInFavourites(QueueHelper.getMusicIdOf(currentMusic)));
        }

        stateBuilder.setExtras(b);

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        // tell service that notification is required
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mServiceCallback.onNotificationRequired();
        }
    }

    public boolean isPlaying() {
        return mPlayback.isPlaying();
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        Playback.CUSTOM_ACTION_ADD_TO_PLAYLIST ;
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
        // handle according to repeat mode
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
        Log.d(TAG, "onPlaybackStatusChanged:called");
        Log.i(TAG, "onPlaybackStatusChanged:state="+Playback.states[state]);

        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        //todo understand
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            if(mPlayback.play()) {
                mServiceCallback.onPlaybackStart();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "onPlayFromMediaId:called");
            Log.d(TAG, "onPlayFromMediaId:mediaId="+mediaId);
            Log.d(TAG, "onPlayFromMediaId:bundle="+extras);

            mQueueManager.setQueueFromMediaId(mediaId);
            if (mShuffleMode==1) {
                mQueueManager.shuffleMusic(mQueueManager.getCurrentMusicMediaId());
            }
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long id) {
            Log.d(TAG, "onSkipToQueueItem:called");
            Log.i(TAG, "onSkipToQueueItem: id="+id);
            mQueueManager.setCurrentQueueItem(id);
            handlePlayRequest();
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
            handleStopRequest();
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
                        Log.w(TAG, "onSetShuffleModeEnabled:cur="+mQueueManager.getCurrentMusicMediaId());
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

    // to tell service about various stuff
    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);

        void onShuffleModeChanged(int mode);

        void onRepeatModeChanged(int mode);
    }
}


