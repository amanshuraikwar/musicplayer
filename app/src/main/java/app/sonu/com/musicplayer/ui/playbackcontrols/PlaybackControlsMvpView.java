package app.sonu.com.musicplayer.ui.playbackcontrols;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 5/10/17.
 */

public interface PlaybackControlsMvpView extends BaseMvpView {
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
    void resetSeekbar();
    void displayToast(String message);
    void showAddToPlaylistsDialog(String mediaId);
}
