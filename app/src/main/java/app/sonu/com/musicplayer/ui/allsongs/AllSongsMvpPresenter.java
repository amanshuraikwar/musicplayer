package app.sonu.com.musicplayer.ui.allsongs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;
import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 2/7/17.
 */

public interface AllSongsMvpPresenter extends BaseMvpPresenter<AllSongsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onRefresh();
    void onShuffleAllClick();
}
