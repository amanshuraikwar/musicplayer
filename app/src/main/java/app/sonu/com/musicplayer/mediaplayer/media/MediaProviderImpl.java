package app.sonu.com.musicplayer.mediaplayer.media;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaListHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;

/**
 * Created by sonu on 7/9/17.
 */

public class MediaProviderImpl extends MediaProvider {
    private static final String TAG = MediaProviderImpl.class.getSimpleName();

    // for getting all songs sorted by title quickly
    private ArrayList<MediaMetadataCompat> allSongs;
    private ArrayList<MediaMetadataCompat> allAlbums;
    private ArrayList<MediaMetadataCompat> allArtists;

    public MediaProviderImpl(@NonNull MediaProviderSource mediaProviderSource) {
        super(mediaProviderSource);
    }

    @Override
    public synchronized void retrieveMedia()  {
        Log.d(TAG, "retrieveMedia:called");
        try {
            if (mCurrentState == MediaProvider.State.NON_INITIALIZED) {
                mCurrentState = MediaProvider.State.INITIALIZING;

                Iterator<MediaMetadataCompat> allSongsIterator = mSource.getAllSongsIterator();
                Iterator<MediaMetadataCompat> albumsIterator = mSource.getAlbumsIterator();
                Iterator<MediaMetadataCompat> artistsIterator = mSource.getArtistsIterator();

                // making music caches

                // albumlistbykey has to be formed before making allsongs list
                // album art from this list is used for making song lists

                // building albums cache
                while (albumsIterator.hasNext()) {
                    MediaMetadataCompat item = albumsIterator.next();
                    mAlbumListById.put(
                            item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                            item
                    );
                }

                while (allSongsIterator.hasNext()) {
                    MediaMetadataCompat item = allSongsIterator.next();
                    String songId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

                    //setting album art to the song
                    item = setAlbumArtForSong(item);

                    mSongListById.put(songId, item);

                    // adding the current song to its album cache
                    String albumId = item
                            .getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_ALBUM_ID);
                    String artistId = item
                            .getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_ARTIST_ID);

                    if (!mSongListByAlbumId.containsKey(albumId)) {
                        mSongListByAlbumId.put(albumId, new ArrayList<MediaMetadataCompat>());
                    }
                    mSongListByAlbumId.get(albumId).add(item);

                    String albumArtUri =
                            item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

                    // adding first album art as artist art
                    // this has to be done before forming artist cache
                    // artist art is used from this list to build artist metadata
                    if (albumArtUri != null) {
                        if (!mArtistArtById.containsKey(artistId)) {
                            mArtistArtById.put(artistId, albumArtUri);
                        }
                    }

                    // adding current song to its artist cache
                    if (!mSongListByArtistId.containsKey(artistId)) {
                        mSongListByArtistId.put(artistId, new ArrayList<MediaMetadataCompat>());
                    }
                    mSongListByArtistId.get(artistId).add(item);
                }

                // forming artist cache
                while (artistsIterator.hasNext()) {
                    MediaMetadataCompat item = artistsIterator.next();
                    mArtistListById.put(
                            item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                            setAlbumArtForArtist(item)
                    );
                }

                // initializing <all> lists cache
                allSongs = new ArrayList<>(mSongListById.values());
                allAlbums = new ArrayList<>(mAlbumListById.values());
                allArtists = new ArrayList<>(mArtistListById.values());

                // sorting all the <all> lists according to their title
                MediaListHelper.sortByTitle(allSongs);
                MediaListHelper.sortByTitle(allAlbums);
                MediaListHelper.sortByTitle(allArtists);

                mCurrentState = MediaProvider.State.INITIALIZED;
            }
        } catch (Exception e) {
            Log.e(TAG, "retrieveMedia:", e);
            e.printStackTrace();
        } finally {
            if (mCurrentState != MediaProvider.State.INITIALIZED) {
                Log.w(TAG, "retrieveMedia:state is not initialized");
                // setting state to non-initialized to allow retires
                // if something bad happened
                mCurrentState = MediaProvider.State.NON_INITIALIZED;
            }
        }
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getChildren(@NonNull String mediaId) {
        Log.d(TAG, "getChildren:called mediaId="+mediaId);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // if media id is not browsable then return
        if (!MediaIdHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MediaIdHelper.MEDIA_ID_ALL_SONGS.equals(mediaId)) {// all songs list
            for (MediaMetadataCompat metadata : getSongs()) {
                mediaItems.add(
                        createPlayableMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALL_SONGS)
                );
            }
        } else if (MediaIdHelper.MEDIA_ID_ALBUMS.equals(mediaId)) { // all albums list
            for (MediaMetadataCompat album : getAlbums()) {
                mediaItems.add(createBrowsableMediaItem(album,
                        mediaId,
                        album.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
            }
        } else if (MediaIdHelper.MEDIA_ID_ARTISTS.equals(mediaId)) { // all artists list
            for (MediaMetadataCompat artist : getArtists()) {
                mediaItems.add(createBrowsableMediaItem(artist,
                        mediaId,
                        artist.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
            }
        } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)) { // songs of an album
            String album = MediaIdHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getSongsByAlbumId(album)) {
                mediaItems.add(createPlayableMediaItem(metadata,
                        MediaIdHelper.MEDIA_ID_ALBUMS,
                        MediaIdHelper.getHierarchyId(mediaId)));
            }
        } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) { // songs of an artist
            String artist = MediaIdHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getSongsByArtistId(artist)) {
                mediaItems.add(createPlayableMediaItem(metadata,
                        MediaIdHelper.MEDIA_ID_ARTISTS,
                        MediaIdHelper.getHierarchyId(mediaId)));
            }
        } else {
            Log.w(TAG, "getChildren:skipping unmatched mediaId: "+mediaId);
        }
        return mediaItems;
    }

    @Override
    public List<MediaMetadataCompat> getAlbums() {
        Log.d(TAG, "getAlbums:called");
        if (mCurrentState != MediaProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }

        return allAlbums;
    }

    @Override
    public List<MediaMetadataCompat> getSongs() {
        Log.d(TAG, "getSongs:called");
        if (mCurrentState != MediaProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }

        return allSongs;
    }

    @Override
    public List<MediaMetadataCompat> getArtists() {
        Log.d(TAG, "getArtists:called");
        if (mCurrentState != MediaProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }
        Log.i(TAG, "getArtists:noOfArtists="+ mArtistListById.values().size());

        return allArtists;
    }

    @Override
    public List<MediaMetadataCompat> getSongsByAlbumId(@NonNull String albumId) {
        Log.d(TAG, "getSongsByAlbumId:called");
        if (mCurrentState != MediaProvider.State.INITIALIZED || !mSongListByAlbumId.containsKey(albumId)) {
            return Collections.emptyList();
        }
        return mSongListByAlbumId.get(albumId);
    }

    @Override
    public List<MediaMetadataCompat> getSongsByArtistId(@NonNull String artistId) {
        Log.d(TAG, "getSongsByArtistId:called");
        if (mCurrentState != MediaProvider.State.INITIALIZED || !mSongListByArtistId.containsKey(artistId)) {
            return Collections.emptyList();
        }
        return mSongListByArtistId.get(artistId);
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getItemsBySearchQuery(@NonNull String query) {
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat metadata : getSongs()) {
            if (MediaMetadataHelper.getSongDisplayTitle(metadata)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createPlayableMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALL_SONGS)
                );
            }
        }

        for (MediaMetadataCompat metadata : getAlbums()) {
            if (MediaMetadataHelper.getAlbumDisplayTitle(metadata)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createBrowsableMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALBUMS,
                                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                );
            }
        }

        for (MediaMetadataCompat metadata : getArtists()) {
            if (MediaMetadataHelper.getArtistDisplayTitle(metadata)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createBrowsableMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ARTISTS,
                                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                );
            }
        }
        return mediaItems;
    }

    /**
     * setting album art for a song according to its album key
     * @param item old mediametadata
     * @return new mediametadata with the album art
     */
    private MediaMetadataCompat setAlbumArtForSong(MediaMetadataCompat item) {
        // we create a new object as MediaMetadataCompat is immutable
        return new MediaMetadataCompat
                .Builder(item)
                .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        mAlbumListById.get(
                                item.getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_ALBUM_ID)
                        ).getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
                )
                .build();
    }

    /**
     * setting art for artists, from artistartbykey list
     * @param item original mediaitem
     * @return mediaitem with album art
     */
    private MediaMetadataCompat setAlbumArtForArtist(MediaMetadataCompat item) {
        // we create a new object as MediaMetadataCompat is immutable
        return new MediaMetadataCompat
                .Builder(item)
                .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        mArtistArtById.get(
                                item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        )
                )
                .build();
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItem(
            MediaMetadataCompat metadata,
            String ... hierarchy) {

//        Log.d(TAG, "createBrowsableMediaItem:called");
//        Log.i(TAG, "createBrowsableMediaItem:metadata="+metadata);
//        Log.i(TAG, "createBrowsableMediaItem:hierarchy="+Arrays.toString(hierarchy));

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();

        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                null,
                hierarchy);
        builder.setMediaId(hierarchyAwareMediaId);

        Bundle extras = new Bundle();
        extras.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));

        String title = "", subtitle = "";

        if (hierarchy[0].startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)) {
            extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
            title = MediaMetadataHelper.getAlbumDisplayTitle(metadata);
            subtitle = MediaMetadataHelper.getAlbumDisplaySubtitle(metadata);
        } else if (hierarchy[0].startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) {
            title = MediaMetadataHelper.getArtistDisplayTitle(metadata);
            subtitle = MediaMetadataHelper.getArtistDisplaySubtitle(metadata);
        }

        builder.setTitle(title);
        builder.setSubtitle(subtitle);

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

