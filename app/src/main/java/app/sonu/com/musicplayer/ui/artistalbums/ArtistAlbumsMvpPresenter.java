package app.sonu.com.musicplayer.ui.artistalbums;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.ui.base.medialist.MediaListMvpPresenter;

/**
 * Created by sonu on 4/10/17.
 */

public interface ArtistAlbumsMvpPresenter extends MediaListMvpPresenter<ArtistAlbumsMvpView> {
    void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView);
}
