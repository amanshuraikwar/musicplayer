package app.sonu.com.musicplayer.mediaplayer.playlist;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaListHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import app.sonu.com.musicplayer.util.UniqueIdGenerator;

/**
 * Created by sonu on 6/9/17.
 */

public class PlaylistsManagerImpl extends PlaylistsManager {

    private static final String TAG = PlaylistsManagerImpl.class.getSimpleName();

    public PlaylistsManagerImpl(@NonNull PlaylistsSource playlistsSource,
                                @NonNull MediaProvider mediaProvider,
                                PlaylistsServiceCallback playlistsServiceCallback) {
        super(playlistsSource, mediaProvider, playlistsServiceCallback);
    }

    @Override
    public synchronized void retrievePlaylists() {
        Log.d(TAG, "retrievePlaylists:called");

        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;

                if (!mMediaProvider.isInitialized()) {
                    mMediaProvider.retrieveMedia();
                }

                // creating last added playlist
                List<MediaMetadataCompat> lastAddedPlaylist =
                        new ArrayList<>(mMediaProvider.getSongs());
                MediaListHelper.sortByDateModified(lastAddedPlaylist);

                // putting last added playlist in cache
                mPlaylistListById.put(
                        PlaylistsSource.LAST_ADDED_PLAYLIST_ID,
                        MediaMetadataHelper
                                .buildPlaylistMetadata(
                                        PlaylistsSource.LAST_ADDED_PLAYLIST_ID,
                                        PlaylistsSource.LAST_ADDED_PLAYLIST_TITLE,
                                        lastAddedPlaylist.size(),
                                        PlaylistsSource.LAST_ADDED_PLAYLIST_ICON_DRAWABLE_ID,
                                        PlaylistsSource.LAST_ADDED_PLAYLIST_COLOR,
                                        Playlist.Type.AUTO));

                // putting last added playlist songs in cache
                mSongListByPlaylistId.put(PlaylistsSource.LAST_ADDED_PLAYLIST_ID,
                        lastAddedPlaylist);

                // getting playlist id list from source
                List<String> playlistIdList = mPlaylistsSource.getPlaylistIdList();

                // creating playlist id list if null
                if (playlistIdList == null) {
                    mPlaylistsSource.createPlaylistIdList();
                    playlistIdList = mPlaylistsSource.getPlaylistIdList();
                }

                // creating favorites playlist if not exists
                if (playlistIdList.size() == 0) {
                    mPlaylistsSource.createPlaylist(
                            new Playlist(
                                    PlaylistsSource.FAVORITES_PLAYLIST_ID,
                                    PlaylistsSource.FAVORITES_PLAYLIST_TITLE,
                                    Playlist.Type.AUTO,
                                    new ArrayList<String>(),
                                    PlaylistsSource.FAVORITES_PLAYLIST_ICON_DRAWABLE_ID,
                                    PlaylistsSource.FAVORITES_PLAYLIST_COLOR));
                    playlistIdList = mPlaylistsSource.getPlaylistIdList();
                }

                // creating playlist cache
                for (String playlistId : playlistIdList) {
                    List<MediaMetadataCompat> metadataList = new ArrayList<>();
                    Playlist playlist = mPlaylistsSource.getPlaylistByPlaylistId(playlistId);

                    if (playlist == null) {
                        continue;
                    }

                    // putting playlist in cache
                    mPlaylistListById
                            .put(playlist.getPlaylistId(),
                                    MediaMetadataHelper.buildPlaylistMetadata(playlist));

                    for (String songId : playlist.getSongIdList()) {
                        MediaMetadataCompat songMetadata = mMediaProvider.getSongBySongId(songId);
                        if (songMetadata != null) {
                            metadataList.add(mMediaProvider.getSongBySongId(songId));
                        } else {
                            Log.w(TAG, "retrievePlaylists:songid="+songId+" does not exist in library");
                        }
                    }
                    // putting songs in playlist in cache
                    mSongListByPlaylistId.put(playlistId, metadataList);
                }

                mCurrentState = State.INITIALIZED;
            }
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
    @Override
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
            String playlistId = MediaIdHelper.getHierarchy(mediaId)[1];

