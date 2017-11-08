package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;

import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.mediaplayer.playback.Playback;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsSource;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerPresenter extends BasePresenter<MusicPlayerMvpView>
        implements MusicPlayerMvpPresenter, MediaBrowserManager.MediaBrowserCallback{

    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();

    private PerSlidingUpPanelBus mSlidingUpPanelBus;

    private MediaBrowserManager mMediaBrowserManager;
    private PublishSubject<Integer> mMusicPlayerPanelPublishSubject;

    private Context mContext;

    private PlaybackStateCompat mLastPlaybackState;
    private MediaMetadataCompat mMetadata;

    private Disposable setUmanoScrollViewDisposable;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback(){
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged:called");
                    mMetadata = metadata;

                    mMvpView.metadataChanged();
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    mLastPlaybackState = state;
                    updatePlaybackState();
                }
            };

    public MusicPlayerPresenter(DataManager dataManager,
                                MediaBrowserManager browserManager,
                                AppBus appBus,
                                PerSlidingUpPanelBus slidingUpPanelBus) {
        super(dataManager);
        mMediaBrowserManager = browserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mMusicPlayerPanelPublishSubject = appBus.musicPlayerPanelStateSubject;
        mSlidingUpPanelBus = slidingUpPanelBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        setUmanoScrollViewDisposable.dispose();
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

        setUmanoScrollViewDisposable =
                mSlidingUpPanelBus.setScrollViewSubject.subscribe(new Consumer<RecyclerView>() {
                    @Override
                    public void accept(RecyclerView recyclerView) throws Exception {
                        mMvpView.setUmanoScrollView(recyclerView);
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
    public void onHeartIvClick() {
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_SONG_ID, mMetadata.getDescription().getMediaId());
        mMediaBrowserManager
                .getMediaController()
                .sendCommand(MusicService.CMD_FAVOURITES, b, null);
    }

    @Override
    public void setAntiDragView(View view) {
        mSlidingUpPanelBus.setAntidragViewSubject.onNext(view);
    }

    @Override
    public void setSupl(SlidingUpPanelLayout supl) {
        mSlidingUpPanelBus.setSuplSubject.onNext(supl);
    }

    @Override
    public void onCollapseIvClick() {
        mMusicPlayerPanelPublishSubject.onNext(0);
    }

    @Override
    public void onBottomHalfPanelStateChanged(SlidingUpPanelLayout.PanelState state) {
        mSlidingUpPanelBus.bottomHalfPanelStateChangedSubject.onNext(state);
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

        mMvpView.metadataChanged();
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

    private void updatePlaybackState() {
        if (mLastPlaybackState.getExtras()!=null) {
            if (mLastPlaybackState
                    .getExtras()
                    .getBoolean(PlaylistsSource.PLAYBACK_STATE_EXTRA_IS_IN_FAVORITES)) {
                mMvpView.showFavButtonEnabled();
            } else {
                mMvpView.showFavButtonDisabled();
            }
        } else {
            mMvpView.showFavButtonDisabled();
        }
    }
}
