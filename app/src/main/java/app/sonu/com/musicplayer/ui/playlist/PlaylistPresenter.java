package app.sonu.com.musicplayer.ui.playlist;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.mediaitemdetail.MediaItemDetailFragmentPresenter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 21/9/17.
 */

public class PlaylistPresenter extends MediaItemDetailFragmentPresenter<PlaylistMvpView>
        implements PlaylistMvpPresenter{

    private static final String TAG = PlaylistPresenter.class.getSimpleName();

    private AppBus mAppBus;
    private Disposable playlistsChangedDisposable;

    public PlaylistPresenter(DataManager dataManager,
                             MediaBrowserManager mediaBrowserManager,
                             AppBus appBus) {
        super(dataManager, mediaBrowserManager);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        super.onDetach();
        playlistsChangedDisposable.dispose();
    }

    @Override
    public void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onCreate:called");
        Log.i(TAG, "onCreate:mediaId="+item.getDescription().getMediaId());

        super.onCreate(activity, item);

        playlistsChangedDisposable = mAppBus.playlistsChangedSubject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.w(TAG, "playlists changed!");
                mMediaBrowserManager.subscribeMediaBrowser();
            }
        });
    }
}
