package app.sonu.com.musicplayer.data.prefs;

import java.util.List;

/**
 * Created by sonu on 29/6/17.
 */

public interface PrefsHelper {
    boolean isFirstRun();
    boolean setFirstRun(Boolean flag);

    String getPlaylistIdList();
    void createPlaylistIdList(String playlistIdListJson);

    String getPlaylistByPlaylistId(String playlistId);
    void putPlaylist(String playlistId, String playlistJson);
    void removePlaylist(String playlistId);
}