            for (MediaMetadataCompat metadata : getSongListByPlaylistId(playlistId)) {
                mediaItems.add(createPlayableMediaItemForSongInPlaylist(metadata, playlistId));
            }
        }

        return mediaItems;
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getPlaylistsForSongId(String songId) {
        Log.d(TAG, "getPlaylistsForSong:called songId="+songId);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat metadata : getPlaylists()) {
            mediaItems
                    .add(createBrowsableMediaItemForPlaylist(metadata,
                            isSongIdInPlaylist(songId, metadata.getDescription().getMediaId())));
        }

        Log.w(TAG, "getPlaylistsForSong:"+mediaItems.size());

        return mediaItems;
    }

    @Override
    public void addSongIdToPlaylist(String songId, String playlistMediaId) {
        Log.d(TAG, "addSongIdToPlaylist:called");
        Log.i(TAG, "addSongIdToPlaylist:songId="+songId+" playlistMediaId="+playlistMediaId);

        String playlistId = MediaIdHelper.getHierarchyId(playlistMediaId);

        // duplicate songs in playlist not allowed
        if (isSongIdInPlaylist(songId, playlistId)) {
            return;
        }

        MediaMetadataCompat songMetadata = mMediaProvider.getSongBySongId(songId);

        // adding song in playlist cache
        mSongListByPlaylistId
                .get(playlistId)
                .add(songMetadata);

        // adding song in playlist source
        mPlaylistsSource.addSongIdToPlaylist(songId, playlistId);

        Playlist playlist = mPlaylistsSource.getPlaylistByPlaylistId(playlistId);

        // updating playlist metadata in cache
        mPlaylistListById
                .put(
                        playlist.getPlaylistId(),
                        MediaMetadataHelper
                                .buildPlaylistMetadata(playlist));

        if (mPlaylistsServiceCallback != null) {
            PlaylistUpdate playlistUpdate = new PlaylistUpdate(playlist);
            playlistUpdate
                    .setMediaId(MediaIdHelper.createHierarchyAwareMediaId(null,
                            MediaIdHelper.MEDIA_ID_PLAYLISTS, playlistId));
            playlistUpdate.addSong(songMetadata);
            mPlaylistsServiceCallback.onPlaylistUpdated(playlistUpdate);
        }
    }

    @Override
    public void removeSongIdFromPlaylist(String songId, String playlistMediaId) {
        Log.d(TAG, "removeSongIdFromPlaylist:called");
        Log.i(TAG, "removeSongIdFromPlaylist:songId="+songId+" playlistMediaId="+playlistMediaId);

        String playlistId = MediaIdHelper.getHierarchyId(playlistMediaId);

        // cannot delete a song which does not exists in playlist
        if (!isSongIdInPlaylist(songId, playlistId)) {
            return;
        }

        MediaMetadataCompat songMetadata = mMediaProvider.getSongBySongId(songId);

        // removing song from playlist in cache
        mSongListByPlaylistId
                .get(playlistId)
                .remove(songMetadata);

        // removing song from playlist in source
        mPlaylistsSource.removeSongIdFromPlaylist(songId, playlistId);

        // updating playlist cache
        Playlist playlist = mPlaylistsSource.getPlaylistByPlaylistId(playlistId);
        mPlaylistListById
                .put(
                        playlist.getPlaylistId(),
                        MediaMetadataHelper
                                .buildPlaylistMetadata(playlist));

        // notify service
        if (mPlaylistsServiceCallback != null) {
            PlaylistUpdate playlistUpdate = new PlaylistUpdate(playlist);
            playlistUpdate
                    .setMediaId(MediaIdHelper.createHierarchyAwareMediaId(null,
                            MediaIdHelper.MEDIA_ID_PLAYLISTS, playlistId));
            playlistUpdate.addRemovedSong(songMetadata);
            mPlaylistsServiceCallback.onPlaylistUpdated(playlistUpdate);
        }
    }

    @Override
    public boolean isSongIdInPlaylist(@NonNull String mediaId,@NonNull String playlistId) {
        Log.w(TAG, "isSongInPlaylist:playlistId"+playlistId);
        for (MediaMetadataCompat metadata :
                mSongListByPlaylistId.get(playlistId)) {
            if (mediaId.equals(metadata.getDescription().getMediaId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createPlaylist(String title) {
        Playlist playlist = new Playlist(
                UniqueIdGenerator.getId()+"",
                title,
                Playlist.Type.USER,
                new ArrayList<String>(),
                PlaylistsSource.DEFAULT_PLAYLIST_ICON_DRAWABLE_ID,
                PlaylistsSource.DEFAULT_PLAYLIST_COLOR);

        // creating playlist in source
        mPlaylistsSource.createPlaylist(playlist);

        MediaMetadataCompat playlistMetadata = MediaMetadataHelper.buildPlaylistMetadata(playlist);

        // adding playlist to cache
        mPlaylistListById
                .put(
                        playlist.getPlaylistId(),
                        playlistMetadata);

        // adding empty song list to the newly created playlist to cache
        mSongListByPlaylistId.put(playlist.getPlaylistId(), new ArrayList<MediaMetadataCompat>());

        // notify service which in turn notifies ui of the change
        if (mPlaylistsServiceCallback != null) {
            mPlaylistsServiceCallback.onPlaylistAdded(
                    createBrowsableMediaItemForPlaylist(playlistMetadata));
        }
    }

    @Override
    public void deletePlaylist(String playlistId) {

        Log.d(TAG, "deletePlaylist:playlistId="+playlistId);

        // deleting playlist from source
        mPlaylistsSource.deletePlaylist(playlistId);

        MediaMetadataCompat playlistMetadata = mPlaylistListById.get(playlistId);

        // removing playlist from cache
        mPlaylistListById.remove(playlistId);
        mSongListByPlaylistId.remove(playlistId);

        // notify service which in turn notifies ui of the change
        if (mPlaylistsServiceCallback != null) {
            mPlaylistsServiceCallback.onPlaylistRemoved(
                    createBrowsableMediaItemForPlaylist(playlistMetadata));
        }
    }



    private MediaBrowserCompat.MediaItem createPlayableMediaItemForSongInPlaylist(
            MediaMetadataCompat metadata,
            String playlistId) {
        Log.d(TAG, "createPlayableMediaItemForSongInPlaylist:called");

        Log.i(TAG, "createPlayableMediaItemForSongInPlaylist:metadata="+metadata);
        Log.i(TAG, "createPlayableMediaItemForSongInPlaylist:playlistId="+playlistId);

        Bundle extras = new Bundle();

        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                MediaIdHelper.MEDIA_ID_PLAYLISTS,
                playlistId);

        String title = MediaMetadataHelper.getSongDisplayTitle(metadata);
        String subtitle = MediaMetadataHelper.getSongDisplaySubtitle(metadata);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(title)
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
        return mPlaylistListById.values();
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForPlaylist(
            MediaMetadataCompat metadata) {

        Bundle extras = new Bundle();

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_COLOR,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_COLOR));

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID));

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE));

        extras.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));

        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                null,
                MediaIdHelper.MEDIA_ID_PLAYLISTS,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        );

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(MediaMetadataHelper.getPlaylistDisplayTitle(metadata))
                .setSubtitle(MediaMetadataHelper.getPlaylistDisplaySubtitle(metadata));

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForPlaylist(
            MediaMetadataCompat metadata, boolean songExistsInPlaylist) {

        Bundle extras = new Bundle();

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_COLOR,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_COLOR));

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID));

        extras.putLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE,
                metadata.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE));

        extras.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));

        extras.putBoolean(MediaMetadataHelper.CUSTOM_METADATA_KEY_IS_SONG_IN_PLAYLIST,
                songExistsInPlaylist);

        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                null,
                MediaIdHelper.MEDIA_ID_PLAYLISTS,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        );

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(MediaMetadataHelper.getPlaylistDisplayTitle(metadata))
                .setSubtitle(MediaMetadataHelper.getPlaylistDisplaySubtitle(metadata));

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }
}
