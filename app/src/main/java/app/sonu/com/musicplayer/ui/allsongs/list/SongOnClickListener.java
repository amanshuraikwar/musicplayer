package app.sonu.com.musicplayer.ui.allsongs.list;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public interface SongOnClickListener extends BaseListItemOnClickListener {
    void onSongClick(Song currentSong);
}
