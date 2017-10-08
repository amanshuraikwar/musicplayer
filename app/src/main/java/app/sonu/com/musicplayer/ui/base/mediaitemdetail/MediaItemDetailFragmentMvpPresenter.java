package app.sonu.com.musicplayer.ui.base.mediaitemdetail;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 21/9/17.
 */

public interface MediaItemDetailFragmentMvpPresenter<MvpView extends MediaItemDetailFragmentMvpView>
        extends BaseMvpPresenter<MvpView> {
    void onCreateView();
    void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item);
    void onSongClicked(MediaBrowserCompat.MediaItem item);
    void onBackIbClick();
    void onShuffleAllClick();
    MediaBrowserCompat.MediaItem getMediaItem();
    void onAddToPlaylistClick(MediaBrowserCompat.MediaItem item);
}
