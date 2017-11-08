package app.sonu.com.musicplayer.ui.adapter;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 8/11/17.
 */

public interface BaseMediaListMvpView extends BaseMvpView {
    void displayMediaItems(List<MediaBrowserCompat.MediaItem> itemList);
    String getMediaId();
}
