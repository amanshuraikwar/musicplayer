package app.sonu.com.musicplayer.mediaplayernew.util;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

/**
 * helps with calculations related to mediaid
 * @author amanshu
 */
public class MediaIdHelper {
    private static final String TAG = MediaIdHelper.class.getSimpleName();

    //Media IDs used for different media browsers
    public static final String MEDIA_ID_ALL_SONGS = "__ALL_SONGS__";
    public static final String MEDIA_ID_ALBUMS = "__BY_ALBUMS__";
    public static final String MEDIA_ID_ARTISTS = "__BY_ARTISTS__";

    @SuppressWarnings("WeakerAccess")
    public static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__";

    public static final String ALL_SONGS_ROOT_HINT = "__ALL_SONGS__";
    public static final String ALBUMS_ROOT_HINT = "__BY_ALBUMS__";
    public static final String ARTISTS_ROOT_HINT = "__BY_ARTISTS__";

    private static final String CATEGORY_SEPARATOR = "--/--";
    private static final String LEAF_SEPARATOR = "--|--";

    /**
     * for creating hierarchy aware mediaid
     * @param musicId musicid of song to determine song
     * @param categories categories to determine the list in which the song is present
     * @return built mediaid
     */
    public static String createMediaId(String musicId, String... categories) {
        StringBuilder stringBuilder = new StringBuilder();

        if (categories != null) {
            for (int i=0; i < categories.length; i++) {
                if(!isValidCategory(categories[i])) {
                    throw new IllegalArgumentException("invalid category: "+categories[i]);
                }
                stringBuilder.append(categories[i]);
                if (i < categories.length - 1) {
                    stringBuilder.append(CATEGORY_SEPARATOR);
                }
            }

            if (musicId != null) {
                stringBuilder.append(LEAF_SEPARATOR).append(musicId);
            }
        } else {
            Log.w(TAG, "createMediaId:categories is null");
        }

        return stringBuilder.toString();
    }

    /**
     * checking if the category is valid ir not
     * @param category category string to be checked
     * @return boolean telling the same
     */
    private static boolean isValidCategory(String category) {
        return category == null
                ||
                (!category.contains(CATEGORY_SEPARATOR) && !category.contains(LEAF_SEPARATOR));
    }

    /**
     * for extracting musicid from hierarchy aware mediaid
     * @param mediaId given hierarchy aware mediaid
     * @return musicid
     */
    public static String extractMusicIdFromMediaId(@NonNull String mediaId) {
        int pos = mediaId.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            return mediaId.substring(pos + LEAF_SEPARATOR.length());
        }
        return null;
    }

    /**
     *
     * @param mediaID
     * @return
     */
    public static boolean isBrowseable(@NonNull String mediaID) {
        return mediaID.indexOf(LEAF_SEPARATOR) < 0;
    }

    /**
     * for getting the categories/hierarchy of the given song according to its mediaid
     * @param mediaID input hierarchy aware mediaid
     * @return string array of categories/hierarchy
     */
    public static @NonNull String[] getHierarchy(@NonNull String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaID = mediaID.substring(0, pos);
        }
        return mediaID.split(String.valueOf(CATEGORY_SEPARATOR));
    }

    /**
     * for getting hierarchy id from a mediaid
     * @param mediaID input hierarchy aware mediaid
     * @return string hierarchyid
     */
    public static @NonNull String getHierarchyId(@NonNull String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaID = mediaID.substring(0, pos);
        }
        return mediaID;
    }

    public static String getMediaId(MediaSessionCompat.QueueItem item) {
        return item.getDescription().getMediaId();
    }
}
