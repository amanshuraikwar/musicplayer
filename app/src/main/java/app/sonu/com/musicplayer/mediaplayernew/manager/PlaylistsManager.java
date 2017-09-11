package app.sonu.com.musicplayer.mediaplayernew.manager;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.mediaplayernew.playlistssource.Playlist;
import app.sonu.com.musicplayer.mediaplayernew.playlistssource.PlaylistsSource;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaListHelper;
import app.sonu.com.musicplayer.util.PlaylistHelper;
import app.sonu.com.musicplayer.util.UniqueIdGenerator;

/**
 * Created by sonu on 6/9/17.
 */

public class PlaylistsManager {

    private static final String TAG = PlaylistsManager.class.getSimpleName();

    private PlaylistsSource mPlaylistsSource;
    private MusicProvider mMusicProvider;
    private PlaylistsCallback mPlaylistsCallback;

    private final ConcurrentMap<String, MediaMetadataCompat> mAllPlaylistsByKey;
    private final ConcurrentMap<String, List<MediaMetadataCompat>> mPlaylistSongsByPlaylistKey;

    @SuppressWarnings("WeakerAccess")
    public enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    //defining volatile to make it thread safe but not blocking
    private volatile PlaylistsManager.State mCurrentState = PlaylistsManager.State.NON_INITIALIZED;

    public PlaylistsManager(@NonNull PlaylistsSource playlistsSource,
                            @NonNull MusicProvider musicProvider,
                            PlaylistsCallback playlistsCallback) {
        mPlaylistsSource = playlistsSource;
        mMusicProvider = musicProvider;
        mPlaylistsCallback = playlistsCallback;

        mPlaylistSongsByPlaylistKey = new ConcurrentHashMap<>();
        mAllPlaylistsByKey = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("WeakerAccess")
    public void retrievePlaylistsAsync(@NonNull final Callback callback) {
        Log.d(TAG, "retrievePlaylistsAsync:called");
        if (mCurrentState == PlaylistsManager.State.INITIALIZED) {

            // Nothing to do, execute callback immediately
            callback.onPlaylistCatalogReady(true);
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, PlaylistsManager.State>() {
            @Override
            protected PlaylistsManager.State doInBackground(Void... params) {
                retrievePlaylists();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(PlaylistsManager.State current) {
                callback.onPlaylistCatalogReady(current == PlaylistsManager.State.INITIALIZED);
            }
        }.execute();
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isInitialized() {
        return this.mCurrentState == PlaylistsManager.State.INITIALIZED;
    }

    public synchronized void retrievePlaylists() {
        Log.d(TAG, "retrievePlaylists:called");

        try {
            if (!mMusicProvider.isInitialized()) {
                mMusicProvider.retrieveMedia();
            }

            String playlistKey = PlaylistHelper.createPlaylistId();
            List<MediaMetadataCompat> lastAddedPlaylist = new ArrayList<>(mMusicProvider.getSongs());

            MediaListHelper.sortByDateModified(lastAddedPlaylist);

            mAllPlaylistsByKey.put(
                    playlistKey,
                    PlaylistHelper
                            .createPlaylistMetadata(
                                    playlistKey,
                                    PlaylistsSource.LAST_ADDED_PLAYLIST_TITLE,
                                    lastAddedPlaylist.size(),
                                    PlaylistsSource.LAST_ADDED_PLAYLIST_ICON_DRAWABLE_ID,
                                    PlaylistsSource.LAST_ADDED_PLAYLIST_COLOR,
                                    PlaylistsSource.PLAYLIST_TYPE_AUTO));

            mPlaylistSongsByPlaylistKey.put(playlistKey, lastAddedPlaylist);

            List<String> playlistIds = mPlaylistsSource.getPlaylistIds();

            if (playlistIds == null) {
                mPlaylistsSource.createPlaylistIdList();
                mPlaylistsSource.addNewPlaylist(
                        new Playlist(
                                PlaylistsSource.FAVORITES_PLAYLIST_ID,
                                PlaylistsSource.FAVORITES_PLAYLIST_TITLE,
                                PlaylistsSource.PLAYLIST_TYPE_AUTO,
                                PlaylistsSource.FAVORITES_PLAYLIST_ICON_DRAWABLE_ID,
                                PlaylistsSource.FAVORITES_PLAYLIST_COLOR));
            } else if (playlistIds.size() == 0) {
                    mPlaylistsSource.addNewPlaylist(
                            new Playlist(
                                    PlaylistsSource.FAVORITES_PLAYLIST_ID,
                                    PlaylistsSource.FAVORITES_PLAYLIST_TITLE,
                                    PlaylistsSource.PLAYLIST_TYPE_AUTO,
                                    PlaylistsSource.FAVORITES_PLAYLIST_ICON_DRAWABLE_ID,
                                    PlaylistsSource.FAVORITES_PLAYLIST_COLOR));
            }

            for (String playlistId : mPlaylistsSource.getPlaylistIds()) {
                List<MediaMetadataCompat> metadataList = new ArrayList<>();
                Playlist playlist = mPlaylistsSource.getPlaylistById(Long.parseLong(playlistId));

                if (playlist == null) {
                    continue;
                }

                mAllPlaylistsByKey
                        .put(
                                playlist.getId()+"",
                                PlaylistHelper
                                        .createPlaylistMetadata(
                                                playlist.getId()+"",
                                                playlist.getTitle(),
                                                playlist.getMusicMediaIds().size(),
                                                playlist.getIconDrawableId(),
                                                playlist.getColor(),
                                                playlist.getType()));

                for (String mediaId : playlist.getMusicMediaIds()) {
                    metadataList.add(mMusicProvider.getMusic(mediaId));
                }
                mPlaylistSongsByPlaylistKey.put(playlist.getId()+"", metadataList);
            }

            mCurrentState = PlaylistsManager.State.INITIALIZED;
        } catch (Exception e){
            Log.e(TAG, "retrieveMedia:", e);
            e.printStackTrace();
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                Log.w(TAG, "retrieveMedia:state is not initialized");
                // setting state to non-initialized to allow retires
                // if something bad happened
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public List<MediaBrowserCompat.MediaItem> getChildren(@NonNull String mediaId) {
        Log.d(TAG, "getChildren:called mediaId="+mediaId);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (!MediaIdHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MediaIdHelper.MEDIA_ID_PLAYLISTS.equals(mediaId)) {// all playlists
            for (MediaMetadataCompat metadata : getPlaylists()) {
                mediaItems.add(createBrowsableMediaItemForPlaylist(metadata));
            }
        } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_PLAYLISTS)) { // songs in one playlist
            String playlistKey = MediaIdHelper.getHierarchy(mediaId)[1];

            for (MediaMetadataCompat metadata : getMusicsByPlaylistKey(playlistKey)) {
                mediaItems.add(createMediaItem(metadata, playlistKey));
            }
        }

        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata,
                                                         String playlistKey) {
        Bundle extras = new Bundle();

        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                MediaIdHelper.MEDIA_ID_PLAYLISTS,
                playlistKey);

        String subtitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(subtitle);

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    private Iterable<MediaMetadataCompat> getPlaylists() {
        return mAllPlaylistsByKey.values();
    }

    public Iterable<MediaMetadataCompat> getMusicsByPlaylistKey(String key) {
        return mPlaylistSongsByPlaylistKey.get(key);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForPlaylist(
            MediaMetadataCompat metadata) {

        Bundle extras = new Bundle();

        extras.putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_COLOR,
                metadata.getLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_COLOR));

        extras.putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                metadata.getLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID));

        extras.putLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_TYPE,
                metadata.getLong(PlaylistsSource.CUSTOM_METADATA_KEY_PLAYLIST_TYPE));

        extras.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                null,
                MediaIdHelper.MEDIA_ID_PLAYLISTS,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        );

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    public void addToFavorites(String mediaId) {
        if (isSongInFavourites(mediaId)) {
            return;
        }

        mPlaylistSongsByPlaylistKey
                .get(PlaylistsSource.FAVORITES_PLAYLIST_ID+"")
                .add(mMusicProvider.getMusic(mediaId));
        mPlaylistsSource.addMusicMediaIdToPlaylist(PlaylistsSource.FAVORITES_PLAYLIST_ID, mediaId);

        Playlist playlist = mPlaylistsSource.getPlaylistById(PlaylistsSource.FAVORITES_PLAYLIST_ID);

        mAllPlaylistsByKey
                .put(
                        playlist.getId()+"",
                        PlaylistHelper
                                .createPlaylistMetadata(
                                        playlist.getId()+"",
                                        playlist.getTitle(),
                                        playlist.getMusicMediaIds().size(),
                                        playlist.getIconDrawableId(),
                                        playlist.getColor(),
                                        playlist.getType()));

        if (mPlaylistsCallback != null) {
            mPlaylistsCallback.onFavoriteStatusChange(mediaId, true);
            mPlaylistsCallback.onPlaylistsChanged(PlaylistsSource.FAVORITES_PLAYLIST_ID+"");
        }
    }

