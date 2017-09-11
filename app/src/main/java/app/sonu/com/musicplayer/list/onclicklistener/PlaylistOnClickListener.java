package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;

/**
 * Created by sonu on 6/9/17.
 */

public interface PlaylistOnClickListener extends BaseListItemOnClickListener {
    void onPlaylistClick(MediaBrowserCompat.MediaItem item, View animatingView);
}
