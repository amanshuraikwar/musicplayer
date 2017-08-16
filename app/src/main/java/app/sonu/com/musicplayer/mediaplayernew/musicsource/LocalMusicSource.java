package app.sonu.com.musicplayer.mediaplayernew.musicsource;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 27/7/17.
 */

public class LocalMusicSource implements MusicProviderSource {

    private static final String TAG = LocalMusicSource.class.getSimpleName();

    private DataManager mDataManager;

    public LocalMusicSource(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public Iterator<MediaMetadataCompat> getAllSongsIterator() {
        return mDataManager.getSongListFromLocalStorage().iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> getAlbumsIterator() {
        return mDataManager.getAlbumListFromLocalStorage().iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> getArtistsIterator() {
        return mDataManager.getArtistListFromLocalStorage().iterator();
    }
}
