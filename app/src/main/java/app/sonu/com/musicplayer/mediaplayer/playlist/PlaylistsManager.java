package app.sonu.com.musicplayer.mediaplayer.playlist;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import app.sonu.com.musicplayer.util.MediaIdHelper;

/**
 * Created by sonu on 30/9/17.
 */

public abstract class PlaylistsManager {

    private static final String TAG = PlaylistsManager.class.getSimpleName();

    PlaylistsSource mPlaylistsSource;
    MediaProvider mMediaProvider;
    PlaylistsServiceCallback mPlaylistsServiceCallback;

    final ConcurrentMap<String, MediaMetadataCompat> mPlaylistListById;
    final ConcurrentMap<String, List<MediaMetadataCompat>> mSongListByPlaylistId;

    @SuppressWarnings("WeakerAccess")
    public enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    //defining volatile to make it thread safe but not blocking
    volatile PlaylistsManagerImpl.State mCurrentState =
            PlaylistsManagerImpl.State.NON_INITIALIZED;

    PlaylistsManager(@NonNull PlaylistsSource playlistsSource,
                                @NonNull MediaProvider mediaProvider,
                                PlaylistsServiceCallback playlistsServiceCallback) {
        mPlaylistsSource = playlistsSource;
        mMediaProvider = mediaProvider;
        mPlaylistsServiceCallback = playlistsServiceCallback;

        mSongListByPlaylistId = new ConcurrentHashMap<>();
        mPlaylistListById = new ConcurrentHashMap<>();
    }

    public void retrievePlaylistsAsync(@NonNull final PlaylistsManager.Callback callback) {
        Log.d(TAG, "retrievePlaylistsAsync:called");
        if (mCurrentState == PlaylistsManagerImpl.State.INITIALIZED) {

            // Nothing to do, execute callback immediately
            callback.onPlaylistCatalogReady(true);
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, State>() {
            @Override
            protected PlaylistsManagerImpl.State doInBackground(Void... params) {
                retrievePlaylists();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(PlaylistsManagerImpl.State current) {
                callback.onPlaylistCatalogReady(current == PlaylistsManagerImpl.State.INITIALIZED);
            }
        }.execute();
    }

    public boolean isInitialized() {
        return this.mCurrentState == PlaylistsManagerImpl.State.INITIALIZED;
    }

    public abstract void retrievePlaylists();

    public abstract List<MediaBrowserCompat.MediaItem> getChildren(@NonNull String mediaId);

    public abstract List<MediaBrowserCompat.MediaItem> getPlaylistsForSongId(String songId);

    public List<MediaBrowserCompat.MediaItem> getPlaylistsForSongMediaId(String songMediaId) {
        return getPlaylistsForSongId(MediaIdHelper.getSongIdFromMediaId(songMediaId));
    }

    public Iterable<MediaMetadataCompat> getSongListByPlaylistId(String playlistId) {
        return mSongListByPlaylistId.get(playlistId);
    }

    public void addSongIdToFavorites(String songId) {
        addSongIdToPlaylist(songId,
                MediaIdHelper.createHierarchyAwareMediaId(null,
                        MediaIdHelper.MEDIA_ID_PLAYLISTS, PlaylistsSource.FAVORITES_PLAYLIST_ID));
    }

    public void removeSongIdFromFavorites(String songId) {
        removeSongIdFromPlaylist(songId, MediaIdHelper.createHierarchyAwareMediaId(null,
                MediaIdHelper.MEDIA_ID_PLAYLISTS, PlaylistsSource.FAVORITES_PLAYLIST_ID));
    }

    public boolean isSongIdInFavourites(String songId) {
        return isSongIdInPlaylist(songId, PlaylistsSource.FAVORITES_PLAYLIST_ID);
    }

    public void addSongMediaIdToPlaylist(String songMediaId, String playlistMediaId) {
        addSongIdToPlaylist(MediaIdHelper.getSongIdFromMediaId(songMediaId), playlistMediaId);
    }

    public abstract void addSongIdToPlaylist(String songId, String playlistMediaId);

    public void removeSongMediaIdFromPlaylist(String songMediaId, String playlistMediaId) {
        removeSongIdFromPlaylist(MediaIdHelper.getSongIdFromMediaId(songMediaId), playlistMediaId);
    }

    public abstract void removeSongIdFromPlaylist(String songId, String playlistMediaId);

    public abstract boolean isSongIdInPlaylist(String songId, String playlistId);

    public abstract void createPlaylist(String playlistTitle);

    public abstract void deletePlaylist(String playlistId);

    public void deletePlaylistByMediaId(String playlistMediaId) {
        deletePlaylist(MediaIdHelper.getHierarchyId(playlistMediaId));
    }

    public interface Callback {
        void onPlaylistCatalogReady(boolean success);
    }

    public interface PlaylistsServiceCallback {
        void onPlaylistUpdated(PlaylistUpdate playlistUpdate);
        void onPlaylistsUpdated(List<PlaylistUpdate> playlistUpdateList);
        void onPlaylistAdded(MediaBrowserCompat.MediaItem playlist);
        void onPlaylistRemoved(MediaBrowserCompat.MediaItem playlist);
    }
}
