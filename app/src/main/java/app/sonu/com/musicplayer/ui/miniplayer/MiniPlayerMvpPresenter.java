package app.sonu.com.musicplayer.ui.miniplayer;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 9/8/17.
 */

public interface MiniPlayerMvpPresenter extends BaseMvpPresenter<MiniPlayerMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onDestroy();
    void playPauseButtonOnClick();
    void updateProgress();
    void onNavUpClick();
}
