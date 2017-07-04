package app.sonu.com.musicplayer.ui.allsongs.list;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public class SongVisitable extends BaseVisitable<SongOnClickListener, AllSongsListTypeFactory> {

    private Song song;

    public SongVisitable(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public int type(AllSongsListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }

    @Override
    public int getUniqueId() {
        return 0;
    }
}
