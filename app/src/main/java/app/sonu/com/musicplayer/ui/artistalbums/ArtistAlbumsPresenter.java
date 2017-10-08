package app.sonu.com.musicplayer.ui.artistalbums;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.medialist.MediaListMvpPresenter;
import app.sonu.com.musicplayer.ui.base.medialist.MediaListPresenter;

/**
 * Created by sonu on 4/10/17.
 */

public class ArtistAlbumsPresenter extends MediaListPresenter<ArtistAlbumsMvpView>
        implements  ArtistAlbumsMvpPresenter {

    private AppBus mAppBus;

    public ArtistAlbumsPresenter(DataManager dataManager,
                                 MediaBrowserManager mediaBrowserManager,
                                 AppBus appBus) {
        super(dataManager, mediaBrowserManager);
        mAppBus = appBus;
    }

    @Override
    public void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        mAppBus.albumClickSubject.onNext(new Pair<>(item, animatingView));
    }
}
