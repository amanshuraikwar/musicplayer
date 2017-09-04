package app.sonu.com.musicplayer.data.local;

import android.support.v4.media.MediaMetadataCompat;
import java.util.List;

/**
 * Created by sonu on 30/6/17.
 */

public interface LocalStorageHelper {
    List<MediaMetadataCompat> getSongListFromLocalStorage();
    List<MediaMetadataCompat> getAlbumListFromLocalStorage();
    List<MediaMetadataCompat> getArtistListFromLocalStorage();
}
