package app.sonu.com.musicplayer.mediaplayernew.manager;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.mediaplayernew.MusicService;

/**
 * Created by sonu on 2/8/17.
 */

public class MediaBrowserManager {

    private static final String TAG = MediaBrowserManager.class.getSimpleName();

    private MediaBrowserCompat mMediaBrowser;
    private String mMediaId;
    private List<MediaBrowserCompat.MediaItem> mItemList;
    private String mRootHint;
    private MediaBrowserCallback mMediaBrowserCallback;
    private MediaControllerCallback mMediaControllerCallback;
    private FragmentActivity mActivity;


    public MediaBrowserManager(String rootHint) {
        mRootHint = rootHint;
    }

    public MediaBrowserManager(String rootHint, MediaBrowserCallback callback) {
        mRootHint = rootHint;
        mMediaBrowserCallback = callback;
    }

    public boolean isMediaBrowserConnected() {
        return mMediaBrowser != null ? mMediaBrowser.isConnected() : false;
    }

    public void setCallback(@NonNull MediaBrowserCallback callback) {
        mMediaBrowserCallback = callback;
    }

    public void setControllerCallback(@NonNull MediaControllerCallback callback){
        Log.d(TAG, "setControllerCallback:called");
        mMediaControllerCallback = callback;
    }

    public String getMediaId() {
        return mMediaId;
    }

    public void setMediaId(String mMediaId) {
        this.mMediaId = mMediaId;
    }

    public void initMediaBrowser(FragmentActivity activity) {
        Log.d(TAG, "initMediaBrowser:called");
        mActivity = activity;
        Bundle bundle = null;

        // check if a list is needed
        if (mRootHint != null) {
            bundle = new Bundle();
            bundle.putString(mActivity.getResources().getString(R.string.root_hint_key),
                    mRootHint);

        }

        mMediaBrowser = new MediaBrowserCompat(mActivity,
                new ComponentName(mActivity, MusicService.class),
                mConnectionCallbacks,
                bundle);
    }

    public void subscribeMediaBrowser() {
        Log.d(TAG, "subscribeMediaBrowser:called");
        if (mMediaId == null) {
            Log.i(TAG, "subscribeMediaBrowser:getting root");
            mMediaId = mMediaBrowser.getRoot();
        }

        if (mMediaId == null) {
            Log.i(TAG, "subscribeMediaBrowser:mediaId still null");
            return;
        }

        if (isMediaBrowserConnected()) {
            mMediaBrowser.subscribe(mMediaId, mSubscriptionCallback);
        } else {
            Log.w(TAG, "subscribeMediaBrowser:media browser not connected");
        }

    }

    public void connectMediaBrowser(){
        Log.d(TAG, "connectMediaBrowser:called");
        if (mMediaBrowser != null) {
            if (!mMediaBrowser.isConnected()) {
                mMediaBrowser.connect();
            } else {
                Log.w(TAG, "connectMediaBrowser:mMediaBrowser already connected, will not connect");
            }
        } else {
            Log.e(TAG, "connectMediaBrowser:mMediaBrowser is null, cannot connect");
        }
    }

    public void disconnectMediaBrowser() {
        Log.d(TAG, "disconnectMediaBrowser:called");
        if (mMediaBrowser != null) {
            if (mMediaBrowser.isConnected()) {
                if (mMediaControllerCallback != null) {
                    MediaControllerCompat
                            .getMediaController(mActivity)
                            .unregisterCallback(mMediaControllerCallback);
                }
                if (mMediaId != null) {
                    mMediaBrowser.unsubscribe(mMediaId, mSubscriptionCallback);
                }
                mMediaBrowser.disconnect();
            } else {
                Log.w(TAG, "disconnectMediaBrowser:mMediaBrowser not connected, will not disconnect");
            }
        } else {
            Log.e(TAG, "disconnectMediaBrowser:mMediaBrowser is null, cannot disconnect");
        }
    }

    public List<MediaBrowserCompat.MediaItem> getItemList() {
        Log.d(TAG, "getItemList:called");
        return mItemList;
    }

    public MediaControllerCompat getMediaController() {
        Log.d(TAG, "getMediaController:called");
        return MediaControllerCompat.getMediaController(mActivity);
    }

    public void search(String query) {
        mMediaBrowser.search(query, null, new MediaBrowserCompat.SearchCallback() {
            @Override
            public void onSearchResult(@NonNull String query,
                                       Bundle extras,
                                       @NonNull List<MediaBrowserCompat.MediaItem> items) {
                mMediaBrowserCallback.onSearchResult(items);
            }
        });
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    mMediaBrowserCallback.onMediaBrowserConnected();

                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

                    // Create a MediaControllerCompat
                    MediaControllerCompat mediaController;
                    try {
                        mediaController = new MediaControllerCompat(mActivity, // Context
                                token);

                        if (mMediaControllerCallback != null) {
                            Log.d(TAG, "onConnected:mediaControllerCallback:registering");
                            mediaController.registerCallback(mMediaControllerCallback);
                        } else {
                            Log.w(TAG, "onConnected:mediaControllerCallback is null, not registering");
                        }

                        // Save the controller
                        MediaControllerCompat
                                .setMediaController(mActivity, mediaController);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onConnected:exception during getting media controller", e);
                    }
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls
                    // until it automatically reconnects
                    mMediaBrowserCallback.onMediaBrowserConnectionSuspended();
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                    mMediaBrowserCallback.onMediaBrowserConnectionFailed();
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "onChildrenLoaded:called");
                        Log.i(TAG, "onChildrenLoaded:no of items="+children.size());
                        mItemList = children;
                        mMediaBrowserCallback.onMediaBrowserChildrenLoaded(children);
                    } catch (Throwable t) {
                        Log.e(TAG, "onChildrenLoaded:exception", t);
                        t.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "onError:subscription error id="+id);
                    mMediaBrowserCallback.onMediaBrowserSubscriptionError(id);
                }
            };



    /**
     * callbacks of media browser to be implemented in any presenter of ui element
     */
    public interface MediaBrowserCallback{
        void onMediaBrowserConnected();
        void onMediaBrowserConnectionSuspended();
        void onMediaBrowserConnectionFailed();
        void onMediaBrowserChildrenLoaded(List<MediaBrowserCompat.MediaItem> items);
        void onMediaBrowserSubscriptionError(String id);
        void onSearchResult(List<MediaBrowserCompat.MediaItem> items);
    }

    public static class MediaControllerCallback extends MediaControllerCompat.Callback {
        //define custom methods if any
    }
}
