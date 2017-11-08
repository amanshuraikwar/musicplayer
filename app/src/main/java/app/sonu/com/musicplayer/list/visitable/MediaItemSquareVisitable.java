package app.sonu.com.musicplayer.list.visitable;

import android.support.v4.media.MediaBrowserCompat;
import android.view.View;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.AlbumOnClickListener;
import app.sonu.com.musicplayer.list.onclicklistener.MediaItemSquareOnClickListener;

/**
 * Created by sonu on 30/7/17.
 */

public class MediaItemSquareVisitable extends BaseVisitable<MediaItemSquareOnClickListener,
        MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;
    private int itemIdentifierDrawable = R.drawable.ic_music_note_grey_24dp;

    public MediaItemSquareVisitable(MediaBrowserCompat.MediaItem item) {
        this.item = item;
    }

    public MediaItemSquareVisitable(MediaBrowserCompat.MediaItem item, int itemIdentifierDrawable) {
        this.item = item;
        this.itemIdentifierDrawable = itemIdentifierDrawable;
    }

    public MediaBrowserCompat.MediaItem getMediaItem() {
        return item;
    }

    public int getItemIdentifierDrawable() {
        return itemIdentifierDrawable;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
