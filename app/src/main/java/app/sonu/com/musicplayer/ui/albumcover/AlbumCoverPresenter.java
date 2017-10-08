package app.sonu.com.musicplayer.ui.albumcover;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.mediaplayer.playback.Playback;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.LogHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 4/10/17.
 */

public class AlbumCoverPresenter extends BasePresenter<AlbumCoverMvpView>
        implements AlbumCoverMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = LogHelper.getLogTag(AlbumCoverPresenter.class);

    private Context mContext;
    private MediaBrowserManager mMediaBrowserManager;
    private AppBus mAppBus;

    private Disposable mQueueIndexUpdatedDisposable;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback() {
                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    Log.d(TAG, "onQueueChanged:called");
                    Log.i(TAG, "onQueueChanged:new queue="+queue);
                    updateQueue(queue);
                }
            };

    public AlbumCoverPresenter(DataManager dataManager,
                               MediaBrowserManager browserManager,
                               AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mQueueIndexUpdatedDisposable.dispose();
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");

        mContext = activity;

        mMediaBrowserManager.initMediaBrowser(activity);

        mQueueIndexUpdatedDisposable = mAppBus.queueIndexUpdatedSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (!mMvpView.updateQueueIndex(integer)) {
                    updateQueue(mMediaBrowserManager.getMediaController().getQueue());
                }
            }
        });
    }

    @Override
    public void onCreateView() {
        Log.d(TAG, "onCreateView:called");
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onPageSelected(MediaSessionCompat.QueueItem item) {
        Log.d(TAG, "onPageSelected:called");
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .skipToQueueItem(item.getQueueId());
    }

    private void updateQueue(List<MediaSessionCompat.QueueItem> queue) {
        if (queue == null) {
            return;
        }

        mMvpView.displayQueue(queue);
    }

    //media browser implementations
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

        updateQueue(mMediaBrowserManager.getMediaController().getQueue());

        Bundle b = mMediaBrowserManager.getMediaController().getPlaybackState().getExtras();
        if (b != null) {
            mMvpView.updateQueueIndex(b.getInt(Playback.PLAYBACK_STATE_EXTRA_CURRENT_QUEUE_INDEX));
        }
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
