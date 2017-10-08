package app.sonu.com.musicplayer.ui.search;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;

/**
 * Created by sonu on 21/9/17.
 */

public class SearchPresenter extends BasePresenter<SearchMvpView>
        implements SearchMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = SearchPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private AppBus mAppBus;

    public SearchPresenter(DataManager dataManager,
                           MediaBrowserManager mediaBrowserManager,
                           AppBus appBus) {
        super(dataManager);

        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);
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
    public void onSongClicked(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onSongClick:item="+item);

        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getMediaId(), null);
    }

    @Override
    public void onAlbumClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        mAppBus.albumClickSubject.onNext(new Pair<>(item, animatingView));
    }

    @Override
    public void onArtistClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        mAppBus.artistClickSubject.onNext(new Pair<>(item, animatingView));
    }

    @Override
    public void onSearchQueryTextChange(String searchString) {
        if (searchString != null && !searchString.equals("")) {
            mMediaBrowserManager.search(searchString);
        } else {
            mMvpView.displayList(new ArrayList<MediaBrowserCompat.MediaItem>());
        }
    }

    @Override
    public void onBackIbClick() {
        mMvpView.close();
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        Log.d(TAG, "onMediaBrowserConnected:called");
        mMediaBrowserManager.subscribeMediaBrowser();
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
        // do nothing
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {
        Log.e(TAG, "onMediaBrowserSubscriptionError:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {
        mMvpView.displayList(items);
    }
}
