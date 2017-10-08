package app.sonu.com.musicplayer.ui.main;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.LogHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 15/9/17.
 */

public class MainPresenter extends BasePresenter<MainMvpView>
        implements MainMvpPresenter {

    private static final String TAG = LogHelper.getLogTag(MainPresenter.class);

    private AppBus mAppBus;

    private Disposable medialistSelectedDisposable,
            musicPlayerPanelStateChangedDisposable,
            albumClickDisposable, artistClickDisposable, playlistClickDisposable;

    public MainPresenter(DataManager dataManager,
                         AppBus appBus) {
        super(dataManager);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        medialistSelectedDisposable.dispose();
        musicPlayerPanelStateChangedDisposable.dispose();
        albumClickDisposable.dispose();
        artistClickDisposable.dispose();
        playlistClickDisposable.dispose();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart:called");
        Log.i(TAG, "onStart:is MvpView null="+(mMvpView==null));

        if (mDataManager.isFirstRun()) {
            mMvpView.startIntoActivity();
            mMvpView.close();
        }
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        medialistSelectedDisposable = mAppBus.medialistSelectedSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mMvpView.setNavigationItemSelected(integer);
            }
        });

        musicPlayerPanelStateChangedDisposable =
                mAppBus.musicPlayerPanelStateChangedSubject.subscribe(new Consumer<SlidingUpPanelLayout.PanelState>() {
                    @Override
                    public void accept(SlidingUpPanelLayout.PanelState panelState) throws Exception {
                        if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED ||
                                panelState == SlidingUpPanelLayout.PanelState.HIDDEN ) {
                            mMvpView.showNavigationBar();
                        } else if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED){
                            mMvpView.hideNavigationBar();
                        }
                    }
                });

        albumClickDisposable =
                mAppBus.albumClickSubject.subscribe(new Consumer<Pair<MediaBrowserCompat.MediaItem,
                        View>>() {
                    @Override
                    public void accept(Pair<MediaBrowserCompat.MediaItem, View> mediaItemViewPair)
                            throws Exception {
                        mMvpView.startAlbumActivity(mediaItemViewPair.first,
                                mediaItemViewPair.second);
                    }
                });

        artistClickDisposable =
                mAppBus.artistClickSubject.subscribe(new Consumer<Pair<MediaBrowserCompat.MediaItem, View>>() {
                    @Override
                    public void accept(Pair<MediaBrowserCompat.MediaItem, View> mediaItemViewPair) throws Exception {
                        mMvpView.startArtistActivity(mediaItemViewPair.first,
                                mediaItemViewPair.second);
                    }
                });

        playlistClickDisposable =
                mAppBus.playlistClickSubject.subscribe(new Consumer<Pair<MediaBrowserCompat.MediaItem, View>>() {
                    @Override
                    public void accept(Pair<MediaBrowserCompat.MediaItem, View> mediaItemViewPair) throws Exception {
                        mMvpView.startPlaylistActivity(mediaItemViewPair.first,
                                mediaItemViewPair.second);
                    }
                });
    }

    @Override
    public void onNavigationItemSelected(int itemId) {
        mAppBus.navigationItemSelectedSubject.onNext(itemId);
    }

    @Override
    public void onNavigationItemReselected(int itemId) {
        switch (itemId) {
            case R.id.menuItemSongs:
                mAppBus.allSongsScrollToTopSubject.onNext(1);
                break;
            case R.id.menuItemAlbums:
                mAppBus.albumsScrollToTopSubject.onNext(1);
                break;
            case R.id.menuItemArtists:
                mAppBus.artistsScrollToTopSubject.onNext(1);
                break;
            case R.id.menuItemPlaylists:
                mAppBus.playlistsScrollToTopSubject.onNext(1);
                break;
        }
    }

    @Override
    public void disableFirstRunFlag() {
        mDataManager.setFirstRun(false);
    }
}
