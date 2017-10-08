package app.sonu.com.musicplayer.ui.home;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderPresenter;

/**
 * Created by sonu on 14/9/17.
 */

public class HomePresenter extends MusicPlayerHolderPresenter<HomeMvpView>
        implements HomeMvpPresenter {

    private static final String TAG = HomePresenter.class.getSimpleName();

    public HomePresenter(DataManager dataManager,
                         MediaBrowserManager mediaBrowserManager,
                         AppBus appBus,
                         PerSlidingUpPanelBus slidingUpPanelBus) {
        super(dataManager, mediaBrowserManager, appBus, slidingUpPanelBus);
    }
}
