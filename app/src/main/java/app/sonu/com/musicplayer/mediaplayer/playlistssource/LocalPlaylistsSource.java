package app.sonu.com.musicplayer.mediaplayer.playlistssource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 6/9/17.
 */

public class LocalPlaylistsSource implements PlaylistsSource {

    private DataManager mDataManager;

    public LocalPlaylistsSource(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void createPlaylistIdList() {
        putPlaylistIds(new ArrayList<String>());
    }

    private void putPlaylistIds(List<String> list) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>(){}.getType();
        mDataManager.createPlaylistIdList(gson.toJson(list, listType));
    }

    @Override
    public List<String> getPlaylistIds() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>(){}.getType();
        if (mDataManager.getPlaylistIds() != null) {
            return gson.fromJson(mDataManager.getPlaylistIds(), listType);
        }

        return null;
    }

    @Override
    public Playlist getPlaylistById(long playlistId) {
        Gson gson = new Gson();
        return gson.fromJson(mDataManager.getPlaylistById(playlistId+""), Playlist.class);
    }

    @Override
    public void addNewPlaylist(Playlist playlist) {
        List<String> playlistIds = getPlaylistIds();
        if (playlistIds != null) {
            playlistIds.add(playlist.getId()+"");
            putPlaylistIds(playlistIds);
            putPlaylist(playlist);
        }
    }

    private void putPlaylist(Playlist playlist) {
        Gson gson = new Gson();
        String json = gson.toJson(playlist, Playlist.class);
        mDataManager.putPlaylist(playlist.getId()+"", json);
    }

    @Override
    public void addMusicMediaIdToPlaylist(long playlistId, String mediaId) {
        Playlist playlist = getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.getMusicMediaIds().add(mediaId);
            putPlaylist(playlist);
        }
    }

    @Override
    public void removeMusicMediaIdFromPlaylist(long playlistId, String mediaId) {
        Playlist playlist = getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.getMusicMediaIds().remove(playlist.getMusicMediaIds().indexOf(mediaId));
            putPlaylist(playlist);
        }
    }

    @Override
    public void deletePlaylist(long id) {
        List<String> playlistIds = getPlaylistIds();
        if (playlistIds != null) {
            int index = playlistIds.indexOf(id+"");
            if (index>0) {
                playlistIds.remove(index);
                putPlaylistIds(playlistIds);
            }
        }
    }
}
