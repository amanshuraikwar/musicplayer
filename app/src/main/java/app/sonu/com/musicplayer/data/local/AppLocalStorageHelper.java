package app.sonu.com.musicplayer.data.local;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.di.ApplicationContext;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.utils.FileUtil;

/**
 * Created by sonu on 30/6/17.
 */

public class AppLocalStorageHelper implements LocalStorageHelper {

    private Context applicationContext;

    public AppLocalStorageHelper(@ApplicationContext Context context) {
        this.applicationContext = context;
    }

    @Deprecated
    @Override
    public List<File> getFileList() {
        File root = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath());
        return listDir(root);
    }

    @Deprecated
    @Override
    public List<File> getFileList(String pathOfFolder) {
        File directory = new File(pathOfFolder);
        return listDir(directory);
    }

    @Override
    public List<MediaMetadataCompat> getSongListFromLocalStorage() {
        List<MediaMetadataCompat> songList = new ArrayList<>();
        Cursor songsCursor = getSongsCursor();

        if (songsCursor.moveToFirst()) {
            do {
                songList.add(buildSongMetadata(songsCursor));
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
                albumList.add(buildAlbumMetadata(albumsCursor));
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
                artistList.add(buildArtistMetadata(artistsCursor));
            } while (artistsCursor.moveToNext());
        }

        return artistList;
    }

    @Deprecated
    private MediaMetadataCompat buildFromCursor(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(2))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(3))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(cursor.getString(4)))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_TRACK_SOURCE, cursor.getString(5))
                .build();
    }

    private MediaMetadataCompat buildSongMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_TRACK_SOURCE, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, cursor.getString(2))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_DATE_MODIFIED, cursor.getString(3))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_DISPLAY_NAME, cursor.getString(4))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_SIZE, cursor.getString(5))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(6))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, cursor.getString(6))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(7))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_ID, cursor.getString(8))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY, cursor.getString(9))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(10))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, cursor.getString(10))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_ID, cursor.getString(11))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY, cursor.getString(12))
                .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, cursor.getString(13))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                        Long.parseLong(cursor.getString(14)))
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                        Long.parseLong(cursor.getString(15)))
                //.putLong(MediaMetadataCompat.METADATA_KEY_YEAR,
                //        Long.parseLong(cursor.getString(16).trim()))
                .build();
    }

    private MediaMetadataCompat buildAlbumMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cursor.getString(2))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY, cursor.getString(3))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, cursor.getString(4))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, cursor.getString(4))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_FIRST_YEAR, cursor.getString(5))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_LAST_YEAR, cursor.getString(6))
                //.putLong(MediaMetadataCompat.METADATA_KEY_YEAR,
                //        Integer.parseInt(cursor.getString(6)))
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                        Long.parseLong(cursor.getString(7)))
                .build();
    }

    private MediaMetadataCompat buildArtistMetadata(Cursor cursor) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(0))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(1))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, cursor.getString(1))
                .putString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY, cursor.getString(2))
                .putLong(MusicProviderSource.CUSTOM_METADATA_KEY_NUM_ALBUMS,
                        Integer.parseInt(cursor.getString(3)))
                .putLong(MusicProviderSource.CUSTOM_METADATA_KEY_NUM_TRACKS,
                        Integer.parseInt(cursor.getString(4)))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                        cursor.getString(4)+" tracks")
                .build();
    }

    private Cursor getSongsCursor() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                //MediaStore.Audio.Media._COUNT,
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
                //MediaStore.Audio.Media.BOOKMARK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.YEAR
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
                //MediaStore.Audio.Albums._COUNT,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ALBUM_KEY,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.FIRST_YEAR,
                MediaStore.Audio.Albums.LAST_YEAR,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
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
                //MediaStore.Audio.Albums._COUNT,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.ARTIST_KEY,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS

        };

        // the last parameter sorts the data alphanumerically
        return applicationContext.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Deprecated
    List<File> listDir(File f){
        File files[] = f.listFiles();
        List<File> fileList = new ArrayList<>();
        List<File> folderList = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                folderList.add(file);
            } else if (
                    !file.isHidden() &&
                    FileUtil.fileIsMimeType(file, "audio/*", MimeTypeMap.getSingleton())
                    ) {
                fileList.add(file);
            }
        }
        folderList.addAll(fileList);
        return new ArrayList<>(folderList);
    }
}

