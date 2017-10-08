package app.sonu.com.musicplayer.ui.createplaylist;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 23/9/17.
 */

public interface CreatePlaylistMvpView extends BaseMvpView {
    void displayToast(String message);
    void close();
}
