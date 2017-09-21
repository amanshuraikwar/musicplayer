package app.sonu.com.musicplayer.ui.allsongs;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;


import app.sonu.com.musicplayer.ui.base.BaseMvpView;


/**
 * Created by sonu on 2/7/17.
 */

public interface AllSongsMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
    void scrollListToTop();
}
