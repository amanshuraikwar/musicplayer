package app.sonu.com.musicplayer.ui.playlists;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.manager.MediaBrowserManager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 5/9/17.
 */

public class PlaylistsPresenter extends BasePresenter<PlaylistsMvpView>
        implements PlaylistsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = PlaylistsPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private PublishSubject<Integer> mPlaylistsScrollToTopSubject;
    private PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> mPlaylistClickSubject;
    private PublishSubject<String> mPlaylistsChangedSubject;

    private Disposable mPlaylistsScrollToTopDisposable, mPlaylistsChangedDisposable;

    public PlaylistsPresenter(DataManager dataManager,
                              MediaBrowserManager mediaBrowserManager,
                              AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mPlaylistsScrollToTopSubject = appBus.playlistsScrollToTopSubject;
        mPlaylistClickSubject = appBus.playlistClickSubject;
        mPlaylistsChangedSubject = appBus.playlistsChangedSubject;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        mPlaylistsScrollToTopDisposable.dispose();
        mPlaylistsChangedDisposable.dispose();
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        mPlaylistsScrollToTopDisposable = mPlaylistsScrollToTopSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mMvpView.scrollListToTop();
            }
        });

        mPlaylistsChangedDisposable = mPlaylistsChangedSubject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.w(TAG, "playlists changed!");
                mMediaBrowserManager.subscribeMediaBrowser();
            }
        });
    }

    @Override
    public void onCreateView() {
        Log.d(TAG, "onCreateView:called");
        //check if media browser is already connected or not
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayList(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onPlaylistClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        Log.d(TAG, "onPlaylistClicked:currentAlbum=" + item);
        mPlaylistClickSubject.onNext(new Pair<>(item, animatingView));
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        Log.d(TAG, "onMediaBrowserConnected:called");
        // do nothing
    }

    @Override
    public void onMediaBrowserConnectionSuspended() {
        Log.e(TAG, "onMediaBrowserConnectionSuspended:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onMediaBrowserConnectionFailed() {
        Log.e(TAG, "onMediaBrowserConnectionFailed:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items) {
        Log.d(TAG, "onMediaBrowserChildrenLoaded:called");
        mMvpView.displayList(items);
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {
        Log.e(TAG, "onMediaBrowserSubscriptionError:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {
        // do nothing
    }
}
