package app.sonu.com.musicplayer.ui.album;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.mediaitemdetail.MediaItemDetailFragmentPresenter;
import app.sonu.com.musicplayer.util.LogHelper;

/**
 * Created by sonu on 21/8/17.
 */

public class AlbumPresenter extends MediaItemDetailFragmentPresenter<AlbumMvpView>
        implements AlbumMvpPresenter {

    private static final String TAG = LogHelper.getLogTag(AlbumPresenter.class);

    public AlbumPresenter(DataManager dataManager,
                          MediaBrowserManager mediaBrowserManager,
                          AppBus appBus) {
        super(dataManager, mediaBrowserManager);
    }
}
