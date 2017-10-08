package app.sonu.com.musicplayer.ui.base.medialist;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.LogHelper;

/**
 * Created by sonu on 4/10/17.
 */

public class MediaListPresenter<MvpView extends MediaListMvpView>
        extends BasePresenter<MvpView>
        implements MediaListMvpPresenter<MvpView>, MediaBrowserManager.MediaBrowserCallback  {

    private static final String TAG = LogHelper.getLogTag(MediaListPresenter.class);

    protected MediaBrowserManager mMediaBrowserManager;
    protected Context mContext;
    private String mRootMediaId;

    public MediaListPresenter(DataManager dataManager,
                              MediaBrowserManager mediaBrowserManager) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart:called");
    }

    @Override
    public void onCreate(FragmentActivity activity, String rootMediaId) {
        Log.d(TAG, "onCreate:called");
        Log.i(TAG, "onCreate:mediaId="+rootMediaId);
        mContext = activity;

        mRootMediaId = rootMediaId;

        //init media browser
        // mediaid should be set before calling initMediaBrowser
        mMediaBrowserManager.setMediaId(rootMediaId);
        mMediaBrowserManager.initMediaBrowser(activity);

    }

    @Override
    public void onCreateView() {
        Log.d(TAG, "onCreateView:called");

        //check if media browser is already connected or not
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayMediaList(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public String getRootMediaId() {
        return mRootMediaId;
    }

    // mediabrowser callback
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
        Log.i(TAG, "onMediaBrowserChildrenLoaded:is mvpview null="+(mMvpView==null));

        mMvpView.displayMediaList(items);
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
