package app.sonu.com.musicplayer.mediaplayer.playback;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * Created by sonu on 27/7/17.
 * interface representing playback of music
 * a class must implement this interface if it wants to represent a playback
 * @author amanshu
 */

public interface Playback {

    // playback states
    int CUSTOM_PLAYBACK_STATE_NONE = 0;
    int CUSTOM_PLAYBACK_STATE_IDLE = 1;
    int CUSTOM_PLAYBACK_STATE_STOPPED = 2;
    int CUSTOM_PLAYBACK_STATE_PREPARING = 3;
    int CUSTOM_PLAYBACK_STATE_PAUSED = 4;
    int CUSTOM_PLAYBACK_STATE_PLAYING = 5;

    long CUSTOM_ACTION_ADD_TO_PLAYLIST = 1 << 20;

    String PLAYBACK_STATE_EXTRA_CURRENT_QUEUE_INDEX = "currentQueueIndex";

    String states[] = {"none", "idle", "stopped", "preparing", "paused", "playing"};

    // getter methods
    boolean isPlaying();
    boolean isStopped();
    boolean isPaused();
    boolean isIdle();
    boolean isPreparing();
    int getState();
    int getCurrentPosition();


    void start();
    boolean play(@NonNull QueueItem item);
    boolean play();
    boolean pause();
    boolean stop();
    void seekTo(int position);

    void setCallback(Callback callback);

    // to tell playback manager about various stuff
    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();

        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void setCurrentMediaId(String mediaId);
    }
}

