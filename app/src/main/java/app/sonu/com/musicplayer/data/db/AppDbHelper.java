package app.sonu.com.musicplayer.data.db;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 3/7/17.
 */

public class AppDbHelper implements DbHelper {
    @Override
    public void addSongsToDb(List<Song> songList) {
        for (Song song : songList) {
            song.save();
        }
    }

    @Override
    public void eraseDb() {
        DataSupport.deleteAll(Song.class);
    }

    @Override
    public List<Song> getAllSongsFromDb() {
        List<Song> songList = new ArrayList<>();
        
        try {
            songList = DataSupport.findAll(Song.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("yoyo", e+"");
        }
        return songList;
    }
}
