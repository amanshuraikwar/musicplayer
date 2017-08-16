package app.sonu.com.musicplayer.mediaplayernew.util;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by sonu on 29/7/17.
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
     *
     * @param musicId
     * @param categories
     * @return
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
            } else {
                //Log.i(TAG, "createMediaId:musicId is null(not a problem)");
            }
        } else {
            Log.w(TAG, "createMediaId:categories is null");
        }

        return stringBuilder.toString();
    }

    /**
     *
     * @param category
     * @return
     */
    private static boolean isValidCategory(String category) {
        return category == null
                ||
                (category.indexOf(CATEGORY_SEPARATOR) < 0 && category.indexOf(LEAF_SEPARATOR) < 0);
    }

    /**
     *
     * @param mediaId
     * @return
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
     *
     * @param mediaID
     * @return
     */
    public static @NonNull String[] getHierarchy(@NonNull String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaID = mediaID.substring(0, pos);
        }
        return mediaID.split(String.valueOf(CATEGORY_SEPARATOR));
    }
}
