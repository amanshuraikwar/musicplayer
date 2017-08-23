package app.sonu.com.musicplayer.ui.musicplayer;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 4/7/17.
 */

public interface MusicPlayerMvpView extends BaseMvpView {
    void displaySong(String songTitle,
                     String songSubtitle,
                     String songDuration,
                     String albumArtPath);
    void showPlayIcon();
    void showPauseIcon();
    void setSeekBarPosition(int position);
    void setElapsedTime(int position);
    void updateDuration(long dur);
    void scheduleSeekbarUpdate();
    void stopSeekbarUpdate();
    void setShuffleModeEnabled();
    void setShuffleModeDisabled();
    void setRepeatModeNone();
    void setRepeatModeAll();
    void setRepeatModeOne();
    void displayQueue(List<MediaSessionCompat.QueueItem> queue);
    void resetSeekbar();
    void updateQueueIndex(int index);
}
