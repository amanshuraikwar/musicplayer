package app.sonu.com.musicplayer.mediaplayernew.util;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;

/**
 * Created by sonu on 7/8/17.
 */

public class QueueHelper {

    private static final String TAG = QueueHelper.class.getSimpleName();

    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        if (queue == null) {
            return false;
        }

        if ((index < 0) || (index >= queue.size())) {
            return false;
        }

        return true;
    }

    public static int getQueueIndexOf(@NonNull String mediaId,
                                 List<MediaSessionCompat.QueueItem> queue) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }

        return -1;
    }

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaId,
                                                                     MusicProvider musicProvider) {
        Log.d(TAG, "getPlayingQueue:called");
        Log.i(TAG, "getPlayingQueue:id="+mediaId);

        List<MediaSessionCompat.QueueItem> playingQueue = new ArrayList<>();
        String[] hierarchy = MediaIdHelper.getHierarchy(mediaId);
        //todo add support for albums and artists
        if (hierarchy[0].equals(MediaIdHelper.MEDIA_ID_ALL_SONGS)) {
            for (MediaMetadataCompat metadata : musicProvider.getSongs()) {
                playingQueue.add(getQueueItem(metadata, MediaIdHelper.MEDIA_ID_ALL_SONGS));
            }
        } else if (hierarchy[0].equals(MediaIdHelper.MEDIA_ID_ALBUMS)) {
            for (MediaMetadataCompat metadata : musicProvider.getMusicsByAlbumKey(hierarchy[1])) {
                playingQueue.add(getQueueItem(metadata, hierarchy));
            }
        } else if (hierarchy[0].equals(MediaIdHelper.MEDIA_ID_ARTISTS)) {
            for (MediaMetadataCompat metadata : musicProvider.getMusicsByArtistKey(hierarchy[1])) {
                playingQueue.add(getQueueItem(metadata, hierarchy));
            }
        }

        return playingQueue;
    }

    private static MediaSessionCompat.QueueItem getQueueItem(MediaMetadataCompat metadata,
                                                            String rootId) {
        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                rootId);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));
        MediaDescriptionCompat description = builder.build();

        MediaSessionCompat.QueueItem queueItem =
                new MediaSessionCompat.QueueItem(description, System.currentTimeMillis());

        return queueItem;
    }

    private static MediaSessionCompat.QueueItem getQueueItem(MediaMetadataCompat metadata,
                                                             String...rootId) {
        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                rootId);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));
        MediaDescriptionCompat description = builder.build();

        MediaSessionCompat.QueueItem queueItem =
                new MediaSessionCompat.QueueItem(description, System.currentTimeMillis());

        return queueItem;
    }

    public static MediaSessionCompat.QueueItem getQueueItem(String mediaId,
                                                             MusicProvider musicProvider) {
        String[] hierarchy = MediaIdHelper.getHierarchy(mediaId);
        String musicId = MediaIdHelper.extractMusicIdFromMediaId(mediaId);
        MediaMetadataCompat metadata = musicProvider.getMusic(musicId);
        if (metadata == null) {
            return null;
        } else {
            return getQueueItem(metadata, hierarchy[0]);
        }
    }
}
