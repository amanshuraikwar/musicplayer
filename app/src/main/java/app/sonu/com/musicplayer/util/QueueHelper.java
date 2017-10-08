package app.sonu.com.musicplayer.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsManager;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsManagerImpl;

/**
 * Created by amanshu on 7/8/17.
 * helper class for queue related tasks
 * @author amanshu
 */
public class QueueHelper {

    private static final String TAG = QueueHelper.class.getSimpleName();

    /**
     * tells is this queue index is playable or not
     * @param index index to check
     * @param queue list from which it is checked
     * @return boolean for the decision
     */
    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return queue != null && !((index < 0) || (index >= queue.size()));
    }

    /**
     * for getting queue index for a mediaid
     * @param songMediaId mediaid of which index is required
     * @param queue list from which index is searched
     * @return index of the mediaid
     */
    public static int getQueueIndexOf(@NonNull String songMediaId,
                                      @NonNull List<MediaSessionCompat.QueueItem> queue) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (songMediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }

        return -1;
    }

    public static String getMediaIdOf(long queueItemId,
                                      @NonNull List<MediaSessionCompat.QueueItem> queue) {
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueItemId == item.getQueueId()) {
                return item.getDescription().getMediaId();
            }
        }

        return null;
    }

    public static String getMediaIdOf(@NonNull MediaSessionCompat.QueueItem item) {
        return item.getDescription().getMediaId();
    }

    public static String getSongIdOf(@NonNull MediaSessionCompat.QueueItem item) {
        return MediaIdHelper.getSongIdFromMediaId(item.getDescription().getMediaId());
    }

    /**
     * for getting playing queue according to mediaid
     * @param songMediaId input mediaid
     * @param mediaProvider musicprovider which provides the music
     * @return list from given mediaid
     */
    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(@NonNull String songMediaId,
                                                                     @NonNull MediaProvider mediaProvider,
                                                                     @NonNull PlaylistsManager playlistsManager) {
        Log.d(TAG, "getPlayingQueue:called");
        Log.i(TAG, "getPlayingQueue:songMediaId="+songMediaId);

        List<MediaSessionCompat.QueueItem> playingQueue = new ArrayList<>();
        String[] hierarchy = MediaIdHelper.getHierarchy(songMediaId);

        String temp = "";
        for (String t : hierarchy) {
            temp+="["+t+"]";
        }
        Log.i(TAG, "getPlayingQueue:hierarchy="+temp);

        switch (hierarchy[0]) {
            case MediaIdHelper.MEDIA_ID_ALL_SONGS:
                for (MediaMetadataCompat metadata : mediaProvider.getSongs()) {
                    playingQueue.add(getQueueItem(metadata, MediaIdHelper.MEDIA_ID_ALL_SONGS));
                }
                break;
            case MediaIdHelper.MEDIA_ID_ALBUMS:
                for (MediaMetadataCompat metadata : mediaProvider.getSongsByAlbumId(hierarchy[1])) {
                    playingQueue.add(getQueueItem(metadata, hierarchy));
                }
                break;
            case MediaIdHelper.MEDIA_ID_ARTISTS:
                for (MediaMetadataCompat metadata : mediaProvider.getSongsByArtistId(hierarchy[1])) {
                    playingQueue.add(getQueueItem(metadata, hierarchy));
                }
                break;
            case MediaIdHelper.MEDIA_ID_PLAYLISTS:
                for (MediaMetadataCompat metadata : playlistsManager.getSongListByPlaylistId(hierarchy[1])) {
                    playingQueue.add(getQueueItem(metadata, hierarchy));
                }
                break;
        }

        return playingQueue;
    }

    /**
     * for getting the queueitem from given mediaid
     * @param songMediaId input mediaid
     * @param mediaProvider musicprovider which provides the music
     * @return list of songs
     */
    public static MediaSessionCompat.QueueItem getQueueItem(@NonNull String songMediaId,
                                                            @NonNull MediaProvider mediaProvider) {
        String[] hierarchy = MediaIdHelper.getHierarchy(songMediaId);
        String songId = MediaIdHelper.getSongIdFromMediaId(songMediaId);

        if (songId != null) {
            MediaMetadataCompat metadata = mediaProvider.getSongBySongId(songId);
            if (metadata == null) {
                return null;
            } else {
                return getQueueItem(metadata, hierarchy);
            }
        }
        return null;
    }

    private static MediaSessionCompat.QueueItem getQueueItem(@NonNull MediaMetadataCompat metadata,
                                                             @NonNull String rootId) {
        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                rootId);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(MediaMetadataHelper.getSongDisplayTitle(metadata))
                .setSubtitle(MediaMetadataHelper.getSongDisplaySubtitle(metadata));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat description = builder.build();

        return new MediaSessionCompat.QueueItem(description, UniqueIdGenerator.getId());
    }

    private static MediaSessionCompat.QueueItem getQueueItem(@NonNull MediaMetadataCompat metadata,
                                                             @NonNull String...rootId) {
        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                rootId);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(MediaMetadataHelper.getSongDisplayTitle(metadata))
                .setSubtitle(MediaMetadataHelper.getSongDisplaySubtitle(metadata));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat description = builder.build();

        return new MediaSessionCompat.QueueItem(description, UniqueIdGenerator.getId());
    }
}
