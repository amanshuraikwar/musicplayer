package app.sonu.com.musicplayer.mediaplayer.playlist;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.module.LocalStorageModule;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.util.LogHelper;

/**
 * Created by sonu on 6/9/17.
 */

public class LocalPlaylistsSource implements PlaylistsSource {

    private static final String TAG = LogHelper.getLogTag(LocalPlaylistsSource.class);

    private DataManager mDataManager;
    private Gson gson;

    public LocalPlaylistsSource(DataManager dataManager) {
        mDataManager = dataManager;
        gson = new Gson();
    }

    @Override
    public void createPlaylistIdList() {
        Log.d(TAG, "createPlaylistIdList:called");
        putPlaylistIdList(new ArrayList<String>());
    }

    private void putPlaylistIdList(List<String> list) {
        Type listType = new TypeToken<List<String>>(){}.getType();
        mDataManager.createPlaylistIdList(gson.toJson(list, listType));
    }

    @Override
    public List<String> getPlaylistIdList() {
        Log.d(TAG, "getPlaylistIdList:called");
        Type listType = new TypeToken<List<String>>(){}.getType();
        if (mDataManager.getPlaylistIdList() != null) {
            return gson.fromJson(mDataManager.getPlaylistIdList(), listType);
        }

        return null;
    }

    @Override
    public Playlist getPlaylistByPlaylistId(String playlistId) {
        Log.d(TAG, "getPlaylistByPlaylistId:called");
        if (mDataManager.getPlaylistByPlaylistId(playlistId) != null) {
            return gson.fromJson(mDataManager.getPlaylistByPlaylistId(playlistId), Playlist.class);
        }

        return null;
    }

    @Override
    public void createPlaylist(Playlist playlist) {
        Log.d(TAG, "createPlaylist:called");
        List<String> playlistIdList = getPlaylistIdList();
        if (playlistIdList  != null) {
            playlistIdList.add(playlist.getPlaylistId());
            putPlaylistIdList(playlistIdList);
            putPlaylist(playlist);
        }
    }

    private void putPlaylist(Playlist playlist) {
        Log.d(TAG, "putPlaylist:called");
        Log.i(TAG, "playlist="+playlist);
        Gson gson = new Gson();
        String json = gson.toJson(playlist, Playlist.class);
        mDataManager.putPlaylist(playlist.getPlaylistId(), json);
    }

    @Override
    public void addSongIdToPlaylist(String songId, String playlistId) {
        Log.d(TAG, "addSongIdToPlaylist:called");
        Playlist playlist = getPlaylistByPlaylistId(playlistId);
        if (playlist != null) {
            playlist.getSongIdList().add(songId);
            putPlaylist(playlist);
        }
    }

    @Override
    public void removeSongIdFromPlaylist(String songId, String playlistId) {
        Log.d(TAG, "removeSongFromPlaylist:called");
        Playlist playlist = getPlaylistByPlaylistId(playlistId);
        if (playlist != null) {
            playlist.getSongIdList().remove(playlist.getSongIdList().indexOf(songId));
            putPlaylist(playlist);
        }
    }

    @Override
    public void deletePlaylist(String playlistId) {
        Log.d(TAG, "deletePlaylist:called");
        List<String> playlistIdList = getPlaylistIdList();
        if (playlistIdList != null) {
            int index = playlistIdList.indexOf(playlistId);
            if (index>0) {
                playlistIdList.remove(index);
                putPlaylistIdList(playlistIdList);
            }
        }
    }
}
