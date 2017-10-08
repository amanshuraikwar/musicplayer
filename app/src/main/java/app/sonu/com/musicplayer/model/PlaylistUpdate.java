package app.sonu.com.musicplayer.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonu on 1/10/17.
 */

public class PlaylistUpdate {

    private Playlist playlist;
    private String mediaId;
    private List<MediaMetadataCompat> addedSongList;
    private List<MediaMetadataCompat> removedSongList;
    private int updatedIconDrawableId;
    private int updatedColor;

    public PlaylistUpdate(Playlist playlist) {
        this.playlist = playlist;
        this.addedSongList = new ArrayList<>();
        this.removedSongList = new ArrayList<>();
        this.updatedIconDrawableId = 0;
        this.updatedColor = 0;
    }

    public String getPlaylistId() {
        return playlist.getPlaylistId();
    }

    public boolean areSongsAdded() {
        return addedSongList.size() != 0;
    }

    public boolean areSongRemoved() {
        return removedSongList.size() != 0;
    }

    public boolean isIconDrawableIdUpdated() {
        return updatedIconDrawableId != 0;
    }

    public boolean iscolorUpdated() {
        return updatedColor != 0;
    }

    public String getMediaId() {
        return mediaId;
    }

    public List<MediaMetadataCompat> getAddedSongList() {
        return addedSongList;
    }

    public List<MediaMetadataCompat> getRemovedSongList() {
        return removedSongList;
    }

    public int getUpdatedIconDrawableId() {
        return updatedIconDrawableId;
    }

    public int getUpdatedColor() {
        return updatedColor;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public void addSong(MediaMetadataCompat metadata) {
        addedSongList.add(metadata);
    }

    public void addRemovedSong(MediaMetadataCompat metadata) {
        removedSongList.add(metadata);
    }

    public void addUpdatedIconDrawableId(int iconDrawableId) {
        updatedIconDrawableId = iconDrawableId;
    }

    public void addUpdatedColor(int color) {
        updatedColor = color;
    }
}
