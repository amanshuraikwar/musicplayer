package app.sonu.com.musicplayer.ui.artist;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.album.AlbumMvpView;
import app.sonu.com.musicplayer.ui.base.mediaitemdetail.MediaItemDetailFragmentPresenter;

/**
 * Created by sonu on 21/9/17.
 */

public class ArtistPresenter extends MediaItemDetailFragmentPresenter<ArtistMvpView>
        implements ArtistMvpPresenter {

    public ArtistPresenter(DataManager dataManager,
                           MediaBrowserManager mediaBrowserManager,
                           AppBus appBus) {
        super(dataManager, mediaBrowserManager);
    }
}
