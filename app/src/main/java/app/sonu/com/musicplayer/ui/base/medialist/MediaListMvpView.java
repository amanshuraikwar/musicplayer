package app.sonu.com.musicplayer.ui.base.medialist;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 4/10/17.
 */

public interface MediaListMvpView extends BaseMvpView {
    void displayMediaList(List<MediaBrowserCompat.MediaItem> itemList);
    void displayToast(String message);
}
