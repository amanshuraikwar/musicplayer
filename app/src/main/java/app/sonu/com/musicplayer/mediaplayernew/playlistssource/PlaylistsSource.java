package app.sonu.com.musicplayer.mediaplayernew.playlistssource;

import android.graphics.Color;

import java.util.List;

import app.sonu.com.musicplayer.R;

/**
 * Created by sonu on 6/9/17.
 */

public interface PlaylistsSource {
    String LAST_ADDED_PLAYLIST_TITLE = "Last Added";
    int LAST_ADDED_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_playlist_play_blue_grey_300_24dp;
    int LAST_ADDED_PLAYLIST_COLOR = Color.parseColor("#78909C");

    String FAVORITES_PLAYLIST_TITLE = "Favourites";
    int FAVORITES_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_heart_outline_white_24dp;
    int FAVORITES_PLAYLIST_COLOR = Color.parseColor("#78909C");
    long FAVORITES_PLAYLIST_ID = 1;

    String DAFAULT_PLAYLIST_TITLE = "Last Added";
    int DEFAULT_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_playlist_play_blue_grey_300_24dp;
    int DEFAULT_PLAYLIST_COLOR = Color.parseColor("#78909C");

    String CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID = "__PLAYLIST_ICON_DRAWABLE_ID__";
    String CUSTOM_METADATA_KEY_PLAYLIST_COLOR = "__PLAYLIST_COLOR__";
    String CUSTOM_METADATA_KEY_PLAYLIST_TYPE = "__PLAYLIST_TYPE__";

    String PLAYBACK_STATE_EXTRA_IS_IN_FAVORITES = "isInFavorites";

    int PLAYLIST_TYPE_AUTO = 0;
    int PLAYLIST_TYPE_USER = 1;

    String PLAYLIST_TITLES[] = {"Auto", "User"};

    void createPlaylistIdList();
    List<String> getPlaylistIds();
    Playlist getPlaylistById(long playlistId);
    void addNewPlaylist(Playlist playlist);
    void addMusicMediaIdToPlaylist(long playlistId, String mediaId);
    void removeMusicMediaIdFromPlaylist(long playlistId, String mediaId);
    void deletePlaylist(long id);
}
