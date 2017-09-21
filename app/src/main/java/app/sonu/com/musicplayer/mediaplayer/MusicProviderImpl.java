package app.sonu.com.musicplayer.mediaplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import app.sonu.com.musicplayer.mediaplayer.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.util.MediaListHelper;

/**
 * Created by sonu on 7/9/17.
 */

public class MusicProviderImpl extends MusicProvider {
    private static final String TAG = MusicProviderImpl.class.getSimpleName();

    // for getting all songs in sorted order quickly
    private ArrayList<MediaMetadataCompat> allSongs;
    private ArrayList<MediaMetadataCompat> allAlbums;
    private ArrayList<MediaMetadataCompat> allArtists;

    //defining volatile to make it thread safe but not blocking
    private volatile MusicProvider.State mCurrentState = MusicProvider.State.NON_INITIALIZED;

    public MusicProviderImpl(@NonNull MusicProviderSource musicProviderSource) {
        super(musicProviderSource);
    }

    @Override
    public synchronized void retrieveMedia() {
        Log.d(TAG, "retrieveMedia:called");
        try {
            if (mCurrentState == MusicProvider.State.NON_INITIALIZED) {
                mCurrentState = MusicProvider.State.INITIALIZING;

                Iterator<MediaMetadataCompat> allSongsIterator = mSource.getAllSongsIterator();
                Iterator<MediaMetadataCompat> albumsIterator = mSource.getAlbumsIterator();
                Iterator<MediaMetadataCompat> artistsIterator = mSource.getArtistsIterator();

                // making music caches

                // albumlistbykey has to be formed before making allsongs list
                // album art from this list is used for making song lists
                while (albumsIterator.hasNext()) {
                    MediaMetadataCompat item = albumsIterator.next();
                    mAlbumListByKey.put(
                            item.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY),
                            item
                    );
                }

                while (allSongsIterator.hasNext()) {
                    MediaMetadataCompat item = allSongsIterator.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

                    //setting album art to the song
                    item = setAlbumArt(item);

                    mMusicListById.put(musicId, item);

                    // adding the current song to its album cache
                    String albumKey = item
                            .getString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY);
                    String artistKey = item
                            .getString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY);
                    if (!mMusicListByAlbumKey.containsKey(albumKey)) {
                        mMusicListByAlbumKey.put(albumKey, new ArrayList<MediaMetadataCompat>());
                    }
                    mMusicListByAlbumKey.get(albumKey).add(item);

                    String albumArtUri =
                            item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

                    if (albumArtUri != null) {
                        if (!mArtistArtByKey.containsKey(artistKey)) {
                            mArtistArtByKey.put(artistKey, albumArtUri);
                        }
                    }

                    // adding current song to its artist cache
                    if (!mMusicListByArtistKey.containsKey(artistKey)) {
                        mMusicListByArtistKey.put(artistKey, new ArrayList<MediaMetadataCompat>());
                    }
                    mMusicListByArtistKey.get(artistKey).add(item);
                }

