package app.sonu.com.musicplayer.data.local;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.ApplicationContext;
import app.sonu.com.musicplayer.utils.FileUtil;

/**
 * Created by sonu on 30/6/17.
 */

public class AppLocalStorageHelper implements LocalStorageHelper {

    private Context applicationContext;

    public AppLocalStorageHelper(@ApplicationContext Context context) {
        this.applicationContext = context;
    }

    @Override
    public List<File> getFileList() {
        File root = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath());
        return listDir(root);
    }

    @Override
    public List<File> getFileList(String pathOfFolder) {
        File directory = new File(pathOfFolder);
        return listDir(directory);
    }

    @Override
    public List<Song> getSongListFromLocalStorage() {
        List<Song> songList = new ArrayList<>();
        Cursor songsCursor = getSongsCursor();

        if (songsCursor.moveToFirst()) {
            do {
                songList.add(new Song(songsCursor));
            } while (songsCursor.moveToNext());
        }

        return songList;
    }

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

    public Cursor getSongsCursor() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.COMPOSER
        };

        // the last parameter sorts the data alphanumerically
        return applicationContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE);
    }
}
