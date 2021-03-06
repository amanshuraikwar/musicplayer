package app.sonu.com.musicplayer.ui.artists;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 1/8/17.
 */

public class ArtistsPresenter extends BasePresenter<ArtistsMvpView>
        implements ArtistsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = ArtistsPresenter.class.getSimpleName();
    private Context mContext;
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<MediaBrowserCompat.MediaItem> mArtistClickSubject;
    private PublishSubject<Integer> mArtistsScrollToTopSubject;

    private Disposable mArtistsScrollToTopDisposable;

    public ArtistsPresenter(DataManager dataManager,
                            MediaBrowserManager mediaBrowserManager,
                            PublishSubject<MediaBrowserCompat.MediaItem> artistClickSubject,
                            PublishSubject<Integer> artistsScrollToTopSubject) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mArtistClickSubject = artistClickSubject;
        mArtistsScrollToTopSubject = artistsScrollToTopSubject;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        mArtistsScrollToTopDisposable.dispose();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart:called");
        // do nothing
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);

        mArtistsScrollToTopDisposable = mArtistsScrollToTopSubject.subscribe(new Consumer<Integer>() {
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
    public void onArtistClicked(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onArtistClicked:currentArtist=" + item);
        mArtistClickSubject.onNext(item);
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
