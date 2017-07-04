package app.sonu.com.musicplayer.data.db;

import java.util.List;

import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 3/7/17.
 */

public interface DbHelper {
    void addSongsToDb(List<Song> songList);
    void eraseDb();
    List<Song> getAllSongsFromDb();
}
