package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;


/**
 * Created by sonu on 2/7/17.
 */

public interface SongOnClickListener extends BaseListItemOnClickListener {
    void onSongClick(MediaBrowserCompat.MediaItem item);
}
