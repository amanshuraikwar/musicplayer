package app.sonu.com.musicplayer.mediaplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.mediaplayer.media.MediaProviderImpl;
import app.sonu.com.musicplayer.mediaplayer.playback.PlaybackManager;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsManager;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsManagerImpl;
import app.sonu.com.musicplayer.mediaplayer.media.LocalMediaSource;
import app.sonu.com.musicplayer.mediaplayer.media.MediaProviderSource;
import app.sonu.com.musicplayer.mediaplayer.playback.LocalPlayback;
import app.sonu.com.musicplayer.mediaplayer.playlist.LocalPlaylistsSource;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsSource;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;

/**
 * Created by sonu on 27/7/17.
 */

public class MusicService extends MediaBrowserServiceCompat
        implements PlaybackManager.PlaybackServiceCallback, PlaylistsManager.PlaylistsServiceCallback {

    private static final String TAG = MusicService.class.getSimpleName();

    // action of incoming intent to run a command
    public static final String ACTION_CMD = "app.sonu.com.musicplayer.ACTION_CMD";
    // key of incoming command in extras
    public static final String CMD_NAME = "CMD_NAME";

    // commands
    public static final String CMD_PAUSE = "CMD_PAUSE";
    public static final String CMD_FAVOURITES = "CMD_FAV";
    public static final String CMD_CREATE_PLAYLIST = "CMD_CREATE_PLAYLIST";
    public static final String CMD_DELETE_PLAYLIST = "CMD_DELETE_PLAYLIST";
    public static final String CMD_ADD_SONG_TO_PLAYLIST = "CMD_ADD_SONG_TO_PLAYLIST";
    public static final String CMD_REMOVE_SONG_FROM_PLAYLIST = "CMD_REMOVE_SONG_FROM_PLAYLIST";

    // keys of incoming commands' extras
    public static final String KEY_SONG_ID = "SONG_ID";
    public static final String KEY_MEDIA_ID = "MEDIA_ID";
    public static final String KEY_FAV_STATUS = "FAV_STATUS";
    public static final String KEY_PLAYLIST_TITLE = "PLAYLIST_TITLE";
    public static final String KEY_PLAYLIST_ID = "PLAYLIST_ID";
    public static final String KEY_PLAYLIST_MEDIA_ID = "PLAYLIST_MEDIA_ID";

    private MediaSessionCompat mMediaSession;
    private MediaProvider mMediaProvider;
    private PlaybackManager mPlaybackManager;
    private PlaylistsManager mPlaylistsManager;
    private MediaNotificationManager mMediaNotificationManager;

    @Inject
    DataManager mDataManager;

    @Inject
    AppBus mAppBus;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:called");

        // injecting dependencies
        ((MyApplication)getApplicationContext()).getApplicationComponent().inject(this);

        // local music source
        MediaProviderSource mediaProviderSource = new LocalMediaSource(mDataManager);

        // media provider with local media provider source
        mMediaProvider = new MediaProviderImpl(mediaProviderSource);

        // local playlists source
        PlaylistsSource playlistsSource = new LocalPlaylistsSource(mDataManager);

        // playlists manager with local playlists source
        mPlaylistsManager =
                new PlaylistsManagerImpl(playlistsSource, mMediaProvider,
                        this);

        mPlaylistsManager.retrievePlaylists();

        // media session
        mMediaSession = new MediaSessionCompat(this, TAG);

        // setting flags to media session
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        // queue manager with media provider and playlists manager
        QueueManager queueManager = new QueueManager(mMediaProvider,
                new QueueManager.MetadataUpdateListener() {
                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        Log.d(TAG, "onMetadataChanged:called");
                        Log.i(TAG, "onMetadataChanged:metadata=" + metadata);

                        if (metadata
                                .getString(
                                        MediaMetadataCompat
                                                .METADATA_KEY_ALBUM_ART_URI) == null) {
                            mMediaSession.setMetadata(metadata);
                            return;
                        }

                        // setting album art bitmap to metadata to show in lock screen
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(metadata
                                .getString(
                                        MediaMetadataCompat
                                                .METADATA_KEY_ALBUM_ART_URI),bmOptions);
                        metadata = MediaMetadataHelper.addAlbumArtBitmap(metadata, bitmap);
                        mMediaSession.setMetadata(metadata);


                    }

                    @Override
                    public void onMetadataRetrieveError() {
                        //todo implement
                    }

                    @Override
                    public void onQueueUpdated(String title,
                                               List<MediaSessionCompat.QueueItem> newQueue) {
                        Log.d(TAG, "onQueueUpdated:called");
                        Log.i(TAG, "onQueueUpdated:title="+title);
                        Log.i(TAG, "onQueueUpdated:queue="+newQueue);
                        mMediaSession.setQueue(newQueue);
                        mMediaSession.setQueueTitle(title);
                    }

                    @Override
                    public void onCurrentQueueIndexUpdated(int currentIndex) {
                        Log.d(TAG, "onCurrentQueueIndexUpdated:called");
                        mAppBus.queueIndexUpdatedSubject.onNext(currentIndex);
                    }
                }
                ,mPlaylistsManager);

        // local playback
        LocalPlayback playback = new LocalPlayback(this, mMediaProvider);

        // playback manager with queueManager, local playback, playlists manager
        mPlaybackManager = new PlaybackManager(this, queueManager, playback,
                mPlaylistsManager);

        mMediaSession.setCallback(new MediaSessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        try {
            // notification manager
            mMediaNotificationManager = new MediaNotificationManager(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create a MediaNotificationManager", e);
        }
    }

    @Override
    public int onStartCommand(Intent startIntent,int flags, int startId) {
        Log.d(TAG, "onStartCommand:called");

        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlaybackManager.handlePauseRequest();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!mPlaybackManager.isPlaying()) {
            // stop the service if the ui unbinds and the player is not playing
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:called");
        // Service is being killed, so make sure we release our resources
        mPlaybackManager.handleStopRequest();
        mMediaSession.release();
        mMediaNotificationManager.stopNotification();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 @Nullable Bundle rootHints) {

        Log.d(TAG, "onGetRoot:called");
        Log.i(TAG, "onGetRoot:clientUid="+clientUid);
        Log.i(TAG, "onGetRoot:clientPackageName="+clientPackageName);

        Log.i(TAG, "onGetRoot:rootHints="+rootHints);

        //noinspection ConstantConditions
        if (allowBrowsing(clientPackageName, clientUid)) {
            // checking root hint for appropriate root_id
            if (rootHints != null) {
                String rootHint =
                        rootHints.getString(getResources().getString(R.string.root_hint_key));
                Log.i(TAG, "onGetRoot:rootHint="+rootHint);
                if (rootHint != null) {
                    switch (rootHint) {
                        case MediaIdHelper.ALL_SONGS_ROOT_HINT:
                            Log.i(TAG, "onGetRoot:allSongs");
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ALL_SONGS, null);
                        case MediaIdHelper.ALBUMS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ALBUMS, null);
                        case MediaIdHelper.ARTISTS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ARTISTS, null);
                        case MediaIdHelper.PLAYLISTS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_PLAYLISTS, null);
                        case MediaIdHelper.PLAYLISTS_FOR_SONG_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_PLAYLISTS_FOR_SONG, null);
                        default:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_EMPTY_ROOT, null);
                    }
                } else {
                    // returning null
                    return new BrowserRoot(MediaIdHelper.MEDIA_ID_EMPTY_ROOT, null);
                }
            } else {
                // returning null
                return new BrowserRoot(MediaIdHelper.MEDIA_ID_EMPTY_ROOT, null);
            }
        } else {
            // Clients can connect, but since the BrowserRoot is an empty string
            // onLoadChildren will return nothing. This disables the ability to browse for content.
            return new BrowserRoot(MediaIdHelper.MEDIA_ID_EMPTY_ROOT, null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result,
                               @NonNull final Bundle options) {

        Log.d(TAG, "onLoadChildren:called");
        Log.i(TAG, "onLoadChildren:parentMediaId="+parentMediaId);

        if (MediaIdHelper.MEDIA_ID_EMPTY_ROOT.equals(parentMediaId)) {
            // empty root
            result.sendResult(new ArrayList<MediaBrowserCompat.MediaItem>());

        } else if (parentMediaId.startsWith(MediaIdHelper.MEDIA_ID_PLAYLISTS)) {
            // all playlists or song for a playlist
            if (mPlaylistsManager.isInitialized()) {
                result.sendResult(mPlaylistsManager.getChildren(parentMediaId));

            } else {
                result.detach();
                mPlaylistsManager.retrievePlaylistsAsync(new PlaylistsManager.Callback() {
                    @Override
                    public void onPlaylistCatalogReady(boolean success) {
                        result.sendResult(mPlaylistsManager.getChildren(parentMediaId));
                    }
                });

            }

        } else if (parentMediaId.startsWith(MediaIdHelper.MEDIA_ID_PLAYLISTS_FOR_SONG)) {
            // playlists for a song, including information song existing in playlists
            if (mPlaylistsManager.isInitialized()) {
                result.sendResult(mPlaylistsManager.getPlaylistsForSongId(
                        options.getString(MusicService.KEY_SONG_ID)));

            } else {
                result.detach();
                mPlaylistsManager.retrievePlaylistsAsync(new PlaylistsManager.Callback() {
                    @Override
                    public void onPlaylistCatalogReady(boolean success) {
                        result.sendResult(mPlaylistsManager.getPlaylistsForSongId(
                                options.getString(MusicService.KEY_MEDIA_ID)));
                    }
                });

            }
        } else {
            // all songs, albums, artists, songs for an album, songs for an artist
            if (mMediaProvider.isInitialized()) {
                result.sendResult(mMediaProvider.getChildren(parentMediaId));

            } else {
                result.detach();
                mMediaProvider.retrieveMediaAsync(new MediaProvider.Callback() {
                    @Override
                    public void onMusicCatalogReady(boolean success) {
                        result.sendResult(mMediaProvider.getChildren(parentMediaId));
                    }
                });

            }
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // do nothing
        // this is never called
        Log.w(TAG, "onLoadChildren(without options):called");
    }

    @Override
    public void onSearch(@NonNull String query,
                         Bundle extras,
                         @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren:called");
        result.sendResult(mMediaProvider.getItemsBySearchQuery(query));
    }

    private boolean allowBrowsing(String clientPackageName ,int clientUid) {
        return clientPackageName.contains("app.sonu.com.musicplayer");
    }

    //playback service callback
    @Override
    public void onPlaybackStart() {
        Log.d(TAG, "onPlaybackStart:called");
        mMediaSession.setActive(true);

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(new Intent(getApplicationContext(), MusicService.class));
    }

    @Override
    public void onNotificationRequired() {
        Log.d(TAG, "onNotificationRequired:called");
        // start notification of not already
        mMediaNotificationManager.startNotification();
    }

    @Override
    public void onPlaybackStop() {
        Log.d(TAG, "onPlaybackStop:called");
        mMediaSession.setActive(false);
        // stop foreground so that the notification can be dismissed
        stopForeground(false);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        Log.d(TAG, "onPlaybackStateUpdated:state="+newState);
        mMediaSession.setPlaybackState(newState);

    }

    @Override
    public void onShuffleModeChanged(int mode) {
        Log.d(TAG, "onShuffleModeChanged:mode="+mode);
        if (mode == 0) {
            mMediaSession.setShuffleModeEnabled(false);
        } else if (mode == 1) {
            mMediaSession.setShuffleModeEnabled(true);
        }
    }

    @Override
    public void onRepeatModeChanged(int mode) {
        Log.d(TAG, "onRepeatModeChanged:mode="+mode);
        mMediaSession.setRepeatMode(mode);
    }

    // playlist service callback
    @Override
    public void onPlaylistUpdated(PlaylistUpdate playlistUpdate) {
        if (playlistUpdate.getPlaylistId().equals(PlaylistsSource.FAVORITES_PLAYLIST_ID)) {
            mPlaybackManager.updatePlaybackState(null);
        }
        mAppBus.playlistUpdatedSubject.onNext(playlistUpdate);
    }

    @Override
    public void onPlaylistsUpdated(List<PlaylistUpdate> playlistUpdateList) {
        mAppBus.playlistsUpdatedSubject.onNext(playlistUpdateList);
    }

    @Override
    public void onPlaylistAdded(MediaBrowserCompat.MediaItem playlist) {
        mAppBus.playlistAddedSubject.onNext(playlist);
    }

    @Override
    public void onPlaylistRemoved(MediaBrowserCompat.MediaItem playlist) {
        mAppBus.playlistRemovedSubject.onNext(playlist);
    }

    // media session callback
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlaybackManager.getMediaSessionCallback().onPlay();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mPlaybackManager.getMediaSessionCallback().onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            mPlaybackManager.getMediaSessionCallback().onSkipToQueueItem(id);
        }

        @Override
        public void onPause() {
            mPlaybackManager.getMediaSessionCallback().onPause();
        }

        @Override
        public void onSkipToNext() {
            mPlaybackManager.getMediaSessionCallback().onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            mPlaybackManager.getMediaSessionCallback().onSkipToPrevious();
        }

        @Override
        public void onStop() {
            mPlaybackManager.getMediaSessionCallback().onStop();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlaybackManager.getMediaSessionCallback().onSeekTo(pos);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            mPlaybackManager.getMediaSessionCallback().onSetRepeatMode(repeatMode);
        }

        //todo deprecated in 26.0.0-beta-2
        @Override
        public void onSetShuffleModeEnabled(boolean enabled) {
            mPlaybackManager.getMediaSessionCallback().onSetShuffleModeEnabled(enabled);
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            String songId, playlistMediaId;
            switch (command) {
                case CMD_FAVOURITES:
                    songId = extras.getString(KEY_SONG_ID);
                    if (mPlaylistsManager.isSongIdInFavourites(songId)) {
                        mPlaylistsManager.removeSongIdFromFavorites(songId);
                    } else {
                        mPlaylistsManager.addSongIdToFavorites(songId);
                    }
                    break;
                case CMD_CREATE_PLAYLIST:
                    mPlaylistsManager.createPlaylist(extras.getString(KEY_PLAYLIST_TITLE));
                    break;
                case CMD_DELETE_PLAYLIST:
                    playlistMediaId = extras.getString(KEY_PLAYLIST_MEDIA_ID);
                    mPlaylistsManager.deletePlaylistByMediaId(playlistMediaId);
                    break;
                case CMD_ADD_SONG_TO_PLAYLIST:
                    playlistMediaId = extras.getString(KEY_PLAYLIST_MEDIA_ID);
                    songId = extras.getString(KEY_SONG_ID);
                    mPlaylistsManager.addSongIdToPlaylist(songId, playlistMediaId);
                    break;
                case CMD_REMOVE_SONG_FROM_PLAYLIST:
                    playlistMediaId = extras.getString(KEY_PLAYLIST_MEDIA_ID);
                    songId = extras.getString(KEY_SONG_ID);
                    mPlaylistsManager.removeSongIdFromPlaylist(songId, playlistMediaId);
                    break;
            }
        }
    }
}
