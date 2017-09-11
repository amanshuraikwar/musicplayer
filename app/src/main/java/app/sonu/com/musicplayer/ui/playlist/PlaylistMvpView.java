package app.sonu.com.musicplayer.ui.playlist;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 8/9/17.
 */

public interface PlaylistMvpView extends BaseMvpView {
    void closeFragment();
    void displayListData(final MediaBrowserCompat.MediaItem item,
                             String artPath,
                             final List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
}