                // forming artist cache
                while (artistsIterator.hasNext()) {
                    MediaMetadataCompat item = artistsIterator.next();
                    mArtistListByKey.put(
                            item.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY),
                            setAlbumArtForArtist(item)
                    );
                }

                // initializing <all> lists cache
                allSongs = new ArrayList<>(mMusicListById.values());
                allAlbums = new ArrayList<>(mAlbumListByKey.values());
                allArtists = new ArrayList<>(mArtistListByKey.values());

                // sorting all the <all> lists according to their display title
                MediaListHelper.sortByTitle(allSongs);
                MediaListHelper.sortByTitle(allAlbums);
                MediaListHelper.sortByTitle(allArtists);

                mCurrentState = MusicProvider.State.INITIALIZED;
            }
        } catch (Exception e) {
            Log.e(TAG, "retrieveMedia:", e);
            e.printStackTrace();
        } finally {
            if (mCurrentState != MusicProvider.State.INITIALIZED) {
                Log.w(TAG, "retrieveMedia:state is not initialized");
                // setting state to non-initialized to allow retires
                // if something bad happened
                mCurrentState = MusicProvider.State.NON_INITIALIZED;
            }
        }
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getChildren(@NonNull String mediaId) {
        Log.d(TAG, "getChildren:called mediaId="+mediaId);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (!MediaIdHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MediaIdHelper.MEDIA_ID_ALL_SONGS.equals(mediaId)) {// all songs list
            for (MediaMetadataCompat metadata : getSongs()) {
                mediaItems.add(
                        createMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALL_SONGS,
                                null)
                );
            }
        } else if (MediaIdHelper.MEDIA_ID_ALBUMS.equals(mediaId)) { // all albums list
            for (MediaMetadataCompat album : getAlbums()) {
                mediaItems.add(createBrowsableMediaItemForAlbum(album));
            }
        } else if (MediaIdHelper.MEDIA_ID_ARTISTS.equals(mediaId)) { // all artists list
            for (MediaMetadataCompat artist : getArtists()) {
                mediaItems.add(createBrowsableMediaItemForArtist(artist));
            }
        } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)) { // songs of an album
            String album = MediaIdHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getMusicsByAlbumKey(album)) {
                mediaItems.add(createMediaItem(metadata,
                        MediaIdHelper.MEDIA_ID_ALBUMS,
                        MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY));
            }
        } else if (mediaId.startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) { // songs of an artist
            String artist = MediaIdHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getMusicsByArtistKey(artist)) {
                mediaItems.add(createMediaItem(metadata,
                        MediaIdHelper.MEDIA_ID_ARTISTS,
                        MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY));
            }
        } else {
            Log.w(TAG, "getChildren:skipping unmatched mediaId: "+mediaId);
        }
        return mediaItems;
    }

    @Override
    public List<MediaMetadataCompat> getAlbums() {
        Log.d(TAG, "getAlbums:called");
        if (mCurrentState != MusicProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }

        return allAlbums;
    }

    @Override
    public List<MediaMetadataCompat> getSongs() {
        Log.d(TAG, "getSongs:called");
        if (mCurrentState != MusicProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }

        return allSongs;
    }

    @Override
    public List<MediaMetadataCompat> getArtists() {
        Log.d(TAG, "getArtists:called");
        if (mCurrentState != MusicProvider.State.INITIALIZED) {
            return Collections.emptyList();
        }
        Log.i(TAG, "getArtists:noOfArtists="+mArtistListByKey.values().size());

        return allArtists;
    }

    @Override
    public List<MediaMetadataCompat> getMusicsByAlbumKey(@NonNull String albumKey) {
        Log.d(TAG, "getMusicsByAlbumKey:called");
        if (mCurrentState != MusicProvider.State.INITIALIZED || !mMusicListByAlbumKey.containsKey(albumKey)) {
            return Collections.emptyList();
        }
        return mMusicListByAlbumKey.get(albumKey);
    }

    @Override
    public List<MediaMetadataCompat> getMusicsByArtistKey(@NonNull String artistKey) {
        Log.d(TAG, "getMusicsByArtistKey:called");
        if (mCurrentState != MusicProvider.State.INITIALIZED || !mMusicListByArtistKey.containsKey(artistKey)) {
            return Collections.emptyList();
        }
        return mMusicListByArtistKey.get(artistKey);
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getItemsBySearchQuery(@NonNull String query) {
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat metadata : getSongs()) {
            if (metadata
                    .getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createSearchMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALL_SONGS)
                );
            }
        }

        for (MediaMetadataCompat metadata : getAlbums()) {
            if (metadata
                    .getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createSearchMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ALBUMS)
                );
            }
        }

        for (MediaMetadataCompat metadata : getArtists()) {
            if (metadata
                    .getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
                    .toLowerCase()
                    .replaceAll("\\s","")
                    .contains(query)
                    ) {
                mediaItems.add(
                        createSearchMediaItem(
                                metadata,
                                MediaIdHelper.MEDIA_ID_ARTISTS)
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
    private MediaMetadataCompat setAlbumArt(MediaMetadataCompat item) {
        // we create a new object as MediaMetadataCompat is immutable
        return new MediaMetadataCompat
                .Builder(item)
                .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        mAlbumListByKey.get(
                                item.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY)
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
                        mArtistArtByKey.get(
                                item.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY)
                        )
                )
                .build();
    }

    /**
     * to create a browsable media item for an album
     * @param metadata metadata from which mediaitem is to be created
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForAlbum(
            MediaMetadataCompat metadata) {

        Bundle extras = new Bundle();

        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        extras.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                null,
                MediaIdHelper.MEDIA_ID_ALBUMS,
                metadata.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY)
        );

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /**
     * to create browsable media item for an artist
     * @param metadata metadata from which mediaitem is to be created
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForArtist(
            MediaMetadataCompat metadata) {

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                null,
                MediaIdHelper.MEDIA_ID_ARTISTS,
                metadata.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY)
        );

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /**
     * to create a playable mediaitem
     * @param metadata metadata from which mediaitem will be created
     * @param byMediaId root media id if which media item is to be created
     * @param byMetadataKey metadata key for creating a hierarchy aware mediaid
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createMediaItem(
            MediaMetadataCompat metadata,
            String byMediaId,
            String byMetadataKey) {

        Bundle extras = new Bundle();

        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        extras.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

        String hierarchyAwareMediaId, subtitle="";

        if (byMediaId.equals(MediaIdHelper.MEDIA_ID_ALL_SONGS)) {
            hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                    byMediaId);
            subtitle = metadata.getString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
        } else {
            hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                    byMediaId,
                    metadata.getString(byMetadataKey));

            if (byMediaId.startsWith(MediaIdHelper.MEDIA_ID_ARTISTS)) {
                subtitle = metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM);
            } else if (byMediaId.startsWith(MediaIdHelper.MEDIA_ID_ALBUMS)) {
                subtitle = metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
            }
        }

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

    /**
     * create a mediaitem for search results
     * @param metadata metadata from which media item will be created
     * @param byMediaId root media id for creating hierarchy aware mediaid
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createSearchMediaItem(
            MediaMetadataCompat metadata,
            String byMediaId) {

        if (byMediaId.equals(MediaIdHelper.MEDIA_ID_ALL_SONGS)) {
            Bundle extras = new Bundle();

            extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
            extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
            extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                    metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            extras.putString(
                    MusicProviderSource.CUSTOM_METADATA_KEY_SEARCH_ITEM_TYPE,
                    MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_SONG);

            String hierarchyAwareMediaId;

            hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                    metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                    byMediaId);

            MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                    .setExtras(extras)
                    .setMediaId(hierarchyAwareMediaId)
                    .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                    .setSubtitle(metadata.getString(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

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

        } else if (byMediaId.equals(MediaIdHelper.MEDIA_ID_ALBUMS)) {
            return createSearchBrowsableMediaItemForAlbum(metadata);
        } else if (byMediaId.equals(MediaIdHelper.MEDIA_ID_ARTISTS)) {
            return createSearchBrowsableMediaItemForArtist(metadata);
        }

        return null;
    }

    /**
     * create artist browsable mediaitem for search results
     * @param metadata metadata from which mediaitem will be created
     * @return build mediaitem
     */
    private MediaBrowserCompat.MediaItem createSearchBrowsableMediaItemForArtist(
            MediaMetadataCompat metadata) {

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                null,
                MediaIdHelper.MEDIA_ID_ARTISTS,
                metadata.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ARTIST_KEY)
        );

        Bundle extras = new Bundle();
        extras.putString(
                MusicProviderSource.CUSTOM_METADATA_KEY_SEARCH_ITEM_TYPE,
                MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_ARTIST);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /**
     * create album browsable mediaitem for search results
     * @param metadata metadata from which mediaitem will be created
     * @return built mediaitem
     */
    private MediaBrowserCompat.MediaItem createSearchBrowsableMediaItemForAlbum(
            MediaMetadataCompat metadata) {

        String hierarchyAwareMediaId = MediaIdHelper.createMediaId(
                null,
                MediaIdHelper.MEDIA_ID_ALBUMS,
                metadata.getString(MusicProviderSource.CUSTOM_METADATA_KEY_ALBUM_KEY)
        );

        Bundle extras = new Bundle();
        extras.putString(
                MusicProviderSource.CUSTOM_METADATA_KEY_SEARCH_ITEM_TYPE,
                MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_ALBUM);

        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
                .setExtras(extras)
                .setMediaId(hierarchyAwareMediaId)
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setSubtitle(metadata.getString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

        if (metadata.getString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) != null) {
            builder.setIconUri(
                    Uri.parse(
                            metadata.getString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        }

        MediaDescriptionCompat descriptionCompat = builder.build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }
}
