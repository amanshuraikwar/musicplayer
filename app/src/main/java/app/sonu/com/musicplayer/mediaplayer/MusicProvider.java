package app.sonu.com.musicplayer.mediaplayer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.sonu.com.musicplayer.mediaplayer.musicsource.MusicProviderSource;

/**
 * responsible for providing music for a specified music source
 * @author amanshu
 */
public abstract class MusicProvider {

    private static final String TAG = MusicProvider.class.getSimpleName();

    // source from where music is taken
    MusicProviderSource mSource;

    // caches of music list
    final ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByArtistKey;
    final ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByAlbumKey;

    final ConcurrentMap<String, MediaMetadataCompat> mArtistListByKey;
    final ConcurrentMap<String, MediaMetadataCompat> mAlbumListByKey;
    final ConcurrentMap<String, MediaMetadataCompat> mMusicListById;

    // for storing art for artists
    final ConcurrentHashMap<String, String> mArtistArtByKey;



    @SuppressWarnings("WeakerAccess")
    public enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED;
    }

    //defining volatile to make it thread safe but not blocking
    private volatile MusicProvider.State mCurrentState = MusicProvider.State.NON_INITIALIZED;

    /**
     * constructor
     * @param musicProviderSource music provider is nothing without a musicsource
     */
    @SuppressWarnings("WeakerAccess")
    public MusicProvider(@NonNull MusicProviderSource musicProviderSource) {
        this.mSource = musicProviderSource;

        this.mMusicListById = new ConcurrentHashMap<>();
        this.mArtistListByKey = new ConcurrentHashMap<>();
        this.mAlbumListByKey = new ConcurrentHashMap<>();
        this.mMusicListByAlbumKey = new ConcurrentHashMap<>();
        this.mMusicListByArtistKey = new ConcurrentHashMap<>();
        this.mArtistArtByKey = new ConcurrentHashMap<>();
    }

    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     * @param callback callback method ia called then done retrieving media
     */
    @SuppressWarnings("WeakerAccess")
    public void retrieveMediaAsync(@NonNull final MusicProvider.Callback callback) {
        Log.d(TAG, "retrieveMediaAsync:called");
        if (mCurrentState == MusicProvider.State.INITIALIZED) {

            // Nothing to do, execute callback immediately
            callback.onMusicCatalogReady(true);
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, MusicProvider.State>() {
            @Override
            protected MusicProvider.State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(MusicProvider.State current) {
                callback.onMusicCatalogReady(current == MusicProvider.State.INITIALIZED);
            }
        }.execute();
    }

    /**
     * tells if the provider is initialized or not
     * @return true if initialized else false
     */
    public boolean isInitialized() {
        return this.mCurrentState == MusicProvider.State.INITIALIZED;
    }

    /**
     * for getting music by mediaid
     * @param musicId music id for which metadata will be returned
     * @return metadata of required musicid
     */
    public MediaMetadataCompat getMusic(@NonNull String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId) : null;
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
     * @param albumKey key of the album whose songs are required
     * @return iterable of songs of required album
     */
    public abstract List<MediaMetadataCompat> getMusicsByAlbumKey(@NonNull String albumKey);

    /**
     * get all songs of an artist
     * @param artistKey key of the artist whose songs are required
     * @return iterable of songs of required artist
     */
    public abstract List<MediaMetadataCompat> getMusicsByArtistKey(@NonNull String artistKey);

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