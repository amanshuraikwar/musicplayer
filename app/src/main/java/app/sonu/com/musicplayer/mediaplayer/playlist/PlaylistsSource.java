package app.sonu.com.musicplayer.mediaplayer.playlist;

import android.graphics.Color;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.model.Playlist;

/**
 * Created by sonu on 6/9/17.
 */

public interface PlaylistsSource {
    String LAST_ADDED_PLAYLIST_TITLE = "Last Added";
    String FAVORITES_PLAYLIST_TITLE = "Favourites";

    int LAST_ADDED_PLAYLIST_COLOR = Color.parseColor("#484848");
    int FAVORITES_PLAYLIST_COLOR = Color.parseColor("#F06292");
    int DEFAULT_PLAYLIST_COLOR = Color.parseColor("#484848");

    int LAST_ADDED_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_playlist_play_grey_24dp;
    int FAVORITES_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_heart_solid_grey_24dp;
    int DEFAULT_PLAYLIST_ICON_DRAWABLE_ID = R.drawable.ic_playlist_play_grey_24dp;

    String FAVORITES_PLAYLIST_ID = "1";
    String LAST_ADDED_PLAYLIST_ID = "2";

    String PLAYBACK_STATE_EXTRA_IS_IN_FAVORITES = "isInFavorites";

    void createPlaylistIdList();

    List<String> getPlaylistIdList();

    Playlist getPlaylistByPlaylistId(String playlistId);

    void createPlaylist(Playlist playlist);

    void addSongIdToPlaylist(String songId, String playlistId);

    void removeSongIdFromPlaylist(String songId, String playlistId);

    void deletePlaylist(String playlistId);
}
