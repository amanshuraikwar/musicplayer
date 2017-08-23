package app.sonu.com.musicplayer.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.module.BusModule;
import app.sonu.com.musicplayer.mediaplayer.MediaPlayerService;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 29/6/17.
 */

public class MainPresenter extends BasePresenter<MainMvpView>
        implements MainMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private PublishSubject<MediaBrowserCompat.MediaItem> mSelectedItemPublishSubject;
    private PublishSubject<MediaBrowserCompat.MediaItem> mAlbumClickSubject;
    private PublishSubject<MediaBrowserCompat.MediaItem> mArtistClickSubject;
    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;

    public MainPresenter(DataManager dataManager,
                         PublishSubject<MediaBrowserCompat.MediaItem> selectedItemPublishSubject,
                         PublishSubject<MediaBrowserCompat.MediaItem> albumClickSubject,
                         PublishSubject<MediaBrowserCompat.MediaItem> artistClickSubject,
                         MediaBrowserManager mediaBrowserManager) {
        super(dataManager);
        mSelectedItemPublishSubject = selectedItemPublishSubject;
        mAlbumClickSubject = albumClickSubject;
        mArtistClickSubject = artistClickSubject;
        this.mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
    }

    @Override
    public void onDetach() {
        //nothing
    }

    @Override
    public void onStart() {
        mSelectedItemPublishSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MediaBrowserCompat.MediaItem value) {
                if (mMvpView.isSlidingUpPaneHidden()) {
                    mMvpView.setSlidingUpPaneCollapsed();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        mAlbumClickSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MediaBrowserCompat.MediaItem value) {
                mMvpView.startAlbumFragment(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        mArtistClickSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MediaBrowserCompat.MediaItem value) {
                mMvpView.startArtistFragment(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        mContext = activity;

        //check if media browser is already connected or not
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
//            mMvpView.displaySearchResults(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onCreateView() {
        //do nothing
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:called");
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onSearchResultClick(MediaBrowserCompat.MediaItem item) {
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getMediaId(), null);
        mSelectedItemPublishSubject.onNext(item);
    }

    @Override
    public void onSongSearchResultClick(MediaBrowserCompat.MediaItem item) {
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getMediaId(), null);
        mSelectedItemPublishSubject.onNext(item);
    }

    @Override
    public void onAlbumSearchResultClick(MediaBrowserCompat.MediaItem item) {
        mAlbumClickSubject.onNext(item);
        mMvpView.hideSearchView();
    }

    @Override
    public void onArtistSearchResultClick(MediaBrowserCompat.MediaItem item) {
        mArtistClickSubject.onNext(item);
        mMvpView.hideSearchView();
    }

    @Override
    public void onSlidingUpPanelSlide(float slideOffset) {
//        Log.i(TAG, "onSlidingUpPanelSlide:called slideOffset="+slideOffset);
        if (slideOffset >= 0.9) {
            mMvpView.hideMiniPlayer();
        } else if (slideOffset <= 0.1) {
            mMvpView.showMiniPlayer();
        }
    }

    @Override
    public void onSearchQueryTextChange(String searchText) {
        if (searchText != null && !searchText.equals("")) {
            mMediaBrowserManager.search(searchText);
        } else {
            mMvpView.displaySearchResults(new ArrayList<MediaBrowserCompat.MediaItem>());
        }
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        Log.d(TAG, "onMediaBrowserConnected:called");
//        mMediaBrowserManager.subscribeMediaBrowser();
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
//        mMvpView.displayList(items);
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {
        Log.e(TAG, "onMediaBrowserSubscriptionError:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {
        mMvpView.displaySearchResults(items);
    }
}
