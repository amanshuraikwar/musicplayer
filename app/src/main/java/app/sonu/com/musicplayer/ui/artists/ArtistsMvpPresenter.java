package app.sonu.com.musicplayer.ui.artists;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 1/8/17.
 */

public interface ArtistsMvpPresenter extends BaseMvpPresenter<ArtistsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onArtistClicked(MediaBrowserCompat.MediaItem item, View animatingView);
}
