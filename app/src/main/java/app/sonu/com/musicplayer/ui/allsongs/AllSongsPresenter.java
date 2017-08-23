package app.sonu.com.musicplayer.ui.allsongs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsPresenter extends
        BasePresenter<AllSongsMvpView> implements
        AllSongsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = AllSongsPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private PublishSubject<MediaBrowserCompat.MediaItem> mSelectedItemPublishSubject;

    public AllSongsPresenter(DataManager dataManager,
                             MediaBrowserManager mediaBrowserManager,
                             PublishSubject<MediaBrowserCompat.MediaItem> selectedItemPublishSubject) {
        super(dataManager);
        this.mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mSelectedItemPublishSubject = selectedItemPublishSubject;
    }

    @Override
    public void onDetach() {
        //nothing
    }

    @Override
    public void onStart() {
        //nothing
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        mContext = activity;
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy:called");
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onSongClicked(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onSongClick:item="+item);

        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getMediaId(), null);

        mSelectedItemPublishSubject.onNext(item);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh:called");
        //todo implement
        mMvpView.stopLoading();
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
        mMvpView.displayList(items);
    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {
        Log.e(TAG, "onMediaBrowserSubscriptionError:called");
        mMvpView.displayToast(mContext.getResources().getString(R.string.unexpected_error_message));
    }

    @Override
    public void onSearchResult(List<MediaBrowserCompat.MediaItem> items) {
        //
    }
}
