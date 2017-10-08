package app.sonu.com.musicplayer.ui.addsongstoplaylists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.View;

import java.util.List;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 23/9/17.
 */

public class AddSongsToPlaylistsPresenter extends BasePresenter<AddSongsToPlaylistsMvpView>
        implements AddSongsToPlaylistsMvpPresenter, MediaBrowserManager.MediaBrowserCallback {

    private static final String TAG = AddSongsToPlaylistsPresenter.class.getSimpleName();

    private MediaBrowserManager mMediaBrowserManager;
    private Context mContext;
    private String songId;
    private Bundle mediaBrowserSubscribeExtras;
    private AppBus mAppBus;
    private Disposable playlistUpdatedDisposable, playlistsUpdatedDisposable,
            playlistAddedDisposable, playlistRemovedDisposable;

    public AddSongsToPlaylistsPresenter(DataManager dataManager,
                                        MediaBrowserManager mediaBrowserManager,
                                        AppBus appBus) {
        super(dataManager);
        mMediaBrowserManager = mediaBrowserManager;
        mMediaBrowserManager.setCallback(this);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        mMediaBrowserManager.disconnectMediaBrowser();
        playlistUpdatedDisposable.dispose();
        playlistsUpdatedDisposable.dispose();
        playlistAddedDisposable.dispose();
        playlistRemovedDisposable.dispose();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate(FragmentActivity activity) {
        Log.d(TAG, "onCreate:called");
        mContext = activity;

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
                        mMediaBrowserManager.subscribeMediaBrowser(mediaBrowserSubscribeExtras);
                        // mediaitem does not get boolean to tell if song is in playlist
                        // todo add dynamic list support
//                        mMvpView.addPlaylistToRv(item);
                    }
                });

        playlistRemovedDisposable =
                mAppBus.playlistRemovedSubject.subscribe(new Consumer<MediaBrowserCompat.MediaItem>() {
                    @Override
                    public void accept(MediaBrowserCompat.MediaItem item) throws Exception {
                        mMediaBrowserManager.subscribeMediaBrowser(mediaBrowserSubscribeExtras);
                        // mediaitem does not get boolean to tell if song is in playlist
                        // todo add dynamic list support
//                        mMvpView.removePlaylistFromRv(item);
                    }
                });

        //init media browser
        mMediaBrowserManager.initMediaBrowser(activity);
    }

    private void handlePlaylistUpdate(PlaylistUpdate playlistUpdate) {
        if (playlistUpdate.areSongsAdded()) {
            for (MediaMetadataCompat metadata : playlistUpdate.getAddedSongList()) {
                if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        .equals(songId)) {
                    // todo add dynamic list support
//                    mMvpView.setPlaylistChecked(playlistUpdate.getMediaId());
                }
            }
        }

        if (playlistUpdate.areSongRemoved()) {
            for (MediaMetadataCompat metadata : playlistUpdate.getRemovedSongList()) {
                if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        .equals(songId)) {
                    // todo add dynamic list support
//                    mMvpView.setPlaylistUnchecked(playlistUpdate.getMediaId());
                }
            }
        }

        // todo add dynamic list support
        mMediaBrowserManager.subscribeMediaBrowser(mediaBrowserSubscribeExtras);
    }

    @Override
    public void onCreateView(String songId) {
        Log.d(TAG, "onCreateView:called");

        this.songId = songId;

        //check if media browser is already connected or not
        if (mMediaBrowserManager.isMediaBrowserConnected()) {
            mMvpView.displayList(mMediaBrowserManager.getItemList());
        } else {
            mMediaBrowserManager.connectMediaBrowser();
        }
    }

    @Override
    public void onDoneBtnClick() {
        mMvpView.close();
    }

    @Override
    public void onCreateNewPlaylistBtnClick() {
        mMvpView.startCreatePlaylistFragment();
    }

    @Override
    public void onPlaylistClick(MediaBrowserCompat.MediaItem playlist) {
        Bundle extras = new Bundle();
        extras.putString(MusicService.KEY_PLAYLIST_MEDIA_ID,
                playlist.getDescription().getMediaId());
        extras.putString(MusicService.KEY_SONG_ID, songId);

        Bundle playlistExtras = playlist.getDescription().getExtras();

        if (playlistExtras == null) {
            Log.e(TAG, "onPlaylistClick:extras is null");
            return;
        }


        if (playlistExtras.getBoolean(MediaMetadataHelper.CUSTOM_METADATA_KEY_IS_SONG_IN_PLAYLIST)) {
            mMediaBrowserManager
                    .getMediaController()
                    .sendCommand(MusicService.CMD_REMOVE_SONG_FROM_PLAYLIST, extras, null);
        } else {
            mMediaBrowserManager
                    .getMediaController()
                    .sendCommand(MusicService.CMD_ADD_SONG_TO_PLAYLIST, extras, null);
        }
    }

    // media browser callback
    @Override
    public void onMediaBrowserConnected() {
        Log.d(TAG, "onMediaBrowserConnected:called");
        Bundle extras = new Bundle();
        extras.putString(MusicService.KEY_SONG_ID, songId);

        mediaBrowserSubscribeExtras = extras;

        mMediaBrowserManager.subscribeMediaBrowser(extras);
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
