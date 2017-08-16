package app.sonu.com.musicplayer.ui.miniplayer;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

import javax.inject.Named;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 9/8/17.
 */

public class MiniPlayerPresenter extends BasePresenter<MiniPlayerMvpView>
        implements MiniPlayerMvpPresenter, MediaBrowserManager.MediaBrowserCallback{

    private static final String TAG = MiniPlayerPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private PublishSubject<Integer> mMusicPlayerPanelPublishSubject;

    //todo temp
    private PlaybackStateCompat mLastPlaybackState;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    mMvpView.displaySong(
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE),
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
                    mMvpView.updateDuration(
                            metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    mLastPlaybackState = state;
                    if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        mMvpView.showPauseIcon();
                        mMvpView.scheduleSeekbarUpdate();
                    } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED
                            || state.getState() == PlaybackStateCompat.STATE_NONE
                            || state.getState() == PlaybackStateCompat.STATE_ERROR
                            || state.getState() == PlaybackStateCompat.STATE_STOPPED) {
                        mMvpView.showPlayIcon();
                        mMvpView.stopSeekbarUpdate();
                    }
                }
            };

    public MiniPlayerPresenter(DataManager dataManager,
                               MediaBrowserManager browserManager,
                               PublishSubject<Integer> musicPlayerPanelPublishSubject) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mMusicPlayerPanelPublishSubject = musicPlayerPanelPublishSubject;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        this.mContext = activity;
        mMediaBrowserManager.initMediaBrowser(activity);
    }

    @Override
    public void onCreateView() {
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onDestroy() {
        mMediaBrowserManager.disconnectMediaBrowser();
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

    }

    @Override
    public void onMediaBrowserConnectionSuspended() {

    }

    @Override
    public void onMediaBrowserConnectionFailed() {

    }

    @Override
    public void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items) {

    }

    @Override
    public void onMediaBrowserSubscriptionError(String id) {

    }
}
