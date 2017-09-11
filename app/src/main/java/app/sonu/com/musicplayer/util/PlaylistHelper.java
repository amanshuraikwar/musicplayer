package app.sonu.com.musicplayer.util;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayernew.manager.PlaylistsManager;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.mediaplayernew.playlistssource.PlaylistsSource;

/**
 * Created by sonu on 6/9/17.
 */

public class PlaylistHelper {

    private static final String TAG = PlaylistHelper.class.getSimpleName();

    public static String createPlaylistId() {
        return UniqueIdGenerator.getId()+"";
    }

    public static MediaMetadataCompat createPlaylistMetadata(String playlistKey,
                                                             String playlistTitle,
                                                             int noOfSongs,
                                                             int iconDrawableId,
                                                             int playlistColor,
                                                             int playlistType) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playlistKey)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playlistTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, playlistTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, noOfSongs+" Songs")
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, noOfSongs)
                .putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                        iconDrawableId)
                .putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_COLOR, playlistColor)
                .putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_TYPE, playlistType)
                .build();

    }
}
