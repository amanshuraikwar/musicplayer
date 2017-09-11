package app.sonu.com.musicplayer.ui.playlists;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 5/9/17.
 */

public interface PlaylistsMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void startLoading();
    void stopLoading();
    void displayToast(String message);
    void scrollListToTop();
}
