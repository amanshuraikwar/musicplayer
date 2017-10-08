package app.sonu.com.musicplayer.util;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sonu on 7/9/17.
 */

public class MediaListHelper {
    //comparing two metadata on the basis of their last modified date
    private static final Comparator<MediaMetadataCompat> mediaMetadataDateModifiedComparator =
            new Comparator<MediaMetadataCompat>() {
                @Override
                public int compare(MediaMetadataCompat o1, MediaMetadataCompat o2) {
                    return o1
                            .getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_DATE_MODIFIED)
                            .compareTo(
                                    o2.getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_DATE_MODIFIED));
                }
            };

    //comparing two metadata on the basis of their display title
    private static final Comparator<MediaMetadataCompat> mediaMetadataTitleComparator =
            new Comparator<MediaMetadataCompat>() {
                @Override
                public int compare(MediaMetadataCompat o1, MediaMetadataCompat o2) {
                    return o1
                            .getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                            .compareTo(
                                    o2.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                }
            };

    public static void sortByDateModified(List<MediaMetadataCompat> items) {
        Collections.sort(items, Collections.reverseOrder(mediaMetadataDateModifiedComparator));
    }

    public static void sortByTitle(List<MediaMetadataCompat> items) {
        Collections.sort(items, mediaMetadataTitleComparator);
    }
}
