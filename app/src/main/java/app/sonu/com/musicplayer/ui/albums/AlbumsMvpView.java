package app.sonu.com.musicplayer.ui.albums;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 30/7/17.
 */

public interface AlbumsMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void startLoading();
    void stopLoading();
}
