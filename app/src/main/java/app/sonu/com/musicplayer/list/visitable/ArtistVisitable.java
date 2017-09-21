package app.sonu.com.musicplayer.list.visitable;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.onclicklistener.ArtistOnClickListener;

/**
 * Created by sonu on 30/7/17.
 */

public class ArtistVisitable extends BaseVisitable<ArtistOnClickListener, MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;

    public ArtistVisitable(MediaBrowserCompat.MediaItem item) {
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
