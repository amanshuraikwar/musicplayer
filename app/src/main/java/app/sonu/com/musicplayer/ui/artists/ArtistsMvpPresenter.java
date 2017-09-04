package app.sonu.com.musicplayer.ui.artists;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 1/8/17.
 */

public interface ArtistsMvpPresenter extends BaseMvpPresenter<ArtistsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onArtistClicked(MediaBrowserCompat.MediaItem item);
    void onRefresh();
}
