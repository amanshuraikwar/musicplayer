package app.sonu.com.musicplayer.ui.playlists;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 5/9/17.
 */

public interface PlaylistsMvpPresenter extends BaseMvpPresenter<PlaylistsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onPlaylistClicked(MediaBrowserCompat.MediaItem item, View animatingView);
    void onRefresh();
}
