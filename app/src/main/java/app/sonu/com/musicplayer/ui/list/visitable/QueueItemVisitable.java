package app.sonu.com.musicplayer.ui.list.visitable;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.onclicklistener.QueueItemOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongOnClickListener;

/**
 * Created by sonu on 2/7/17.
 */

public class QueueItemVisitable extends BaseVisitable<QueueItemOnClickListener, MediaListTypeFactory> {

    private MediaSessionCompat.QueueItem item;
    private int indexToDisplay;

    public QueueItemVisitable(MediaSessionCompat.QueueItem item) {
        this.item = item;
    }

    public MediaSessionCompat.QueueItem getMediaItem() {
        return item;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }

    public int getIndexToDisplay() {
        return indexToDisplay;
    }

    public void setIndexToDisplay(int indexToDisplay) {
        this.indexToDisplay = indexToDisplay;
    }
}
