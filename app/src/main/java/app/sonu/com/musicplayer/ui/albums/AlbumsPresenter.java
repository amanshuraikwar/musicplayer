package app.sonu.com.musicplayer.ui.albums;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsPresenter;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 30/7/17.
 */

public class AlbumsPresenter extends BasePresenter<AlbumsMvpView>
        implements AlbumsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = AllSongsPresenter.class.getSimpleName();
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<MediaBrowserCompat.MediaItem> mAlbumClickSubject;

    public AlbumsPresenter(DataManager dataManager,
                           MediaBrowserManager mediaBrowserManager,
                           PublishSubject<MediaBrowserCompat.MediaItem> albumClickSubject) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAlbumClickSubject = albumClickSubject;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        mMediaBrowserManager.initMediaBrowser(activity);
    }

    @Override
    public void onCreateView() {
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayList(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onDestroy() {
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onAlbumClicked(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onAlbumClicked:currentAlbum=" + item);
        mAlbumClickSubject.onNext(item);
    }

    @Override
    public void onRefresh() {
        mMvpView.stopLoading();
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        mMediaBrowserManager.subscribeMediaBrowser();
    }

    @Override
    public void onMediaBrowserConnectionSuspended() {

    }

    @Override
    public void onMediaBrowserConnectionFailed() {

    }

    @Override
    public void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items) {
        mMvpView.displayList(items);
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {

    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {

    }
}
