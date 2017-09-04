package app.sonu.com.musicplayer.ui.main;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 29/6/17.
 */

public interface MainMvpPresenter extends BaseMvpPresenter<MainMvpView> {
    void onSlidingUpPanelSlide(float slideOffset);
    void onSearchQueryTextChange(String searchText);
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onSongSearchResultClick(MediaBrowserCompat.MediaItem item);
    void onAlbumSearchResultClick(MediaBrowserCompat.MediaItem item);
    void onArtistSearchResultClick(MediaBrowserCompat.MediaItem item);
    void onTabClickOnSamePage(int position);
}
