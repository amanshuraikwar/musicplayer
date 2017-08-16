package app.sonu.com.musicplayer.ui.list;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public interface SongOnClickListener extends BaseListItemOnClickListener {
    void onSongClick(MediaBrowserCompat.MediaItem item);
}
