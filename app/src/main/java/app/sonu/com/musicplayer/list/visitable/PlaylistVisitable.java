package app.sonu.com.musicplayer.list.visitable;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.onclicklistener.PlaylistOnClickListener;

/**
 * Created by sonu on 30/7/17.
 */

public class PlaylistVisitable extends BaseVisitable<PlaylistOnClickListener, MediaListTypeFactory> {

    private MediaBrowserCompat.MediaItem item;
    private int playlistType;

    public PlaylistVisitable(MediaBrowserCompat.MediaItem item, int playlistType) {
        this.item = item;
        this.playlistType = playlistType;
    }

    public MediaBrowserCompat.MediaItem getMediaItem() {
        return item;
    }

    public int getPlaylistType() {
        return playlistType;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
