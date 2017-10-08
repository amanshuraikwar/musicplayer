package app.sonu.com.musicplayer.ui.createplaylist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;

/**
 * Created by sonu on 23/9/17.
 */

public class CreatePlaylistPresenter extends BasePresenter<CreatePlaylistMvpView>
        implements CreatePlaylistMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = CreatePlaylistPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;

    public CreatePlaylistPresenter(DataManager dataManager,
                                   MediaBrowserManager mediaBrowserManager) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
    }

    @Override
    public void onDetach() {
        mMediaBrowserManager.disconnectMediaBrowser();
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
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onCreateBtnClick(String playlistTitle) {
        if (playlistTitle != null) {
            if (!playlistTitle.equals("")) {
                Bundle extras = new Bundle();
                extras.putString(MusicService.KEY_PLAYLIST_TITLE, playlistTitle);
                mMediaBrowserManager
                        .getMediaController()
                        .sendCommand(MusicService.CMD_CREATE_PLAYLIST, extras, null);
                mMvpView.close();

                return;
            }
        }

        mMvpView.displayToast("please enter a playlist title");
    }

    @Override
    public void onCancelBtnClick() {
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
