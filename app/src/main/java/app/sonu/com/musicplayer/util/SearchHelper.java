package app.sonu.com.musicplayer.util;

/**
 * Created by sonu on 2/10/17.
 */

public class SearchHelper {

    public static final String ALBUMS_HEADER = "Albums";
    public static final String ARTISTS_HEADER = "Artists";
    public static final String PLAYLISTS_HEADER = "Playlists";
    public static final String SONGS_HEADER = "Songs";

    public static String getSearchItemHeader(String mediaId) {
        String header = "";

        if (MediaIdHelper.isBrowseable(mediaId)) {
            if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)) {
                header = ALBUMS_HEADER;
            } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) {
                header = ARTISTS_HEADER;
            } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_PLAYLISTS)) {
                header = PLAYLISTS_HEADER;
            }
        } else {
            header = SONGS_HEADER;
        }

        return header;
    }
}