//        Log.i(TAG, "createBrowsableMediaItem:built description="+descriptionCompat);
//        Log.i(TAG, "createBrowsableMediaItem:built extras="+extras);

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /**
     * to create a playable song mediaitem
     * @param metadata metadata from which mediaitem will be created
     * @param hierarchy hierarchy of media item
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createPlayableMediaItem(
            MediaMetadataCompat metadata,
            String ... hierarchy) {

//        Log.d(TAG, "createPlayableMediaItem:called");
//        Log.i(TAG, "createPlayableMediaItem:metadata="+metadata);
//        Log.i(TAG, "createPlayableMediaItem:hierarchy="+Arrays.toString(hierarchy));

        // adding extras
        Bundle extras = new Bundle();
        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

        String subtitle = "", title = "";
        if (hierarchy[0].startsWith(MediaIdHelper.MEDIA_ID_ALL_SONGS)) {
            title = MediaMetadataHelper.getSongDisplayTitle(metadata);
            subtitle = MediaMetadataHelper.getSongDisplaySubtitle(metadata);
        } else if (hierarchy[0].startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) {
            title = MediaMetadataHelper.getArtistSongDisplayTitle(metadata);
            subtitle = MediaMetadataHelper.getArtistSongDisplaySubtitle(metadata);
        } else if (hierarchy[0].startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)){
            title = MediaMetadataHelper.getAlbumSongDisplayTitle(metadata);
            subtitle = MediaMetadataHelper.getAlbumSongDisplaySubtitle(metadata);
        }

        // getting hierarchy aware media id
        String hierarchyAwareMediaId = MediaIdHelper.createHierarchyAwareMediaId(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                hierarchy);

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

//        Log.i(TAG, "createPlayableMediaItem:built description="+descriptionCompat);
//        Log.i(TAG, "createPlayableMediaItem:built extras="+extras);

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }

}
