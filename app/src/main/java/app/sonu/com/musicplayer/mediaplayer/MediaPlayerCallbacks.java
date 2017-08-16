package app.sonu.com.musicplayer.mediaplayer;

/**
 * Created by sonu on 23/7/17.
 */

public interface MediaPlayerCallbacks {
    void play();
    void pause();
    void resume();
    void stop();
    void playNext();
    void playPrevious();
    boolean isPlaying();
    int getCurrentPosition();
    int getDuration();
}