    public void removeFromFavorites(String mediaId) {
        if (!isSongInFavourites(mediaId)) {
            return;
        }

        mPlaylistSongsByPlaylistKey
                .get(PlaylistsSource.FAVORITES_PLAYLIST_ID+"")
                .remove(mMusicProvider.getMusic(mediaId));
        mPlaylistsSource.removeMusicMediaIdFromPlaylist(PlaylistsSource.FAVORITES_PLAYLIST_ID, mediaId);

        Playlist playlist = mPlaylistsSource.getPlaylistById(PlaylistsSource.FAVORITES_PLAYLIST_ID);

        mAllPlaylistsByKey
                .put(
                        playlist.getId()+"",
                        PlaylistHelper
                                .createPlaylistMetadata(
                                        playlist.getId()+"",
                                        playlist.getTitle(),
                                        playlist.getMusicMediaIds().size(),
                                        playlist.getIconDrawableId(),
                                        playlist.getColor(),
                                        playlist.getType()));

        if (mPlaylistsCallback != null) {
            mPlaylistsCallback.onFavoriteStatusChange(mediaId, false);
            mPlaylistsCallback.onPlaylistsChanged(PlaylistsSource.FAVORITES_PLAYLIST_ID+"");
        }
    }

    public boolean isSongInFavourites(@NonNull String mediaId) {
        for (MediaMetadataCompat metadata :
                mPlaylistSongsByPlaylistKey.get(PlaylistsSource.FAVORITES_PLAYLIST_ID+"")) {
            if (mediaId.equals(metadata.getDescription().getMediaId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public interface Callback {
        void onPlaylistCatalogReady(boolean success);
    }

    public interface  PlaylistsCallback {
        void onFavoriteStatusChange(String mediaId, boolean status);
        void onPlaylistsChanged(String playlistId);
    }
}
