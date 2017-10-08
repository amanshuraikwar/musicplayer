package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;

import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;


/**
 * Created by sonu on 2/7/17.
 */

public interface SongOnClickListener extends BaseListItemOnClickListener {
    void onSongClick(MediaBrowserCompat.MediaItem item);
    void onOptionsIbClick(MediaBrowserCompat.MediaItem item, View optionsIb);
}
