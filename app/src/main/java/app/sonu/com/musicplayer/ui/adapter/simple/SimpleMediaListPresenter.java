package app.sonu.com.musicplayer.ui.adapter.simple;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.adapter.BaseMediaListPresenter;

/**
 * Created by sonu on 8/11/17.
 */

public class SimpleMediaListPresenter extends BaseMediaListPresenter<SimpleMediaListMvpView>
        implements SimpleMediaListMvpPresenter {

    public SimpleMediaListPresenter(DataManager dataManager,
                                    MediaBrowserManager mediaBrowserManager,
                                    FragmentActivity activity) {
        super(dataManager, mediaBrowserManager, activity);
    }
}
