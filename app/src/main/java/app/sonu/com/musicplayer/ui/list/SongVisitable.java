package app.sonu.com.musicplayer.ui.list;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public class SongVisitable extends BaseVisitable<SongOnClickListener, MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;

    public SongVisitable(MediaBrowserCompat.MediaItem item) {
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
