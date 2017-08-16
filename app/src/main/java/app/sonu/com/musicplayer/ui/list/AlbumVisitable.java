package app.sonu.com.musicplayer.ui.list;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.data.db.model.Album;

/**
 * Created by sonu on 30/7/17.
 */

public class AlbumVisitable extends BaseVisitable<AlbumOnClickListener, MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;

    public AlbumVisitable(MediaBrowserCompat.MediaItem item) {
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
