package app.sonu.com.musicplayer.mediaplayernew.playback;

import android.media.session.MediaSession;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * Created by sonu on 27/7/17.
 */

public interface Playback {
    void start();
    void stop();
    void pause();
    boolean isPlaying();
    void play();
    void play(QueueItem item);
    int getState();
    void setCallback(Callback callback);
    long getCurrentPosition();
    void seekTo(long position);

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
