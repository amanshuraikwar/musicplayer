package app.sonu.com.musicplayer.mediaplayer;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsManager;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.util.QueueHelper;

/**
 * Created by sonu on 27/7/17.
 * this class manages the playing queue
 * @author amanshu
 */

public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();

    private MediaProvider mMediaProvider;
    private MetadataUpdateListener mMetadataUpdateListener;
    private PlaylistsManager mPlaylistsManager;

    // "Now playing" queue
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    public QueueManager(MediaProvider mediaProvider,
                        MetadataUpdateListener metadataUpdateListener,
                        PlaylistsManager playlistsManager) {
        mMediaProvider = mediaProvider;
        mMetadataUpdateListener = metadataUpdateListener;
        mPlaylistsManager = playlistsManager;

        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
    }

    @SuppressWarnings("WeakerAccess")
    public MediaSessionCompat.QueueItem getCurrentSong() {
        Log.d(TAG, "getCurrentSong:called");
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            Log.w(TAG, "getCurrentSong:index not playable");
            return null;
        }

        return mPlayingQueue.get(mCurrentIndex);
    }

    @SuppressWarnings("WeakerAccess")
    public String getCurrentSongMediaId() {
        return getCurrentSong() == null ? null : getCurrentSong().getDescription().getMediaId();
    }

    public int getmCurrentQueueIndex() {
        return mCurrentIndex;
    }

    private boolean setCurrentQueueIndex(int index) {
        Log.d(TAG, "getCurrentQueueIndex:called");
        Log.i(TAG, "getCurrentQueueIndex:index="+index);

        if (index >= 0 && index < mPlayingQueue.size()) {
            mCurrentIndex = index;
            mMetadataUpdateListener.onCurrentQueueIndexUpdated(mCurrentIndex);
            return true;
        }

        return false;
    }

    private boolean setCurrentQueueItem(String songMediaId) {
        Log.d(TAG, "getCurrentQueueIndex:called");
        Log.i(TAG, "getCurrentQueueIndex:mediaId="+songMediaId);
        if (songMediaId == null) {
            // todo inspect
            return false;
        }
        int index = QueueHelper.getQueueIndexOf(songMediaId, mPlayingQueue);
        return setCurrentQueueIndex(index);
    }

    public void setCurrentQueueItem(long queueItemId) {
        setCurrentQueueItem(QueueHelper.getMediaIdOf(queueItemId, mPlayingQueue));
        updateMetadata();
    }

    private void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue,
                                   String initialSongMediaId) {
        Log.d(TAG, "setCurrentQueue:called");
        mPlayingQueue = newQueue;

        int index = 0;
        if (initialSongMediaId != null) {
            index = QueueHelper.getQueueIndexOf(initialSongMediaId, mPlayingQueue);
        }
        mCurrentIndex = Math.max(index, 0);
        mMetadataUpdateListener.onQueueUpdated(title, newQueue);
    }

    @SuppressWarnings("WeakerAccess")
    public void setQueueFromSongMediaId(String songMediaId) {
        Log.d(TAG, "setQueueFromSongMediaId:called");
        if (isQueueReusable(songMediaId)) {
            Log.d(TAG, "setQueueFromSongMediaId:queue is reusable");
            setCurrentQueueItem(songMediaId);
        } else {
            Log.d(TAG, "setQueueFromSongMediaId:queue is not reusable");
            setCurrentQueue("now playing",
                    QueueHelper.getPlayingQueue(songMediaId, mMediaProvider, mPlaylistsManager),
                    songMediaId);
        }
        updateMetadata();
    }

    @SuppressWarnings("WeakerAccess")
    public void setQueueFromSongMediaId(String songMediaId, boolean forceNewQueue) {
        Log.d(TAG, "setQueueFromSongMediaId:called");
        if (isQueueReusable(songMediaId) && !forceNewQueue) {
            Log.d(TAG, "setQueueFromSongMediaId:queue is reusable");
            setCurrentQueueItem(songMediaId);
        } else {
            Log.d(TAG, "setQueueFromSongMediaId:queue is not reusable");
            setCurrentQueue("now playing",
                    QueueHelper.getPlayingQueue(songMediaId, mMediaProvider, mPlaylistsManager),
                    songMediaId);
        }
        updateMetadata();
    }

    @SuppressWarnings("WeakerAccess")
    public void shuffleMusic(String songMediaId) {
        if (mPlayingQueue != null) {
            ArrayList<MediaSessionCompat.QueueItem> newQueue = new ArrayList<>();
            newQueue.add(QueueHelper.getQueueItem(
                    songMediaId,
                    mMediaProvider));
            mPlayingQueue.remove(QueueHelper.getQueueIndexOf(songMediaId, mPlayingQueue));
            Collections.shuffle(mPlayingQueue);
            newQueue.addAll(mPlayingQueue);
            mPlayingQueue = newQueue;
            mCurrentIndex = 0;

            mMetadataUpdateListener.onQueueUpdated("now playing", mPlayingQueue);
            mMetadataUpdateListener.onCurrentQueueIndexUpdated(mCurrentIndex);
        }
    }

    private void updateMetadata() {
        Log.d(TAG, "updateMetadata:called");

        MediaSessionCompat.QueueItem currentMusic = getCurrentSong();
        if (currentMusic == null) {
            mMetadataUpdateListener.onMetadataRetrieveError();
            return;
        }

        String songMediaId = currentMusic.getDescription().getMediaId();
        if (songMediaId == null) {
            Log.w(TAG, "updateMetadata:mediaid is null");
            return;
        }

        String songId = MediaIdHelper.getSongIdFromMediaId(songMediaId);
        if (songId == null) {
            Log.w(TAG, "updateMetadata:musicid is null");
            return;
        }

        MediaMetadataCompat metadata = mMediaProvider.getSongBySongId(songId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + songId);
        }

        mMetadataUpdateListener.onMetadataChanged(metadata);
        mMetadataUpdateListener.onCurrentQueueIndexUpdated(mCurrentIndex);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean skipQueuePosition(int amount) {
        Log.d(TAG, "skipQueuePosition:called");
        int index = mCurrentIndex + amount;

        if (index < 0) {
            // skip backwards before the first song will keep you on the first song
            index = 0;
        } else {
            // skip forwards when in last song will cycle back to start of the queue
            index %= mPlayingQueue.size();
        }

        if (!QueueHelper.isIndexPlayable(index, mPlayingQueue)) {
            Log.e(TAG, "Cannot increment queue index by "+ amount +
                    ". Current=" + mCurrentIndex + " queue length=" + mPlayingQueue.size());
            return false;
        }

        mCurrentIndex = index;
        updateMetadata();
        return true;
    }

    private boolean isQueueReusable(String songMediaId) {
        if (mPlayingQueue == null) { //somehow queue is null
            return false;
        } else if (mPlayingQueue.size() == 0){ //queue has no songs
            return false;
        } else {
            return isSameBrowsingCategory(songMediaId);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSameBrowsingCategory(@NonNull String songMediaId) {
        String[] newBrowseHierarchy = MediaIdHelper.getHierarchy(songMediaId);
        MediaSessionCompat.QueueItem current = getCurrentSong();
        if (current == null) {
            return false;
        }

        String curSongMediaId = current.getDescription().getMediaId();
        if (curSongMediaId == null) {
            Log.w(TAG, "updateMetadata:mediaid is null");
            return false;
        }

        String[] currentBrowseHierarchy = MediaIdHelper.getHierarchy(curSongMediaId);

        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isLastItemPlaying() {
        return mCurrentIndex == (mPlayingQueue.size() - 1);
    }



    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);
        void onMetadataRetrieveError();
        void onQueueUpdated(String title,
                            List<MediaSessionCompat.QueueItem> newQueue);
        void onCurrentQueueIndexUpdated(int currentIndex);
    }
}
