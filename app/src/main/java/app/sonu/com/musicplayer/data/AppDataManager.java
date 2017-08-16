package app.sonu.com.musicplayer.data;

import android.support.v4.media.MediaMetadataCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.data.db.DbHelper;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.data.local.LocalStorageHelper;
import app.sonu.com.musicplayer.data.network.ApiHelper;
import app.sonu.com.musicplayer.data.prefs.PrefsHelper;

/**
 * Created by sonu on 29/6/17.
 */

public class AppDataManager implements DataManager {

    private ApiHelper mApiHelper;
    private PrefsHelper mPrefsHelper;
    private LocalStorageHelper mLocalStorageHelper;
    private DbHelper mDbHelper;

    public AppDataManager(ApiHelper apiHelper, PrefsHelper prefsHelper,
                          LocalStorageHelper localStorageHelper, DbHelper dbHelper) {
        this.mApiHelper = apiHelper;
        this.mPrefsHelper = prefsHelper;
        this.mLocalStorageHelper = localStorageHelper;
        this.mDbHelper = dbHelper;
    }

    @Override
    public List<File> getFileList() {
        return mLocalStorageHelper.getFileList();
    }

    @Override
    public List<File> getFileList(String pathOfFolder) {
        return mLocalStorageHelper.getFileList(pathOfFolder);
    }

    @Override
    public List<MediaMetadataCompat> getSongListFromLocalStorage() {
        return mLocalStorageHelper.getSongListFromLocalStorage();
    }

    @Override
    public List<MediaMetadataCompat> getAlbumListFromLocalStorage() {
        return mLocalStorageHelper.getAlbumListFromLocalStorage();
    }

    @Override
    public List<MediaMetadataCompat> getArtistListFromLocalStorage() {
        return mLocalStorageHelper.getArtistListFromLocalStorage();
    }

    @Override
    public boolean isFirstRun() {
        return mPrefsHelper.isFirstRun();
    }

    @Override
    public boolean setFirstRun(Boolean flag) {
        return mPrefsHelper.setFirstRun(flag);
    }

    @Override
    public ArrayList<Song> getSongQueue() {
        return mPrefsHelper.getSongQueue();
    }

    @Override
    public boolean setSongQueue(ArrayList<Song> songQueue) {
        return mPrefsHelper.setSongQueue(songQueue);
    }

    @Override
    public int getCurrentSongIndex() {
        return mPrefsHelper.getCurrentSongIndex();
    }

    @Override
    public boolean setCurrentSongIndex(int index) {
        return mPrefsHelper.setCurrentSongIndex(index);
    }

    @Override
    public void addSongsToDb(List<Song> songList) {
        mDbHelper.addSongsToDb(songList);
    }

    @Override
    public void eraseDb() {
        mDbHelper.eraseDb();
    }

    @Override
    public List<Song> getAllSongsFromDb() {
        return mDbHelper.getAllSongsFromDb();
    }
}
