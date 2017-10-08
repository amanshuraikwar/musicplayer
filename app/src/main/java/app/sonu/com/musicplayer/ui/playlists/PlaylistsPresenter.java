package app.sonu.com.musicplayer.ui.playlists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 5/9/17.
 */

public class PlaylistsPresenter extends BasePresenter<PlaylistsMvpView>
        implements PlaylistsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = PlaylistsPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private AppBus mAppBus;

    private Disposable mPlaylistsScrollToTopDisposable, playlistUpdatedDisposable, playlistsUpdatedDisposable,
            playlistAddedDisposable, playlistRemovedDisposable;;

    public PlaylistsPresenter(DataManager dataManager,
                              MediaBrowserManager mediaBrowserManager,
                              AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach:called");
        mMediaBrowserManager.disconnectMediaBrowser();
        mPlaylistsScrollToTopDisposable.dispose();
        playlistUpdatedDisposable.dispose();
        playlistsUpdatedDisposable.dispose();
        playlistAddedDisposable.dispose();
        playlistRemovedDisposable.dispose();
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);

        mPlaylistsScrollToTopDisposable = mAppBus.playlistsScrollToTopSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mMvpView.scrollListToTop();
            }
        });

        playlistUpdatedDisposable =
                mAppBus.playlistUpdatedSubject.subscribe(new Consumer<PlaylistUpdate>() {
                    @Override
                    public void accept(PlaylistUpdate playlistUpdate) throws Exception {
                        handlePlaylistUpdate(playlistUpdate);
                    }
                });

        playlistsUpdatedDisposable =
                mAppBus.playlistsUpdatedSubject.subscribe(new Consumer<List<PlaylistUpdate>>() {
                    @Override
                    public void accept(List<PlaylistUpdate> playlistUpdateList) throws Exception {
                        for (PlaylistUpdate playlistUpdate : playlistUpdateList) {
                            handlePlaylistUpdate(playlistUpdate);
                        }
                    }
                });

        playlistAddedDisposable =
                mAppBus.playlistAddedSubject.subscribe(new Consumer<MediaBrowserCompat.MediaItem>() {
                    @Override
                    public void accept(MediaBrowserCompat.MediaItem item) throws Exception {
                        mMediaBrowserManager.subscribeMediaBrowser();
                        // mediaitem does not get boolean to tell if song is in playlist
                        // todo add dynamic list support
//                        mMvpView.addPlaylistToRv(item);
                    }
                });

        playlistRemovedDisposable =
                mAppBus.playlistRemovedSubject.subscribe(new Consumer<MediaBrowserCompat.MediaItem>() {
                    @Override
                    public void accept(MediaBrowserCompat.MediaItem item) throws Exception {
                        mMediaBrowserManager.subscribeMediaBrowser();
                        // mediaitem does not get boolean to tell if song is in playlist
                        // todo add dynamic list support
//                        mMvpView.removePlaylistFromRv(item);
                    }
                });
    }

    private void handlePlaylistUpdate(PlaylistUpdate playlistUpdate) {
        // todo add dynamic list support
        mMediaBrowserManager.subscribeMediaBrowser();
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
    public void onPlaylistClicked(MediaBrowserCompat.MediaItem item, View animatingView) {
        Log.d(TAG, "onPlaylistClicked:currentAlbum=" + item);
        mAppBus.playlistClickSubject.onNext(new Pair<>(item, animatingView));
    }

    @Override
    public void onAddPlaylistBtnClick() {
        Log.d(TAG, "onAddPlaylistBtnClick:called");
        mMvpView.showCreatePlaylistDialog();
    }

    @Override
    public void onDeletePlaylistClick(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onDeletePlaylistClick:called");
        Log.i(TAG, "onDeletePlaylistClick:mediaId="+item.getDescription().getMediaId());
        if (item != null) {
            Bundle b = new Bundle();
            b.putString(MusicService.KEY_PLAYLIST_MEDIA_ID, item.getDescription().getMediaId());
            mMediaBrowserManager
                    .getMediaController()
                    .sendCommand(MusicService.CMD_DELETE_PLAYLIST, b, null);
        }
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
