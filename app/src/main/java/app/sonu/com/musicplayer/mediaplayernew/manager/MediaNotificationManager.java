package app.sonu.com.musicplayer.mediaplayernew.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.mediaplayernew.MusicService;
import app.sonu.com.musicplayer.mediaplayernew.playback.Playback;
import app.sonu.com.musicplayer.ui.main.MainActivity;
import app.sonu.com.musicplayer.utils.ColorUtil;

/**
 * Created by sonu on 20/8/17.
 */

// todo study this class
public class MediaNotificationManager extends BroadcastReceiver {

    private static final String TAG = MediaNotificationManager.class.getSimpleName();

    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "app.sonu.com.musicplayer.pause";
    public static final String ACTION_PLAY = "app.sonu.com.musicplayer.play";
    public static final String ACTION_PREV = "app.sonu.com.musicplayer.prev";
    public static final String ACTION_NEXT = "app.sonu.com.musicplayer.next";
    public static final String ACTION_DELETE = "app.sonu.com.musicplayer.delete";
    public static final String ACTION_ADD_TO_PLAYLIST= "app.sonu.com.musicplayer.addtoplaylist";

    private final MusicService mService;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;

    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMetadata;

    private final NotificationManagerCompat mNotificationManager;

    private final PendingIntent mPauseIntent;
    private final PendingIntent mPlayIntent;
    private final PendingIntent mPreviousIntent;
    private final PendingIntent mNextIntent;
    private final PendingIntent mDeleteIntent;
    private final PendingIntent mAddToPlaylistIntent;

    private boolean mStarted = false;

    public MediaNotificationManager(MusicService service) throws RemoteException {
        mService = service;
        updateSessionToken();

        mNotificationManager = NotificationManagerCompat.from(service);

        String pkg = mService.getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mDeleteIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_DELETE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mAddToPlaylistIntent= PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_ADD_TO_PLAYLIST).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before {@link #stopNotification} is called.
     */
    public void startNotification() {
        // do this only once so that only one instance listens to callback
        if (!mStarted) {
            mMetadata = mController.getMetadata();
            mPlaybackState = mController.getPlaybackState();

            // The notification must be updated after setting started to true
            Notification notification = createNotification();
            if (notification != null) {

                mController.registerCallback(mCb);
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_NEXT);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PREV);
                filter.addAction(ACTION_DELETE);
                filter.addAction(ACTION_ADD_TO_PLAYLIST);
                mService.registerReceiver(this, filter);

                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            mController.unregisterCallback(mCb);
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mService.stopForeground(true);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "Received intent with action " + action);
        switch (action) {
            case ACTION_PAUSE:
                mTransportControls.pause();
                break;
            case ACTION_PLAY:
                mTransportControls.play();
                break;
            case ACTION_NEXT:
                mTransportControls.skipToNext();
                break;
            case ACTION_PREV:
                mTransportControls.skipToPrevious();
                break;
            case ACTION_DELETE:
                // todo implement
                break;
            case ACTION_ADD_TO_PLAYLIST:
                // todo implement
                break;
            default:
                Log.w(TAG, "Unknown intent ignored. Action="+action);
        }
    }

    private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            PlaybackStateCompat oldState = mPlaybackState;
            mPlaybackState = state;

