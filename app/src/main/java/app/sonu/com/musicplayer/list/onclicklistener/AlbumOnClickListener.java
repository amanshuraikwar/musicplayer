package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;


/**
 * Created by sonu on 30/7/17.
 */

public interface AlbumOnClickListener extends BaseListItemOnClickListener {
    void onAlbumClick(MediaBrowserCompat.MediaItem item, View animatingView);
}
