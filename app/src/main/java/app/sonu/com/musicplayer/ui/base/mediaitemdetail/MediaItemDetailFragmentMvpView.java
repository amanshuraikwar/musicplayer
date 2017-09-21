package app.sonu.com.musicplayer.ui.base.mediaitemdetail;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 21/9/17.
 */

public interface MediaItemDetailFragmentMvpView extends BaseMvpView {
    void displayMetadata(String title, String subtitle, String artPath);
    void displayMediaList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
    void close();
}
