package app.sonu.com.musicplayer.ui.artist;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.album.AlbumPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsMvpView;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 21/8/17.
 */

public class ArtistPresenter extends BasePresenter<ArtistMvpView>
        implements ArtistMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = ArtistPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private PublishSubject<MediaBrowserCompat.MediaItem> mSelectedItemPublishSubject;
    private MediaBrowserCompat.MediaItem mMediaItem;

    public ArtistPresenter(DataManager dataManager,
                           MediaBrowserManager mediaBrowserManager,
                           PublishSubject<MediaBrowserCompat.MediaItem> selectedItemPublishSubject) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mSelectedItemPublishSubject = selectedItemPublishSubject;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart:called");
        String iconPath =  mMediaItem.getDescription().getIconUri() != null
                ? mMediaItem.getDescription().getIconUri().getEncodedPath()
                : null;

        mMvpView.displayArtistData(
                mMediaItem.getDescription().getTitle().toString(),
                mMediaItem.getDescription().getSubtitle().toString(),
                iconPath);
    }

    @Override
    public void onCreate(FragmentActivity activity, MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onCreate:called");
        Log.i(TAG, "onCreate:mediaId="+item.getDescription().getMediaId());

        mMediaItem = item;
        mContext = activity;

        Log.i(TAG, "onCreate:is mvpview null="+(mMvpView==null));

        //init media browser
        // mediaid should be set before calling initmediabrowser
        mMediaBrowserManager.setMediaId(item.getDescription().getMediaId());
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

        mSelectedItemPublishSubject.onNext(item);
    }

    @Override
    public void onDragDismissed() {
        mMvpView.closeFragment();
    }

    @Override
    public void onBackIbClick() {
        mMvpView.closeFragment();
    }

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
