package app.sonu.com.musicplayer.ui.allsongs;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 2/7/17.
 */

public interface AllSongsMvpPresenter extends BaseMvpPresenter<AllSongsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onShuffleAllClick();
}
