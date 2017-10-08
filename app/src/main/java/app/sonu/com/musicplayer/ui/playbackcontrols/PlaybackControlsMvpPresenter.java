package app.sonu.com.musicplayer.ui.playbackcontrols;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 5/10/17.
 */

public interface PlaybackControlsMvpPresenter extends BaseMvpPresenter<PlaybackControlsMvpView> {
    void playPauseButtonOnClick();
    void skipNextButtonOnClick();
    void skipPreviousButtonOnClick();
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void updateProgress();
    void onSeekbarStopTrackingTouch(int progress);
    void onShuffleButtonClick();
    void onRepeatButtonClick();
    void addToPlaylistIvClick();
}
