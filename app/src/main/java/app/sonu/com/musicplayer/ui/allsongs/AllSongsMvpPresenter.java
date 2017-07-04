package app.sonu.com.musicplayer.ui.allsongs;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;
import app.sonu.com.musicplayer.base.ui.BaseMvpView;
import app.sonu.com.musicplayer.data.db.model.Song;

/**
 * Created by sonu on 2/7/17.
 */

public interface AllSongsMvpPresenter extends BaseMvpPresenter<AllSongsMvpView> {
    void onRefresh();
    void onSongClick(Song song);
}
