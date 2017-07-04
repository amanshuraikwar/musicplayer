package app.sonu.com.musicplayer.ui.allsongs;

import java.util.List;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseMvpView;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public interface AllSongsMvpView extends BaseMvpView {
    void displayList(List<Song> songList);
    void startLoading();
    void stopLoading();
}
