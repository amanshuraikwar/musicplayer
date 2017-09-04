package app.sonu.com.musicplayer.mediaplayernew;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.BusModule;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaNotificationManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.PlaybackManager;
import app.sonu.com.musicplayer.mediaplayernew.manager.QueueManager;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.LocalMusicSource;
import app.sonu.com.musicplayer.mediaplayernew.playback.LocalPlayback;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 27/7/17.
 */

public class MusicService extends MediaBrowserServiceCompat
        implements PlaybackManager.PlaybackServiceCallback{

    private static final String TAG = MusicService.class.getSimpleName();

    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "app.sonu.com.musicplayer.ACTION_CMD";
    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";

    private MediaSessionCompat mMediaSession;

    private MusicProvider mMusicProvider;
    private PlaybackManager mPlaybackManager;

    private MediaNotificationManager mMediaNotificationManager;

    @Inject
    DataManager mDataManager;

    @Inject
    @Named(BusModule.PROVIDER_QUEUE_INDEX_UPDATED)
    PublishSubject<Integer> mQueueIndexUpdatedSubject;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:called");

        DaggerUiComponent.builder()
                .uiModule(new UiModule(this))
                .applicationComponent(((MyApplication)getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        mMusicProvider = new MusicProvider(new LocalMusicSource(mDataManager));

        mMediaSession = new MediaSessionCompat(this, TAG);

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        QueueManager queueManager = new QueueManager(mMusicProvider,
                new QueueManager.MetadataUpdateListener() {
                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        Log.d(TAG, "onMetadataChanged:called");
                        Log.i(TAG, "onMetadataChanged:metadata="+metadata);
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
                        mQueueIndexUpdatedSubject.onNext(currentIndex);
                    }
                });

        LocalPlayback playback = new LocalPlayback(this, mMusicProvider);

        mPlaybackManager = new PlaybackManager(this, mMusicProvider, queueManager, playback);

        mMediaSession.setCallback(mPlaybackManager.getMediaSessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        try {
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
                if (rootHint != null) {
                    switch (rootHint) {
                        case MediaIdHelper.ALL_SONGS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ALL_SONGS, null);
                        case MediaIdHelper.ALBUMS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ALBUMS, null);
                        case MediaIdHelper.ARTISTS_ROOT_HINT:
                            return new BrowserRoot(MediaIdHelper.MEDIA_ID_ARTISTS, null);
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
                               @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren: parentMediaId="+parentMediaId);
        if (MediaIdHelper.MEDIA_ID_EMPTY_ROOT.equals(parentMediaId)) {
            result.sendResult(new ArrayList<MediaBrowserCompat.MediaItem>());
        } else if (mMusicProvider.isInitialized()) {
            // if music library is ready, return immediately
            result.sendResult(mMusicProvider.getChildren(parentMediaId));
        } else {
            // otherwise, only return results when the music library is retrieved
            result.detach();
            mMusicProvider.retrieveMediaAsync(new MusicProvider.Callback() {
                @Override
                public void onMusicCatalogReady(boolean success) {
                    result.sendResult(mMusicProvider.getChildren(parentMediaId));
                }
            });
        }
    }

    @Override
    public void onSearch(@NonNull String query,
                         Bundle extras,
                         @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren:called");
        result.sendResult(mMusicProvider.getSongsBySearchQuery(query));
    }

    private boolean allowBrowsing(String clientPackageName ,int clientUid) {
        return clientPackageName.contains("android.sonu.com.musicplayer");
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
}
