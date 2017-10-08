package app.sonu.com.musicplayer.ui.musicplayer;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 4/7/17.
 */

public interface MusicPlayerMvpPresenter extends BaseMvpPresenter<MusicPlayerMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onHeartIvClick();
    void setAntiDragView(View view);
    void setSupl(SlidingUpPanelLayout supl);
    void onCollapseIvClick();
}
