package app.sonu.com.musicplayer.ui.musicplayer;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 4/7/17.
 */

public interface MusicPlayerMvpView extends BaseMvpView {
    void displaySong(Song song);
}
