package app.sonu.com.musicplayer.ui.list.visitable;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.onclicklistener.AlbumSearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongSearchResultOnClickListener;

/**
 * Created by sonu on 2/7/17.
 */

public class AlbumSearchResultVisitable extends BaseVisitable<AlbumSearchResultOnClickListener,
        MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;

    public AlbumSearchResultVisitable(MediaBrowserCompat.MediaItem item) {
        this.item = item;
    }

    public MediaBrowserCompat.MediaItem getMediaItem() {
        return item;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
