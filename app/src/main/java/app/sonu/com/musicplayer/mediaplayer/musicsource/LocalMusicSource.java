package app.sonu.com.musicplayer.mediaplayer.musicsource;

import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.Iterator;

import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 27/7/17.
 * this class represents local music source
 * @author amanshu
 */

public class LocalMusicSource implements MusicProviderSource {

    private static final String TAG = LocalMusicSource.class.getSimpleName();

    private DataManager mDataManager;

    public LocalMusicSource(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public Iterator<MediaMetadataCompat> getAllSongsIterator() {
        Log.d(TAG, "getAllSongsIterator:called");
        return mDataManager.getSongListFromLocalStorage().iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> getAlbumsIterator() {
        Log.d(TAG, "getAlbumsIterator:called");
        return mDataManager.getAlbumListFromLocalStorage().iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> getArtistsIterator() {
        Log.d(TAG, "getArtistsIterator:called");
        return mDataManager.getArtistListFromLocalStorage().iterator();
    }
}
