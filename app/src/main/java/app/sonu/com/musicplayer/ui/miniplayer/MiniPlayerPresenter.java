package app.sonu.com.musicplayer.ui.miniplayer;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 9/8/17.
 */

public class MiniPlayerPresenter extends BasePresenter<MiniPlayerMvpView>
        implements MiniPlayerMvpPresenter, MediaBrowserManager.MediaBrowserCallback{

    private static final String TAG = MiniPlayerPresenter.class.getSimpleName();

    private Context mContext;
    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<Integer> mMusicPlayerPanelPublishSubject;

    private PlaybackStateCompat mLastPlaybackState;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    displayMetadata(metadata);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    mLastPlaybackState = state;
                    updatePlaybackState();
                }

                @Override
                public void onShuffleModeChanged(boolean enabled) {
                    updateShuffleMode(enabled);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    updateRepeatMode(repeatMode);
                }
            };

    public MiniPlayerPresenter(DataManager dataManager,
                               MediaBrowserManager browserManager,
                               AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mMusicPlayerPanelPublishSubject = appBus.musicPlayerPanelStateSubject;
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

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);
    }

    @Override
    public void onCreateView() {
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void playPauseButtonOnClick() {
        int pbState = mMediaBrowserManager.getMediaController().getPlaybackState().getState();
        Log.i(TAG, "playPauseButtonOnClick:pbState="+pbState+PlaybackStateCompat.STATE_PLAYING);
        if (pbState == PlaybackStateCompat.STATE_PLAYING) {
            mMediaBrowserManager.getMediaController().getTransportControls().pause();
        } else {
            mMediaBrowserManager.getMediaController().getTransportControls().play();
        }
    }

    @Override
    public void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mMvpView.setSeekBarPosition((int) currentPosition);
    }

    @Override
    public void onNavUpClick() {
        mMusicPlayerPanelPublishSubject.onNext(1);
    }

    //media browser manager implementations
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

        mLastPlaybackState = mMediaBrowserManager.getMediaController().getPlaybackState();
        updatePlaybackState();
        displayMetadata(mMediaBrowserManager.getMediaController().getMetadata());
        updateShuffleMode(mMediaBrowserManager.getMediaController().isShuffleModeEnabled());
        updateRepeatMode(mMediaBrowserManager.getMediaController().getRepeatMode());
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

    private void displayMetadata(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        mMvpView.displaySong(
                MediaMetadataHelper.getSongDisplayTitle(metadata),
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        mMvpView.updateDuration(
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    private void updatePlaybackState() {
        if (mLastPlaybackState == null) {
            return;
        }
        if (mLastPlaybackState .getState() == PlaybackStateCompat.STATE_PLAYING) {
            mMvpView.showPauseIcon();
            mMvpView.scheduleSeekbarUpdate();
        } else if (mLastPlaybackState .getState() == PlaybackStateCompat.STATE_PAUSED) {
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();
        } else if (mLastPlaybackState .getState() == PlaybackStateCompat.STATE_NONE
                || mLastPlaybackState .getState() == PlaybackStateCompat.STATE_ERROR
                || mLastPlaybackState .getState() == PlaybackStateCompat.STATE_STOPPED) {
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();
            mMvpView.resetSeekbar();
        }
    }

    private void updateShuffleMode(boolean enabled) {
        if (enabled) {
            mMvpView.setShuffleModeEnabled();
        } else {
            mMvpView.setShuffleModeDisabled();
        }
    }

    private void updateRepeatMode(int repeatMode) {
        switch (repeatMode) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                mMvpView.setRepeatModeNone();
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                mMvpView.setRepeatModeAll();
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                mMvpView.setRepeatModeOne();
        }
    }
}
