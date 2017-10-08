package app.sonu.com.musicplayer.ui.albumcover;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 4/10/17.
 */

public interface AlbumCoverMvpPresenter extends BaseMvpPresenter<AlbumCoverMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onPageSelected(MediaSessionCompat.QueueItem item);
}
