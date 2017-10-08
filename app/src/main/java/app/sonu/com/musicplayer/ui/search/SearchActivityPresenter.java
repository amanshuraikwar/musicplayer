package app.sonu.com.musicplayer.ui.search;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderPresenter;

/**
 * Created by sonu on 22/9/17.
 */

public class SearchActivityPresenter extends MusicPlayerHolderPresenter<SearchActivityMvpView>
        implements SearchActivityMvpPresenter {

    public SearchActivityPresenter(DataManager dataManager,
                                   MediaBrowserManager mediaBrowserManager,
                                   AppBus appBus,
                                   PerSlidingUpPanelBus slidingUpPanelBus) {
        super(dataManager, mediaBrowserManager, appBus, slidingUpPanelBus);
    }
}
