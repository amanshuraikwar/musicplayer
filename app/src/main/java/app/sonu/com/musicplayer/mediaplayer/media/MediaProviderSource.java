package app.sonu.com.musicplayer.mediaplayer.media;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

/**
 * Created by sonu on 27/7/17.
 * this interface represents a music provider source
 * if a class wants to be a music source, it must implement this interface
 * @author amanshu
 */

public interface MediaProviderSource {
    Iterator<MediaMetadataCompat> getAllSongsIterator();
    Iterator<MediaMetadataCompat> getAlbumsIterator();
    Iterator<MediaMetadataCompat> getArtistsIterator();
}
