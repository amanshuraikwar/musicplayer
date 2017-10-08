package app.sonu.com.musicplayer.ui.addsongstoplaylists;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 23/9/17.
 */

public interface AddSongsToPlaylistsMvpView extends BaseMvpView {
    void displayToast(String message);
    void close();
    void displayList(List<MediaBrowserCompat.MediaItem> items);
    void startCreatePlaylistFragment();
    void addPlaylistToRv(MediaBrowserCompat.MediaItem item);
    void removePlaylistFromRv(MediaBrowserCompat.MediaItem item);
    void setPlaylistChecked(String playlistMediaId);
    void setPlaylistUnchecked(String playlistMediaId);
}
