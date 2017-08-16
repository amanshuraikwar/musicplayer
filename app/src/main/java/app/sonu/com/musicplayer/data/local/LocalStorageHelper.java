package app.sonu.com.musicplayer.data.local;

import android.support.v4.media.MediaMetadataCompat;

import java.io.File;
import java.util.List;

import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 30/6/17.
 */

public interface LocalStorageHelper {
    List<File> getFileList();
    List<File> getFileList(String pathOfFolder);
    List<MediaMetadataCompat> getSongListFromLocalStorage();
    List<MediaMetadataCompat> getAlbumListFromLocalStorage();
    List<MediaMetadataCompat> getArtistListFromLocalStorage();
}
