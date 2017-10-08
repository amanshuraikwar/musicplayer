package app.sonu.com.musicplayer.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import app.sonu.com.musicplayer.model.Playlist;

/**
 * Created by sonu on 26/9/17.
 */

public class MediaMetadataHelper {

    public static final String CUSTOM_METADATA_KEY_SOURCE = "__SOURCE__";
    public static final String CUSTOM_METADATA_KEY_DATE_MODIFIED = "__DATA_ADDED__";
    public static final String CUSTOM_METADATA_KEY_DISPLAY_NAME = "__DISPLAY_NAME__";
    public static final String CUSTOM_METADATA_KEY_SIZE = "__SIZE__";
    public static final String CUSTOM_METADATA_KEY_ALBUM_ID = "__ALBUM_ID__";
    public static final String CUSTOM_METADATA_KEY_ALBUM_KEY = "__ALBUM_KEY__";
    public static final String CUSTOM_METADATA_KEY_ARTIST_ID = "__ARTIST_ID__";
    public static final String CUSTOM_METADATA_KEY_ARTIST_KEY = "__ARTIST_KEY__";
    public static final String CUSTOM_METADATA_KEY_DURATION = "__DURATION__";
    public static final String CUSTOM_METADATA_KEY_FIRST_YEAR = "__FIRST_YEAR__";
    public static final String CUSTOM_METADATA_KEY_LAST_YEAR = "__LAST_YEAR__";
    public static final String CUSTOM_METADATA_KEY_NUM_ALBUMS = "__NUM_ALBUMS__";
    public static final String CUSTOM_METADATA_KEY_NUM_TRACKS = "__NUM_TRACKS__";

    public static final String CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID = "__PLAYLIST_ICON_DRAWABLE_ID__";
    public static final String CUSTOM_METADATA_KEY_PLAYLIST_COLOR = "__PLAYLIST_COLOR__";
    public static final String CUSTOM_METADATA_KEY_PLAYLIST_TYPE = "__PLAYLIST_TYPE__";
    public static final String CUSTOM_METADATA_KEY_IS_SONG_IN_PLAYLIST = "__IS_SONG_IN_PLAYLIST";

    public static MediaMetadataCompat buildSongMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(CUSTOM_METADATA_KEY_SOURCE, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, cursor.getString(2))
                .putString(CUSTOM_METADATA_KEY_DATE_MODIFIED,
                        cursor.getString(3))
                .putString(CUSTOM_METADATA_KEY_DISPLAY_NAME,
                        cursor.getString(4))
                .putString(CUSTOM_METADATA_KEY_SIZE, cursor.getString(5))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(6))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(7))
                .putString(CUSTOM_METADATA_KEY_ALBUM_ID, cursor.getString(8))
                .putString(CUSTOM_METADATA_KEY_ALBUM_KEY, cursor.getString(9))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(10))
                .putString(CUSTOM_METADATA_KEY_ARTIST_ID, cursor.getString(11))
                .putString(CUSTOM_METADATA_KEY_ARTIST_KEY, cursor.getString(12))
                .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, cursor.getString(13))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                        Long.parseLong(cursor.getString(14)))
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                        Long.parseLong(cursor.getString(15)))
                //.putLong(MediaMetadataCompat.METADATA_KEY_YEAR,
                //        Long.parseLong(cursor.getString(16).trim()))
                .build();
    }

    public static MediaMetadataCompat buildAlbumMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cursor.getString(2))
                .putString(CUSTOM_METADATA_KEY_ALBUM_KEY, cursor.getString(3))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, cursor.getString(4))
                .putString(CUSTOM_METADATA_KEY_FIRST_YEAR, cursor.getString(5))
                .putString(CUSTOM_METADATA_KEY_LAST_YEAR, cursor.getString(6))
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                        Long.parseLong(cursor.getString(7)))
                //.putLong(MediaMetadataCompat.METADATA_KEY_YEAR,
                //        Integer.parseInt(cursor.getString(6)))
                .build();
    }

    public static MediaMetadataCompat buildArtistMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(1))
                .putString(CUSTOM_METADATA_KEY_ARTIST_KEY, cursor.getString(2))
                .putLong(CUSTOM_METADATA_KEY_NUM_ALBUMS,
                        Integer.parseInt(cursor.getString(3)))
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                        Integer.parseInt(cursor.getString(4)))
                .build();
    }

    public static String getSongDisplayTitle(MediaMetadataCompat metadata) {
        // title
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getSongDisplaySubtitle(MediaMetadataCompat metadata) {
        // artist
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
    }

    public static String getArtistSongDisplayTitle(MediaMetadataCompat metadata) {
        // title
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getArtistSongDisplaySubtitle(MediaMetadataCompat metadata) {
        // artist
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
    }

    public static String getAlbumSongDisplayTitle(MediaMetadataCompat metadata) {
        // title
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getAlbumSongDisplaySubtitle(MediaMetadataCompat metadata) {
        // artist
        return String.valueOf(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    public static String getAlbumDisplayTitle(MediaMetadataCompat metadata) {
        // album
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getAlbumDisplaySubtitle(MediaMetadataCompat metadata) {
        // artist . last_year
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
                + " . "
                + metadata.getString(CUSTOM_METADATA_KEY_LAST_YEAR);
    }

    public static String getArtistDisplayTitle(MediaMetadataCompat metadata) {
        // album
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getArtistDisplaySubtitle(MediaMetadataCompat metadata) {
        // num_tracks songs
        return metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
                + " songs";
    }

    public static String getPlaylistDisplayTitle(MediaMetadataCompat metadata) {
        // album
        return metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public static String getPlaylistDisplaySubtitle(MediaMetadataCompat metadata) {
        // num_tracks songs
        return metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
                + " songs";
    }

    public static MediaMetadataCompat buildPlaylistMetadata(String playlistId,
                                                            String playlistTitle,
                                                            int noOfSongs,
                                                            int iconDrawableId,
                                                            int playlistColor,
                                                            Playlist.Type playlistType) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playlistId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playlistTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, noOfSongs)
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                        iconDrawableId)
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_COLOR, playlistColor)
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_TYPE, playlistType.hashCode())
                .build();

    }

    public static MediaMetadataCompat buildPlaylistMetadata(Playlist playlist) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playlist.getPlaylistId())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playlist.getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                        playlist.getSongIdList().size())
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                        playlist.getIconDrawableId())
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_COLOR, playlist.getColor())
                .putLong(CUSTOM_METADATA_KEY_PLAYLIST_TYPE,
                        playlist.getType().hashCode())
                .build();

    }

    public static MediaMetadataCompat addAlbumArtBitmap(MediaMetadataCompat metadata, Bitmap bitmap) {
        if (bitmap != null) {
            metadata = new MediaMetadataCompat.Builder(metadata)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap).build();
        }

        return metadata;
    }
}
