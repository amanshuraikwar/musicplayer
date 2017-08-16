package app.sonu.com.musicplayer.ui.list;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.data.db.model.Album;

/**
 * Created by sonu on 30/7/17.
 */

public interface AlbumOnClickListener extends BaseListItemOnClickListener {
    void onAlbumClick(MediaBrowserCompat.MediaItem item);
}
