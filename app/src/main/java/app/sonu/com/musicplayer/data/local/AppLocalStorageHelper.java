package app.sonu.com.musicplayer.data.local;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.di.ApplicationContext;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;

/**
 * Created by sonu on 30/6/17.
 */

public class AppLocalStorageHelper implements LocalStorageHelper {

    private Context applicationContext;

    public AppLocalStorageHelper(@ApplicationContext Context context) {
        this.applicationContext = context;
    }

    @Override
    public List<MediaMetadataCompat> getSongListFromLocalStorage() {
        List<MediaMetadataCompat> songList = new ArrayList<>();
        Cursor songsCursor = getSongsCursor();

        if (songsCursor.moveToFirst()) {
            do {
                songList.add(MediaMetadataHelper.buildSongMetadata(songsCursor));
            } while (songsCursor.moveToNext());
        }

        return songList;
    }

    @Override
    public List<MediaMetadataCompat> getAlbumListFromLocalStorage() {
        List<MediaMetadataCompat> albumList = new ArrayList<>();
        Cursor albumsCursor = getAlbumsCursor();

        if (albumsCursor.moveToFirst()) {
            do {
                albumList.add(MediaMetadataHelper.buildAlbumMetadata(albumsCursor));
            } while (albumsCursor.moveToNext());
        }

        return albumList;
    }

    @Override
    public List<MediaMetadataCompat> getArtistListFromLocalStorage() {
        List<MediaMetadataCompat> artistList = new ArrayList<>();
        Cursor artistsCursor = getArtistsCursor();

        if (artistsCursor.moveToFirst()) {
            do {
                artistList.add(MediaMetadataHelper.buildArtistMetadata(artistsCursor));
            } while (artistsCursor.moveToNext());
        }

        return artistList;
    }

    private Cursor getSongsCursor() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM_KEY,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST_KEY,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.YEAR
                //MediaStore.Audio.Media.BOOKMARK,
                //MediaStore.Audio.Media._COUNT,
        };

        // the last parameter sorts the data alphanumerically
        return applicationContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    private Cursor getAlbumsCursor() {

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ALBUM_KEY,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.FIRST_YEAR,
                MediaStore.Audio.Albums.LAST_YEAR,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                //MediaStore.Audio.Albums._COUNT,
                //MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST
        };

        // the last parameter sorts the data alphanumerically
        return applicationContext.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    private Cursor getArtistsCursor() {

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.ARTIST_KEY,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                //MediaStore.Audio.Albums._COUNT,

        };

        // the last parameter sorts the data alphanumerically
        return applicationContext.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
}

