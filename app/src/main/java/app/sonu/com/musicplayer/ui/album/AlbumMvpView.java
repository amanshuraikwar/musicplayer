package app.sonu.com.musicplayer.ui.album;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 21/8/17.
 */

public interface AlbumMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayAlbumData(String title, String subtitle, String artPath);
    void displayToast(String message);
}
