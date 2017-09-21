package app.sonu.com.musicplayer.ui.miniplayer;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 9/8/17.
 */

public interface MiniPlayerMvpView extends BaseMvpView {
    void displaySong(String title, String albumArtPath);
    void showPauseIcon();
    void showPlayIcon();
    void setSeekBarPosition(int position);
    void updateDuration(long dur);
    void scheduleSeekbarUpdate();
    void stopSeekbarUpdate();
    void resetSeekbar();
    void setShuffleModeEnabled();
    void setShuffleModeDisabled();
    void setRepeatModeNone();
    void setRepeatModeAll();
    void setRepeatModeOne();
    void displayToast(String message);
}
