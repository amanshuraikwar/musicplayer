package app.sonu.com.musicplayer.ui.artists;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 1/8/17.
 */

public interface ArtistsMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void startLoading();
    void stopLoading();
    void displayToast(String message);
    void scrollListToTop();
}
