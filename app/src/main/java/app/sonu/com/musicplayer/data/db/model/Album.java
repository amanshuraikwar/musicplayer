package app.sonu.com.musicplayer.data.db.model;

import android.support.v4.media.MediaBrowserCompat;

/**
 * Created by sonu on 30/7/17.
 */

public class Album {
    private String mediaId;
    private String title;
    private String artist;

    public Album(MediaBrowserCompat.MediaItem item) {
        this.mediaId = item.getMediaId();
        this.title = item.getDescription().getTitle().toString();
        this.artist = item.getDescription().getSubtitle().toString();
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
