package app.sonu.com.musicplayer.ui.playbackcontrols;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.mediaplayer.playback.Playback;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsSource;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.DurationUtil;
import app.sonu.com.musicplayer.util.LogHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 5/10/17.
 */

public class PlaybackControlsPresenter extends BasePresenter<PlaybackControlsMvpView>
        implements PlaybackControlsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = LogHelper.getLogTag(PlaybackControlsPresenter.class);

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private PlaybackStateCompat mLastPlaybackState;
    private MediaMetadataCompat mMetadata;
    private PerSlidingUpPanelBus mSlidingUpPanelBus;

    private Disposable bottomHalfPanelStateDisposable;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    mMetadata = metadata;
                    displayMetadata(metadata);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    mLastPlaybackState = state;
                    updatePlaybackState();
                }

                @Override
                public void onShuffleModeChanged(int mode) {
                    Log.d(TAG, "onShuffleModeChanged:called");
                    updateShuffleMode(mode);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.d(TAG, "onRepeatModeChanged:called");
                    updateRepeatMode(repeatMode);
                }
            };

    public PlaybackControlsPresenter(DataManager dataManager,
                                     MediaBrowserManager browserManager,
                                     AppBus appBus,
                                     PerSlidingUpPanelBus slidingUpPanelBus) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mSlidingUpPanelBus = slidingUpPanelBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();

        bottomHalfPanelStateDisposable.dispose();
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

        bottomHalfPanelStateDisposable =
                mSlidingUpPanelBus.bottomHalfPanelStateChangedSubject.subscribe(new Consumer<SlidingUpPanelLayout.PanelState>() {
                    @Override
                    public void accept(SlidingUpPanelLayout.PanelState state) throws Exception {
                        if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            mMvpView.showMiniAlbumArt();
                            mMvpView.setMetadataGravityStart();
                        }

                        if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            mMvpView.hideMiniAlbumArt();
                            mMvpView.setMetadataGravityCenter();
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
    public void skipNextButtonOnClick() {
        Log.d(TAG, "skipNextIb onClick:called");
        mMediaBrowserManager.getMediaController().getTransportControls().skipToNext();
    }

    @Override
    public void skipPreviousButtonOnClick() {
        Log.d(TAG, "skipPreviousIb onClick:called");
        mMediaBrowserManager.getMediaController().getTransportControls().skipToPrevious();
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
        mMvpView.setElapsedTime((int) currentPosition);
    }

    @Override
    public void onSeekbarStopTrackingTouch(int progress) {
        Log.d(TAG, "onSeekbarStopTrackingTouch:called progress="+progress);
        mMediaBrowserManager.getMediaController()
                .getTransportControls().seekTo(progress);
    }

    @Override
    public void onShuffleButtonClick() {

        Log.w(TAG, "shuffle mode got on click="
                +mMediaBrowserManager.getMediaController().getShuffleMode());

        if (mMediaBrowserManager.getMediaController().getShuffleMode() == 1) {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleMode(0);
        } else {
            mMediaBrowserManager
                    .getMediaController()
                    .getTransportControls()
                    .setShuffleMode(1);
        }
    }

    @Override
    public void onRepeatButtonClick() {
        int curRepeatMode = mMediaBrowserManager.getMediaController().getRepeatMode();
        switch (curRepeatMode) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                mMediaBrowserManager
                        .getMediaController()
                        .getTransportControls()
                        .setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                break;
        }
    }

    @Override
    public void addToPlaylistIvClick() {
        mMvpView.showAddToPlaylistsDialog(
                mMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
    }

    @Override
    public void onDarkColorChanged(int oldColor, int newColor) {
        mSlidingUpPanelBus.darkColorChangedSubject.onNext(new Pair<>(oldColor, newColor));
    }

    @Override
    public void onLightColorChanged(int oldColor, int newColor) {
        mSlidingUpPanelBus.lightColorChangedSubject.onNext(new Pair<>(oldColor, newColor));
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

        mLastPlaybackState = mMediaBrowserManager.getMediaController().getPlaybackState();
        updatePlaybackState();
        mMetadata = mMediaBrowserManager.getMediaController().getMetadata();
        displayMetadata(mMetadata);

        updateShuffleMode(mMediaBrowserManager.getMediaController().getShuffleMode());
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
                MediaMetadataHelper.getSongDisplaySubtitle(metadata),
                DurationUtil.getFormattedDuration(
                        metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)),
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        mMvpView.updateDuration(
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    private void updatePlaybackState() {
        if (mLastPlaybackState == null) {
            return;
        }
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mMvpView.showPauseIcon();
            mMvpView.scheduleSeekbarUpdate();
        } else if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();

            //to set the seekbar to correct position if somehow paused at
            //a different position than shown on the ui
            updateProgress();
        } else if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_NONE
                || mLastPlaybackState.getState() == PlaybackStateCompat.STATE_ERROR
                || mLastPlaybackState.getState() == PlaybackStateCompat.STATE_STOPPED){
            mMvpView.showPlayIcon();
            mMvpView.stopSeekbarUpdate();
            mMvpView.resetSeekbar();
        }
    }

    private void updateShuffleMode(int mode) {
        if (mode == 1) {
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
