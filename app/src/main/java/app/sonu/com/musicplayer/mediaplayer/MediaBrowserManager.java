package app.sonu.com.musicplayer.mediaplayer;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.List;

import app.sonu.com.musicplayer.R;

/**
 * Created by sonu on 2/8/17.
 * this class is responsible for providing mediabrowser and mediacontroller to ui
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

    private String mParentName;

    public MediaBrowserManager(String rootHint, String parentName) {
        mRootHint = rootHint;
        mParentName = parentName;
    }

    public MediaBrowserManager(String rootHint, String parentName, MediaBrowserCallback callback) {
        mRootHint = rootHint;
        mParentName = parentName;
        mMediaBrowserCallback = callback;
    }

    public boolean isMediaBrowserConnected() {
        return mMediaBrowser != null && mMediaBrowser.isConnected();
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
            bundle.putString(mActivity.getResources().getString(R.string.root_hint_key), mRootHint);
        }

        mMediaBrowser = new MediaBrowserCompat(mActivity,
                new ComponentName(mActivity, MusicService.class),
                mConnectionCallbacks,
                bundle);
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
                Log.w(TAG, "disconnectMediaBrowser:mMediaBrowser not connected, will not" +
                        " disconnect");
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

    public void subscribeMediaBrowser() {
        Log.d(TAG, "subscribeMediaBrowser:0:called");
        subscribeMediaBrowser(new Bundle());
    }

    public void subscribeMediaBrowser(@NonNull Bundle extras) {
        Log.d(TAG, "subscribeMediaBrowser:called");
        Log.i(TAG, "subscribeMediaBrowser:extras="+extras);

        if (mMediaId == null) {
            Log.i(TAG, "subscribeMediaBrowser:getting root");
            mMediaId = mMediaBrowser.getRoot();
        }

        if (isMediaBrowserConnected()) {
            mMediaBrowser.unsubscribe(mMediaId, mSubscriptionCallback);
            mMediaBrowser.subscribe(mMediaId, extras, mSubscriptionCallback);
        } else {
            Log.w(TAG, "subscribeMediaBrowser:media browser not connected");
        }
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {

                @Override
                public void onConnected() {
                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

                    //create only if the media controller is null
                    if (MediaControllerCompat.getMediaController(mActivity) == null) {
                        Log.i(TAG, "onConnected:creating new media controller parent="+mParentName);

                        // Create a MediaControllerCompat
                        MediaControllerCompat mediaController;
                        try {
                            mediaController = new MediaControllerCompat(mActivity, // Context
                                    token);

                            if (mMediaControllerCallback != null) {
                                Log.d(TAG, "onConnected:mediaControllerCallback:registering parent="
                                        +mParentName);
                                mediaController.registerCallback(mMediaControllerCallback);
                            } else {
                                Log.w(
                                        TAG,
                                        "onConnected:mediaControllerCallback is null, not " +
                                                "registering parent="+mParentName);
                            }

                            // Save the controller
                            MediaControllerCompat
                                    .setMediaController(mActivity, mediaController);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onConnected:exception during getting media controller", e);
                        }
                    } else {
                        // atleast register new callback
                        if (mMediaControllerCallback != null) {
                            Log.d(TAG, "onConnected:mediaControllerCallback:registering " +
                                    "parent="+mParentName);
                            MediaControllerCompat
                                    .getMediaController(mActivity)
                                    .registerCallback(mMediaControllerCallback);
                        } else {
                            Log.w(TAG, "onConnected:mediaControllerCallback is null, " +
                                    "not registering parent="+mParentName);
                        }
                    }

                    Log.i(TAG, "mediaBrowserConnected parent="+mParentName);

                    mMediaBrowserCallback.onMediaBrowserConnected();
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
                    Log.d(TAG, "onChildrenLoaded:called");

                    try {
                        Log.i(TAG, "onChildrenLoaded:no of items="+children.size());
                        mItemList = children;
                        mMediaBrowserCallback.onMediaBrowserChildrenLoaded(children);
                    } catch (Throwable t) {
                        Log.e(TAG, "onChildrenLoaded:exception", t);
                        t.printStackTrace();
                    }
                }

                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children,
                                             @NonNull Bundle options) {
                    Log.d(TAG, "onChildrenLoaded:called");

                    try {
                        Log.i(TAG, "onChildrenLoaded:no of items="+children.size());
                        // saving item list
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
