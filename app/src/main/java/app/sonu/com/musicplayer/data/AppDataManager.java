package app.sonu.com.musicplayer.data;

import android.support.v4.media.MediaMetadataCompat;

import java.util.List;

import app.sonu.com.musicplayer.data.network.ApiHelper;
import app.sonu.com.musicplayer.data.db.DbHelper;
import app.sonu.com.musicplayer.data.local.LocalStorageHelper;
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
    public String getPlaylistIds() {
        return mPrefsHelper.getPlaylistIds();
    }

    @Override
    public void createPlaylistIdList(String json) {
        mPrefsHelper.createPlaylistIdList(json);
    }

    @Override
    public String getPlaylistById(String id) {
        return mPrefsHelper.getPlaylistById(id);
    }

    @Override
    public void putPlaylist(String id, String playlist) {
        mPrefsHelper.putPlaylist(id, playlist);
    }
}
