package app.sonu.com.musicplayer.list.visitable;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;

/**
 * Created by sonu on 2/7/17.
 */

public class AlbumArtVisitable extends BaseVisitable<BaseListItemOnClickListener, MediaListTypeFactory> {

    private MediaSessionCompat.QueueItem item;

    public AlbumArtVisitable(MediaSessionCompat.QueueItem item) {
        this.item = item;
    }

    public MediaSessionCompat.QueueItem getQueueItem() {
        return item;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
