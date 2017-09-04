package app.sonu.com.musicplayer.ui.artist;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsMvpView;

/**
 * Created by sonu on 21/8/17.
 */

public interface ArtistMvpPresenter extends BaseMvpPresenter<ArtistMvpView> {
    void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item  );
    void onCreateView();
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onDragDismissed();
    void onBackIbClick();
}
