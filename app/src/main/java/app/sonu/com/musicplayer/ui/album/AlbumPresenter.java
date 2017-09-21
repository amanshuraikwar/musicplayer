package app.sonu.com.musicplayer.ui.album;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.ui.base.mediaitemdetail.MediaItemDetailFragmentPresenter;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailPresenter;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 21/8/17.
 */

public class AlbumPresenter extends MediaItemDetailFragmentPresenter<AlbumMvpView>
        implements AlbumMvpPresenter {

    private static final String TAG = AlbumPresenter.class.getSimpleName();

    public AlbumPresenter(DataManager dataManager,
                          MediaBrowserManager mediaBrowserManager,
                          AppBus appBus) {
        super(dataManager, mediaBrowserManager);
    }
}
