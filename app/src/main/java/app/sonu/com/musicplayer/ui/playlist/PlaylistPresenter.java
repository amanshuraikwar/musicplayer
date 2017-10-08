package app.sonu.com.musicplayer.ui.playlist;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
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
    private Disposable playlistUpdatedDisposable, playlistsUpdatedDisposable;

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
        playlistUpdatedDisposable.dispose();
        playlistsUpdatedDisposable.dispose();
    }

    @Override
    public void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onCreate:called");
        Log.i(TAG, "onCreate:mediaId="+item.getDescription().getMediaId());

        super.onCreate(activity, item);

        playlistUpdatedDisposable =
                mAppBus.playlistUpdatedSubject.subscribe(new Consumer<PlaylistUpdate>() {
                    @Override
                    public void accept(PlaylistUpdate playlistUpdate) throws Exception {
                        handlePlaylistUpdate(playlistUpdate);
                    }
                });

        playlistsUpdatedDisposable =
                mAppBus.playlistsUpdatedSubject.subscribe(new Consumer<List<PlaylistUpdate>>() {
                    @Override
                    public void accept(List<PlaylistUpdate> playlistUpdateList) throws Exception {
                        for (PlaylistUpdate playlistUpdate : playlistUpdateList) {
                            handlePlaylistUpdate(playlistUpdate);
                        }
                    }
                });
    }

    private void handlePlaylistUpdate(PlaylistUpdate playlistUpdate) {
        // todo add dynamic list support
        mMediaBrowserManager.subscribeMediaBrowser();
    }
}
