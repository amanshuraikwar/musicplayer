package app.sonu.com.musicplayer.ui.artists;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 1/8/17.
 */

public interface ArtistsMvpView extends BaseMvpView {
    void displayList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
    void scrollListToTop();
}
