package app.sonu.com.musicplayer.ui.main;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 15/9/17.
 */

public interface MainMvpView extends BaseMvpView {
    void setNavigationItemSelected(int position);
    void showNavigationBar();
    void hideNavigationBar();

    void startAlbumActivity(MediaBrowserCompat.MediaItem item, View animatingView);
    void startArtistActivity(MediaBrowserCompat.MediaItem item, View animatingView);
    void startPlaylistActivity(MediaBrowserCompat.MediaItem item, View animatingView);

    void startIntoActivity();
    void close();
}
