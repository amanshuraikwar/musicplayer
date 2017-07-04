package app.sonu.com.musicplayer.data.local;

import java.io.File;
import java.util.List;

import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 30/6/17.
 */

public interface LocalStorageHelper {
    List<File> getFileList();
    List<File> getFileList(String pathOfFolder);
    List<Song> getSongListFromLocalStorage();
}
