package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;

/**
 * Created by sonu on 30/7/17.
 */

public interface ArtistOnClickListener extends BaseListItemOnClickListener {
    void onArtistClick(MediaBrowserCompat.MediaItem item, View animatingView);
    void onArtistAlbumClick(MediaBrowserCompat.MediaItem item);
}
