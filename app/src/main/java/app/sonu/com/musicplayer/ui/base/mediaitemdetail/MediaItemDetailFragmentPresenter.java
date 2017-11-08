package app.sonu.com.musicplayer.ui.base.mediaitemdetail;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.MediaIdHelper;

/**
 * Created by sonu on 21/9/17.
 */

public class MediaItemDetailFragmentPresenter<MvpView extends MediaItemDetailFragmentMvpView>
        extends BasePresenter<MvpView>
        implements MediaItemDetailFragmentMvpPresenter<MvpView>, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = MediaItemDetailFragmentPresenter.class.getSimpleName();

    protected MediaBrowserManager mMediaBrowserManager;
    protected Context mContext;
    private MediaBrowserCompat.MediaItem mMediaItem;

    public MediaItemDetailFragmentPresenter(DataManager dataManager,
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
    public void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onCreate:called");
        Log.i(TAG, "onCreate:mediaId="+item.getDescription().getMediaId());
        mContext = activity;

        mMediaItem = item;

        //init media browser
        // mediaid should be set before calling initMediaBrowser
        mMediaBrowserManager.setMediaId(item.getDescription().getMediaId());
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
    public void onAttach(MvpView mvpView) {
        super.onAttach(mvpView);

        String artPath = null;
        if (mMediaItem.getDescription().getIconUri() != null) {
            artPath = mMediaItem.getDescription().getIconUri().getEncodedPath();
        }
        mMvpView.displayMetadata(
                mMediaItem.getDescription().getTitle().toString(),
                mMediaItem.getDescription().getSubtitle().toString(),
                artPath);
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
    public void onBackIbClick() {
        mMvpView.close();
    }

    @Override
    public void onShuffleAllClick() {
        List<MediaBrowserCompat.MediaItem> songsList = mMediaBrowserManager.getItemList();
        if (songsList.size() <= 0) {
            return;
        }
        int randomIndex = new Random().nextInt(songsList.size());
        mMediaBrowserManager
                .getMediaController()
                .getTransportControls()
                .playFromMediaId(songsList.get(randomIndex).getMediaId(), null);
        if (!(mMediaBrowserManager.getMediaController().getShuffleMode() == 1)) {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleMode(1);
        }
    }

    @Override
    public MediaBrowserCompat.MediaItem getMediaItem() {
        return mMediaItem;
    }

    @Override
    public void onAddToPlaylistClick(MediaBrowserCompat.MediaItem item) {
        mMvpView.showAddToPlaylistsDialog(
                MediaIdHelper.getSongIdFromMediaId(item.getDescription().getMediaId()));
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
