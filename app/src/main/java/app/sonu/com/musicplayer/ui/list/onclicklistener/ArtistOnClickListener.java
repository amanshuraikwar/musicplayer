package app.sonu.com.musicplayer.ui.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;

/**
 * Created by sonu on 30/7/17.
 */

public interface ArtistOnClickListener extends BaseListItemOnClickListener {
    void onArtistClick(MediaBrowserCompat.MediaItem item);
}
