package app.sonu.com.musicplayer.ui.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.LogHelper;

/**
 * Created by sonu on 8/11/17.
 */

public class BaseMediaListPresenter<MvpView extends BaseMediaListMvpView>
        extends BasePresenter<MvpView>
        implements BaseMediaListMvpPresenter<MvpView>, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = LogHelper.getLogTag(BaseMediaListMvpPresenter.class);

    private MediaBrowserManager mMediaBrowserManager;
    private FragmentActivity mActivity;

    public BaseMediaListPresenter(DataManager dataManager,
                                  MediaBrowserManager mediaBrowserManager,
                                  FragmentActivity activity) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mActivity = activity;
    }

    @Override
    public void onAttach(MvpView mvpView) {
        super.onAttach(mvpView);

        Log.d(TAG, "onAttach:called");

        mMediaBrowserManager.setMediaId(mMvpView.getMediaId());
        mMediaBrowserManager.initMediaBrowser(mActivity);

        //check if media browser is already connected or not
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayMediaItems(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onStart() {
        // do nothing
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        Log.d(TAG, "onMediaBrowserConnected:called");
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
        Log.d(TAG, "onMediaBrowserChildrenLoaded:called");
        Log.i(TAG, "onMediaBrowserChildrenLoaded:is mvpview null="+(mMvpView==null));

        mMvpView.displayMediaItems(items);
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {

    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {
        // do nothing
    }
}
