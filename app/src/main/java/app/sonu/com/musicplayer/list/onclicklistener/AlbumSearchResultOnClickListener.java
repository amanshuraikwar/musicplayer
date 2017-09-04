package app.sonu.com.musicplayer.list.onclicklistener;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;

/**
 * Created by sonu on 22/8/17.
 */

public interface AlbumSearchResultOnClickListener extends BaseListItemOnClickListener {
    void onSearchResultClick(MediaBrowserCompat.MediaItem item);
}
