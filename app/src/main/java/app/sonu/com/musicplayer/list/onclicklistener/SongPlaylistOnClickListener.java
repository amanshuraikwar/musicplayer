package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;

/**
 * Created by sonu on 24/9/17.
 */

public interface SongPlaylistOnClickListener extends BaseListItemOnClickListener {
    void onPlaylistClick(MediaBrowserCompat.MediaItem playlist);
}
