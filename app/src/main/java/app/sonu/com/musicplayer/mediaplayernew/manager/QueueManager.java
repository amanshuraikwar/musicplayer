package app.sonu.com.musicplayer.mediaplayernew.manager;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
 * this class manages the playing queue
 * @author amanshu
 */

public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();

    private MusicProvider mMusicProvider;
    private MetadataUpdateListener mMetadataUpdateListener;

    // "Now playing" queue
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    public QueueManager(MusicProvider musicProvider,
                        MetadataUpdateListener metadataUpdateListener) {
        this.mMusicProvider = musicProvider;
        this.mMetadataUpdateListener = metadataUpdateListener;

        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
    }

    @SuppressWarnings("WeakerAccess")
    public MediaSessionCompat.QueueItem getCurrentMusic() {
        Log.d(TAG, "getCurrentMusic:called");
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            Log.w(TAG, "getCurrentMusic:index not playable");
            return null;
        }

        return mPlayingQueue.get(mCurrentIndex);
    }

    @SuppressWarnings("WeakerAccess")
    public String getCurrentMusicMediaId() {
        return getCurrentMusic() == null ? null : getCurrentMusic().getDescription().getMediaId();
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

        int index = 0;
        if (initialMediaId != null) {
            index = QueueHelper.getQueueIndexOf(initialMediaId, mPlayingQueue);
        }
        mCurrentIndex = Math.max(index, 0);
        mMetadataUpdateListener.onQueueUpdated(title, newQueue);
    }

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
    public void setQueueFromMediaId(String mediaId, boolean forceNewQueue) {
        Log.d(TAG, "setQueueFromMediaId:called");
        if (isQueueReusable(mediaId) && !forceNewQueue) {
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

    @SuppressWarnings("WeakerAccess")
    public void shuffleMusic(String mediaId) {
        if (mPlayingQueue != null) {
            ArrayList<MediaSessionCompat.QueueItem> newQueue = new ArrayList<>();
            newQueue.add(QueueHelper.getQueueItem(mediaId, mMusicProvider));
            mPlayingQueue.remove(QueueHelper.getQueueIndexOf(mediaId, mPlayingQueue));
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

        MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            mMetadataUpdateListener.onMetadataRetrieveError();
            return;
        }

        String mediaId = currentMusic.getDescription().getMediaId();
        if (mediaId == null) {
            Log.w(TAG, "updateMetadata:mediaid is null");
            return;
        }

        String musicId = MediaIdHelper.extractMusicIdFromMediaId(mediaId);
        if (musicId == null) {
            Log.w(TAG, "updateMetadata:musicid is null");
            return;
        }

        MediaMetadataCompat metadata = mMusicProvider.getMusic(musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
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

    private boolean isQueueReusable(String mediaId) {
        if (mPlayingQueue == null) { //somehow queue is null
            return false;
        } else if (mPlayingQueue.size() == 0){ //queue has no songs
            return false;
        } else {
            return isSameBrowsingCategory(mediaId);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSameBrowsingCategory(@NonNull String mediaId) {
        String[] newBrowseHierarchy = MediaIdHelper.getHierarchy(mediaId);
        MediaSessionCompat.QueueItem current = getCurrentMusic();
        if (current == null) {
            return false;
        }

        String curMediaId = current.getDescription().getMediaId();
        if (curMediaId == null) {
            Log.w(TAG, "updateMetadata:mediaid is null");
            return false;
        }

        String[] currentBrowseHierarchy = MediaIdHelper.getHierarchy(curMediaId);

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
