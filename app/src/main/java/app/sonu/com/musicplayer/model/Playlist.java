package app.sonu.com.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonu on 9/9/17.
 */

public class Playlist {
    private String playlistId;
    private String title;
    private Type type;
    private long timeCreated;
    private List<String> songIdList;
    private int iconDrawableId;
    private int color;

    public static enum Type {
        AUTO, USER;
    }

    public Playlist(String playlistId,
                    String title,
                    Type type,
                    List<String> songIdList,
                    int iconDrawableId,
                    int color) {
        this.playlistId = playlistId;
        this.title = title;
        this.type = type;
        timeCreated = System.currentTimeMillis();
        this.songIdList = songIdList;
        this.iconDrawableId = iconDrawableId;
        this.color = color;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public List<String> getSongIdList() {
        return this.songIdList;
    }

    public Type getType() {
        return type;
    }

    public int getIconDrawableId() {
        return iconDrawableId;
    }

    public void setIconDrawableId(int mIconDrawableId) {
        this.iconDrawableId = mIconDrawableId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int mColor) {
        this.color = mColor;
    }

    public void addSongId(String songId) {
        songIdList.add(songId);
    }

    public void removeSongId(String songId) {
        if (songIdList.contains(songId)) {
            songIdList.remove(songIdList.lastIndexOf(songId));
        }
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId='" + playlistId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", timeCreated=" + timeCreated +
                ", songIdList=" + songIdList +
                ", iconDrawableId=" + iconDrawableId +
                ", color=" + color +
                '}';
    }
}
