package app.sonu.com.musicplayer.ui.addsongstoplaylists;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;
import app.sonu.com.musicplayer.ui.createplaylist.CreatePlaylistMvpView;

/**
 * Created by sonu on 23/9/17.
 */

public interface AddSongsToPlaylistsMvpPresenter extends BaseMvpPresenter<AddSongsToPlaylistsMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView(String songId);
    void onDoneBtnClick();
    void onCreateNewPlaylistBtnClick();
    void onPlaylistClick(MediaBrowserCompat.MediaItem playlist);
}
