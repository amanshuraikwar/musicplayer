package app.sonu.com.musicplayer.ui.playingqueue;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 5/10/17.
 */

public interface PlayingQueueMvpView extends BaseMvpView {
    void displayQueue(List<MediaSessionCompat.QueueItem> queue);
    boolean updateQueueIndex(int index);
    void displayToast(String message);
    void onMetadataChanged();
}
