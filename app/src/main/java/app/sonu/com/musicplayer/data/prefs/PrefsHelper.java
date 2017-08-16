package app.sonu.com.musicplayer.data.prefs;

import java.util.ArrayList;

import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 29/6/17.
 */

public interface PrefsHelper {
    boolean isFirstRun();
    boolean setFirstRun(Boolean flag);

    //not sure
    ArrayList<Song> getSongQueue();
    boolean setSongQueue(ArrayList<Song> songQueue);

    int getCurrentSongIndex();
    boolean setCurrentSongIndex(int index);
}
