package app.sonu.com.musicplayer.mediaplayernew.manager;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayernew.util.MediaIdHelper;
import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;
import app.sonu.com.musicplayer.mediaplayernew.util.QueueHelper;

/**
 * Created by sonu on 27/7/17.
 */

public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();

    private MusicProvider mMusicProvider;
    private MetadataUpdateListener mMetadataUpdateListener;

    // "Now playing" queue
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    private int mShuffleMode;

    public QueueManager(MusicProvider musicProvider,
                        MetadataUpdateListener metadataUpdateListener) {
        this.mMusicProvider = musicProvider;
        this.mMetadataUpdateListener = metadataUpdateListener;

        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
        mShuffleMode = 0;

    }

    public MediaSessionCompat.QueueItem getCurrentMusic() {
        Log.d(TAG, "getCurrentMusic:called");
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            Log.w(TAG, "getCurrentMusic:index not playable");
            return null;
        }

        return mPlayingQueue.get(mCurrentIndex);
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

    private boolean setCurrentQueueItem(String mediaId) {
        Log.d(TAG, "getCurrentQueueIndex:called");
        Log.i(TAG, "getCurrentQueueIndex:mediaId="+mediaId);
        int index = QueueHelper.getQueueIndexOf(mediaId, mPlayingQueue);
        return setCurrentQueueIndex(index);
    }

    private void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue,
                                   String initialMediaId) {
        Log.d(TAG, "setCurrentQueue:called");
        mPlayingQueue = newQueue;

        if (getShuffleMode() == 1) {
            Collections.shuffle(mPlayingQueue);
        }

        int index = 0;
        if (initialMediaId != null) {
            index = QueueHelper.getQueueIndexOf(initialMediaId, mPlayingQueue);
        }
        mCurrentIndex = Math.max(index, 0);
        mMetadataUpdateListener.onQueueUpdated(title, newQueue);
    }

    public void setQueueFromMediaId(String mediaId) {
        Log.d(TAG, "setQueueFromMediaId:called");
        if (isQueueReusable(mediaId)) {
            Log.d(TAG, "setQueueFromMediaId:queue is reusable");
            setCurrentQueueItem(mediaId);
        } else {
            Log.d(TAG, "setQueueFromMediaId:queue is not reusable");
            setCurrentQueue("now playing",
                    QueueHelper.getPlayingQueue(mediaId, mMusicProvider),
                    mediaId);
        }
        updateMetadata();
    }

    private void updateMetadata() {
        Log.d(TAG, "updateMetadata:called");
        MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            mMetadataUpdateListener.onMetadataRetrieveError();
            return;
        }
        final String musicId = MediaIdHelper.extractMusicIdFromMediaId(
                currentMusic.getDescription().getMediaId());
        MediaMetadataCompat metadata = mMusicProvider.getMusic(musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
        }

        mMetadataUpdateListener.onMetadataChanged(metadata);
    }

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

    private boolean isQueueReusable(String mediaId) {
        if (mPlayingQueue == null) { //somehow queue is null
            return false;
        } else if (mPlayingQueue.size() == 0){ //queue has no songs
            return false;
        } else {
            return isSameBrowsingCategory(mediaId);
        }
    }

    public boolean isSameBrowsingCategory(@NonNull String mediaId) {
        String[] newBrowseHierarchy = MediaIdHelper.getHierarchy(mediaId);
        MediaSessionCompat.QueueItem current = getCurrentMusic();
        if (current == null) {
            return false;
        }
        String[] currentBrowseHierarchy = MediaIdHelper.getHierarchy(
                current.getDescription().getMediaId());

        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
    }

    public void setShuffleMode(int mode) {
        Log.d(TAG, "setShuffleMode:called");
        if (mode == 0 || mode == 1) {
            if (mShuffleMode != mode) {
                Log.i(TAG, "setShuffleMode:mode="+mode);
                mShuffleMode = mode;
                String mediaId = getCurrentMusic().getDescription().getMediaId();
                if (mShuffleMode == 1) {
                    setCurrentQueue("now playing",
                            QueueHelper.getPlayingQueue(mediaId, mMusicProvider), mediaId);
                } else {
                    setCurrentQueue("now playing",
                            QueueHelper.getPlayingQueue(mediaId, mMusicProvider), mediaId);
                }
                mMetadataUpdateListener.onCurrentQueueIndexUpdated(mCurrentIndex);
            }
        }
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);
        void onMetadataRetrieveError();
        void onQueueUpdated(String title,
                            List<MediaSessionCompat.QueueItem> newQueue);
        void onCurrentQueueIndexUpdated(int currentIndex);
    }
}
