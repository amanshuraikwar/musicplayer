package app.sonu.com.musicplayer.mediaplayer.media;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * responsible for providing music for a specified music source
 * @author amanshu
 */
public abstract class MediaProvider {

    private static final String TAG = MediaProvider.class.getSimpleName();

    // source from where music is taken
    MediaProviderSource mSource;

    // caches of music list
    final ConcurrentMap<String, List<MediaMetadataCompat>> mSongListByArtistId;
    final ConcurrentMap<String, List<MediaMetadataCompat>> mSongListByAlbumId;

    final ConcurrentMap<String, MediaMetadataCompat> mArtistListById;
    final ConcurrentMap<String, MediaMetadataCompat> mAlbumListById;
    final ConcurrentMap<String, MediaMetadataCompat> mSongListById;

    // for storing art for artists
    final ConcurrentHashMap<String, String> mArtistArtById;

    final ConcurrentHashMap<String, List<MediaMetadataCompat>> mAlbumListByArtistId;

    @SuppressWarnings("WeakerAccess")
    public enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED;
    }

    //defining volatile to make it thread safe but not blocking
    volatile MediaProvider.State mCurrentState = MediaProvider.State.NON_INITIALIZED;

    /**
     * constructor
     * @param mediaProviderSource music provider is nothing without a musicsource
     */
    @SuppressWarnings("WeakerAccess")
    public MediaProvider(@NonNull MediaProviderSource mediaProviderSource) {
        this.mSource = mediaProviderSource;

        this.mSongListById = new ConcurrentHashMap<>();
        this.mArtistListById = new ConcurrentHashMap<>();
        this.mAlbumListById = new ConcurrentHashMap<>();
        this.mSongListByAlbumId = new ConcurrentHashMap<>();
        this.mSongListByArtistId = new ConcurrentHashMap<>();
        this.mArtistArtById = new ConcurrentHashMap<>();
        this.mAlbumListByArtistId = new ConcurrentHashMap<>();
    }

    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     * @param callback callback method ia called then done retrieving media
     */
    @SuppressWarnings("WeakerAccess")
    public void retrieveMediaAsync(@NonNull final MediaProvider.Callback callback) {
        Log.d(TAG, "retrieveMediaAsync:called");
        if (mCurrentState == MediaProvider.State.INITIALIZED) {

            // Nothing to do, execute callback immediately
            callback.onMusicCatalogReady(true);
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, MediaProvider.State>() {
            @Override
            protected MediaProvider.State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(MediaProvider.State current) {
                callback.onMusicCatalogReady(current == MediaProvider.State.INITIALIZED);
            }
        }.execute();
    }

    /**
     * tells if the provider is initialized or not
     * @return true if initialized else false
     */
    public boolean isInitialized() {
        return this.mCurrentState == MediaProvider.State.INITIALIZED;
    }

    /**
     * for getting music by mediaid
     * @param songId music id for which metadata will be returned
     * @return metadata of required musicid
     */
    public MediaMetadataCompat getSongBySongId(@NonNull String songId) {
        return mSongListById.containsKey(songId) ? mSongListById.get(songId) : null;
    }

    /**
     * retrieves media
     */
    public abstract void retrieveMedia();

    /**
     * returns children for given root media id
     * @param mediaId root media id
     * @return list of children
     */
    public abstract List<MediaBrowserCompat.MediaItem> getChildren(@NonNull String mediaId);

    /**
     * get all albums by artist id
     * @param artistId artist id
     * @return iterable of albums
     */
    public abstract List<MediaMetadataCompat> getAlbumsByArtist(String artistId);

    /**
     * get all albums in sorted order
     * @return iterable of albums
     */
    public abstract List<MediaMetadataCompat> getAlbums();

    /**
     * get all songs in sorted order
     * @return iterable of songs
     */
    public abstract List<MediaMetadataCompat> getSongs();

    /**
     * get all artists in sorted order
     * @return iterable of artists
     */
    public abstract List<MediaMetadataCompat> getArtists();

    /**
     * get all songs of an album
     * @param albumId key of the album whose songs are required
     * @return iterable of songs of required album
     */
    public abstract List<MediaMetadataCompat> getSongsByAlbumId(@NonNull String albumId);

    /**
     * get all songs of an artist
     * @param artistId key of the artist whose songs are required
     * @return iterable of songs of required artist
     */
    public abstract List<MediaMetadataCompat> getSongsByArtistId(@NonNull String artistId);

    /**
     * getting songs, albums and artists for given search string
     * @param query search string
     * @return list of matching items
     */
    public abstract List<MediaBrowserCompat.MediaItem> getItemsBySearchQuery(@NonNull String query);
    /**
     * callback to tell client that catalog is build when async retrieval is done
     */
    @SuppressWarnings("WeakerAccess")
    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }
}