package app.sonu.com.musicplayer.ui.allsongs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private PublishSubject<Integer> mAllSongsScrollToTopSubject;

    private Disposable mAllSongsScrollToTopDisposable;

    public AllSongsPresenter(DataManager dataManager,
                             MediaBrowserManager mediaBrowserManager,
                             AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAllSongsScrollToTopSubject = appBus.allSongsScrollToTopSubject;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        mAllSongsScrollToTopDisposable.dispose();
    }

    @Override
    public void onStart() {
        //nothing
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        mAllSongsScrollToTopDisposable = mAllSongsScrollToTopSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mMvpView.scrollListToTop();
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
    public void onSongClicked(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onSongClick:item="+item);

        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(item.getMediaId(), null);
    }

    @Override
    public void onShuffleAllClick() {
        List<MediaBrowserCompat.MediaItem> songsList = mMediaBrowserManager.getItemList();
        int randomIndex = new Random().nextInt(songsList.size());
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(songsList.get(randomIndex).getMediaId(), null);

        if (!mMediaBrowserManager.getMediaController().isShuffleModeEnabled()) {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleModeEnabled(true);
        }
    }

    @Override
    public void onAddToPlaylistClick(MediaBrowserCompat.MediaItem item) {
        mMvpView.showAddToPlaylistsDialog(
                MediaIdHelper.getSongIdFromMediaId(item.getDescription().getMediaId()));
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
        // do nothing
    }
}
