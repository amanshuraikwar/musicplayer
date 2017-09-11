package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 4/7/17.
 */

public interface MusicPlayerMvpPresenter extends BaseMvpPresenter<MusicPlayerMvpView> {
    void playPauseButtonOnClick();
    void skipNextButtonOnClick();
    void skipPreviousButtonOnClick();
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void updateProgress();
    void onSeekbarStopTrackingTouch(int progress);
    void onCollapseIvClick();
    void onShuffleButtonClick();
    void onRepeatButtonClick();
    void onQueueItemClick(MediaSessionCompat.QueueItem item);
    void onHeartIvClick();
}
