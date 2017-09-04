package app.sonu.com.musicplayer.ui.albums;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsPresenter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 30/7/17.
 */

public class AlbumsPresenter extends BasePresenter<AlbumsMvpView>
        implements AlbumsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = AllSongsPresenter.class.getSimpleName();
    private Context mContext;
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> mAlbumClickSubject;
    private PublishSubject<Integer> mAlbumsScrollToTopSubject;

    private Disposable mAlbumsScrollToTopDisposable;

    public AlbumsPresenter(DataManager dataManager,
                           MediaBrowserManager mediaBrowserManager,
                           PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> albumClickSubject,
                           PublishSubject<Integer> albumsScrollToTopSubject) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAlbumClickSubject = albumClickSubject;
        mAlbumsScrollToTopSubject = albumsScrollToTopSubject;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        mAlbumsScrollToTopDisposable.dispose();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);

        mAlbumsScrollToTopDisposable = mAlbumsScrollToTopSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mMvpView.scrollListToTop();
            }
        });
    }

    @Override
    public void onCreateView() {
        Log.d(TAG, "onCreateView:called");
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayList(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        Log.d(TAG, "onAlbumClicked:currentAlbum=" + item);
        mAlbumClickSubject.onNext(new Pair<>(item, animatingView));
    }

    @Override
    public void onRefresh() {
        mMvpView.stopLoading();
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
        Log.i(TAG, "onMediaBrowserChildrenLoaded:is mvpview null="+(mMvpView==null));
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
