package app.sonu.com.musicplayer.ui.search;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 21/9/17.
 */

public interface SearchMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
    void scrollListToTop();
    void close();
}
