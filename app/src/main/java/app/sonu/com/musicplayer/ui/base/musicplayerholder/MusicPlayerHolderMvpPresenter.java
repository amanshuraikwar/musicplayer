package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.support.v4.app.FragmentActivity;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 14/9/17.
 */

public interface MusicPlayerHolderMvpPresenter<MvpView extends MusicPlayerHolderMvpView>
        extends BaseMvpPresenter<MvpView> {
    void onCreate(FragmentActivity activity);
    void onMusicPlayerPanelStateChanged(SlidingUpPanelLayout.PanelState state);
}
