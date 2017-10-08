package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 14/9/17.
 */

public class MusicPlayerHolderPresenter<MvpView extends MusicPlayerHolderMvpView>
        extends BasePresenter<MvpView>
        implements MusicPlayerHolderMvpPresenter<MvpView>, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = MusicPlayerHolderPresenter.class.getSimpleName();

    protected MediaBrowserManager mMediaBrowserManager;
    protected Context mContext;
    private AppBus mAppBus;
    private PerSlidingUpPanelBus mSlidingUpPanelBus;

    private Disposable musicPlayerPanelStateDisposable, setAntiDragViewDisposable, setSuplDisposable;

    private final MediaBrowserManager.MediaControllerCallback mMediaControllerCallback =
            new MediaBrowserManager.MediaControllerCallback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged:state="+state);
                    if (state.getState() == PlaybackStateCompat.STATE_PAUSED
                            || state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        if (mMvpView.isSlidingUpPaneHidden()) {
                            mMvpView.collapseSlidingUpPanelLayout();
                        }
                    }
                }
            };

    public MusicPlayerHolderPresenter(DataManager dataManager,
                                      MediaBrowserManager mediaBrowserManager,
                                      AppBus appBus,
                                      PerSlidingUpPanelBus slidingUpPanelBus) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mMediaBrowserManager.setControllerCallback(mMediaControllerCallback);
        mAppBus = appBus;
        mSlidingUpPanelBus = slidingUpPanelBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called"+mContext);
        musicPlayerPanelStateDisposable.dispose();
        setAntiDragViewDisposable.dispose();
        setSuplDisposable.dispose();
        mMediaBrowserManager.disconnectMediaBrowser();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called"+activity);
        mContext = activity;

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        //check if media browser is already connected or not
        if (!mMediaBrowserManager.isMediaBrowserConnected()) {
            mMediaBrowserManager.connectMediaBrowser();
        }

        musicPlayerPanelStateDisposable =
                mAppBus.musicPlayerPanelStateSubject.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        switch (integer) {
                            case -1:
                                mMvpView.hideSlidingUpPanelLayout();
                                break;
                            case 0:
                                mMvpView.collapseSlidingUpPanelLayout();
                                break;
                            case 1:
                                mMvpView.expandSlidingUpPanelLayout();
                                break;
                        }
                    }
                });

        setAntiDragViewDisposable =
                mSlidingUpPanelBus.setAntidragViewSubject.subscribe(new Consumer<View>() {
                    @Override
                    public void accept(View view) throws Exception {
                        mMvpView.setAntiDragView(view);
                    }
                });

        setSuplDisposable =
                mSlidingUpPanelBus.setSuplSubject.subscribe(new Consumer<SlidingUpPanelLayout>() {
                    @Override
                    public void accept(SlidingUpPanelLayout slidingUpPanelLayout) throws Exception {
                        mMvpView.setSupl(slidingUpPanelLayout);
                    }
                });
    }

    @Override
    public void onMusicPlayerPanelStateChanged(SlidingUpPanelLayout.PanelState state) {
        mAppBus.musicPlayerPanelStateChangedSubject.onNext(state);
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

        PlaybackStateCompat playbackState =
                mMediaBrowserManager.getMediaController().getPlaybackState();

        if (playbackState != null) {
            if (playbackState.getState() == PlaybackStateCompat.STATE_PAUSED
                    || playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                if (mMvpView.isSlidingUpPaneHidden()) {
                    mMvpView.collapseSlidingUpPanelLayout();
                }
            }
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