            Log.d(TAG, "Received new playback state"+ state);

            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_STOPPED ||
                    mPlaybackState.getState() == PlaybackStateCompat.STATE_NONE) {
                stopNotification();
            } else {
                Notification notification = createNotification();
                if (notification != null) {
                    if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        if (mPlaybackState.getState() != oldState.getState()) {
                            // if the state changed from stopped to playing then
                            // start foreground so as to maintain service running even after
                            // app swiped away in recent apps
                            mService.startForeground(NOTIFICATION_ID, notification);
                        } else {
                            // if it has not changed from stopped to playing
                            // then notification already running in foreground
                            mNotificationManager.notify(NOTIFICATION_ID, notification);
                        }
                    } else if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
                        // if it has changed to paused then stopForeground()
                        // was called in the service in method onPlaybackStopped()
                        mNotificationManager.notify(NOTIFICATION_ID, notification);
                    }

                }
            }

        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMetadata = metadata;
            Log.d(TAG, "Received new metadata "+ metadata);
            Notification notification = createNotification();
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "Session was destroyed, resetting to the new session token");
            try {
                updateSessionToken();
            } catch (RemoteException e) {
                Log.e(TAG, "could not connect media controller", e);
            }
        }
    };

    private void updateSessionToken() throws RemoteException {
        MediaSessionCompat.Token freshToken = mService.getSessionToken();
        if (mSessionToken == null && freshToken != null ||
                mSessionToken != null && !mSessionToken.equals(freshToken)) {
            if (mController != null) {
                mController.unregisterCallback(mCb);
            }
            mSessionToken = freshToken;
            if (mSessionToken != null) {
                mController = new MediaControllerCompat(mService, mSessionToken);
                mTransportControls = mController.getTransportControls();
                if (mStarted) {
                    mController.registerCallback(mCb);
                }
            }
        }
    }

    private PendingIntent createContentIntent(MediaDescriptionCompat description) {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private Notification createNotification() {
        Log.d(TAG, "updateNotificationMetadata. mMetadata=" + mMetadata);
        if (mMetadata == null || mPlaybackState == null) {
            return null;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService);

        // If skip to previous action is enabled
        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_previous_grey_24dp,
                    "previous", mPreviousIntent);
        }

        addPlayPauseAction(notificationBuilder);

        // If skip to next action is enabled
        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_next_grey_24dp,
                    "next", mNextIntent);
        }

        // If add to playlist action is enabled
        if ((mPlaybackState.getActions() & Playback.CUSTOM_ACTION_ADD_TO_PLAYLIST) != 0) {
            notificationBuilder.addAction(R.drawable.ic_playlist_add_black_24dp,
                    "next", mAddToPlaylistIntent);
        }

        MediaDescriptionCompat description = mMetadata.getDescription();

        Bitmap art;
        int notificationColor;

        if (mMetadata.getDescription().getIconUri() != null) {
            art = BitmapFactory.decodeFile(mMetadata.getDescription().getIconUri().getEncodedPath());
        } else {
            art = BitmapFactory.decodeResource(mService.getResources(),
                    R.drawable.ic_audiotrack_grey_96dp);
        }

        notificationColor =
                ColorUtil
                        .getBackgroundColor(
                                ColorUtil.getColorSwatch(ColorUtil.generatePalette(art)));

        notificationBuilder
                .setColor(notificationColor)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setContentIntent(createContentIntent(description))
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setLargeIcon(art)
                .setDeleteIntent(mDeleteIntent)
                .setStyle(
                        new NotificationCompat
                                .MediaStyle()
                                .setShowActionsInCompactView(new int[]{0,1,2})
                .setMediaSession(mSessionToken));

        setNotificationPlaybackState(notificationBuilder);

        return notificationBuilder.build();
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        Log.d(TAG, "updatePlayPauseAction");
        String label;
        int icon;
        PendingIntent intent;
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = "pause";
            icon = R.drawable.ic_pause_grey_24dp;
            intent = mPauseIntent;
        } else {
            label = "play";
            icon = R.drawable.ic_play_arrow_grey_24dp;
            intent = mPlayIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        Log.d(TAG, "setNotificationPlaybackState. mPlaybackState=" + mPlaybackState);

        if (mPlaybackState == null) {
            Log.d(TAG, "setNotificationPlaybackState. cancelling notification!");
            return;
        }

        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                && mPlaybackState.getPosition() >= 0) {
            Log.d(TAG, "setNotificationPlaybackState. updating playback position to "+
                    (System.currentTimeMillis() - mPlaybackState.getPosition()) / 1000+ " seconds");
            builder
                    .setWhen(System.currentTimeMillis() - mPlaybackState.getPosition())
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {
            Log.d(TAG, "updateNotificationPlaybackState. hiding playback position");
            builder
                    .setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING);

        Log.i(TAG, "setNotificationPlaybackState:"
                +(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING));
    }
}
