package app.sonu.com.musicplayer.ui.albumcover;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;
import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 4/10/17.
 */

public interface AlbumCoverMvpView extends BaseMvpView {
    void displayQueue(List<MediaSessionCompat.QueueItem> queue);
    boolean updateQueueIndex(int index);
    void displayToast(String message);
}
