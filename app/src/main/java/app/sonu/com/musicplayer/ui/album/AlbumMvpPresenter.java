package app.sonu.com.musicplayer.ui.album;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 21/8/17.
 */

public interface AlbumMvpPresenter extends BaseMvpPresenter<AlbumMvpView> {
    void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item  );
    void onCreateView();
    void onDestroy();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
}
