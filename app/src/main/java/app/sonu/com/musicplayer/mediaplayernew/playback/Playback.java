package app.sonu.com.musicplayer.mediaplayernew.playback;

import android.media.session.MediaSession;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * Created by sonu on 27/7/17.
 */

public interface Playback {

    int CUSTOM_PLAYBACK_STATE_NONE = 0;
    int CUSTOM_PLAYBACK_STATE_IDLE = 1;
    int CUSTOM_PLAYBACK_STATE_STOPPED = 2;
    int CUSTOM_PLAYBACK_STATE_PREPARING = 3;
    int CUSTOM_PLAYBACK_STATE_PAUSED = 4;
    int CUSTOM_PLAYBACK_STATE_PLAYING = 5;

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

