package app.sonu.com.musicplayer.ui.albums;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 30/7/17.
 */

public interface AlbumsMvpPresenter extends BaseMvpPresenter<AlbumsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView);
}
