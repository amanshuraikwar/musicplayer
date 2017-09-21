package app.sonu.com.musicplayer.ui.search;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 21/9/17.
 */

public interface SearchMvpPresenter extends BaseMvpPresenter<SearchMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView);
    void onArtistClicked(MediaBrowserCompat.MediaItem item, View animatingView);
    void onSearchQueryTextChange(String searchString);
    void onBackIbClick();
}
