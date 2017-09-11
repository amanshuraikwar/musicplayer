package app.sonu.com.musicplayer.data.prefs;

import java.util.List;

/**
 * Created by sonu on 29/6/17.
 */

public interface PrefsHelper {
    boolean isFirstRun();
    boolean setFirstRun(Boolean flag);

    String getPlaylistIds();
    void createPlaylistIdList(String json);

    String getPlaylistById(String id);
    void putPlaylist(String id, String playlist);
}
