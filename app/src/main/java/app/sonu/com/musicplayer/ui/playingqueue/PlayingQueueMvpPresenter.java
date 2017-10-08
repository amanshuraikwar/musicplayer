package app.sonu.com.musicplayer.ui.playingqueue;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 5/10/17.
 */

public interface PlayingQueueMvpPresenter extends BaseMvpPresenter<PlayingQueueMvpView > {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onQueueItemClick(MediaSessionCompat.QueueItem item);
    void setScrollView(RecyclerView recyclerView);
}
