package app.sonu.com.musicplayer.ui.playlist;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 8/9/17.
 */

public interface PlaylistMvpPresenter extends BaseMvpPresenter<PlaylistMvpView>{
    void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item);
    void onCreateView();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onDragDismissed();
    void onBackIbClick();
    void onShuffleAllClick();
}
