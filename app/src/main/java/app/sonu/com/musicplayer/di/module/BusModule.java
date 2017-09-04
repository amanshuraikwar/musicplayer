package app.sonu.com.musicplayer.di.module;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */
@Module
public class BusModule {

    public static final String PROVIDER_SELECTED_SONG = "PROVIDER_SELECTED_SONG";
    public static final String PROVIDER_MUSIC_PLAYER_PANEL = "PROVIDER_MUSIC_PLAYER_PANEL";
    public static final String PROVIDER_ALBUM_CLICK = "PROVIDER_ALBUM_CLICK";
    public static final String PROVIDER_ARTIST_CLICK = "PROVIDER_ARTIST_CLICK";
    public static final String PROVIDER_QUEUE_INDEX_UPDATED = "PROVIDER_QUEUE_INDEX_UPDATED";
    public static final String PROVIDER_ALL_SONGS_SCROLL_TO_TOP = "PROVIDER_ALL_SONGS_SCROLL_TO_TOP";
    public static final String PROVIDER_ALBUMS_SCROLL_TO_TOP = "PROVIDER_ALBUMS_SCROLL_TO_TOP";
    public static final String PROVIDER_ARTISTS_SCROLL_TO_TOP = "PROVIDER_ARTISTS_SCROLL_TO_TOP";

    @Provides
    @Singleton
    @Named(PROVIDER_SELECTED_SONG)
    PublishSubject<MediaBrowserCompat.MediaItem> getSelectedSongProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_MUSIC_PLAYER_PANEL)
    PublishSubject<Integer> getMusicPlayerPanelProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ALBUM_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getAlbumClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ARTIST_CLICK)
    PublishSubject<MediaBrowserCompat.MediaItem> getArtistClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_QUEUE_INDEX_UPDATED)
    PublishSubject<Integer> getQueuIndexUpdatedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAllSongsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ALBUMS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAlbumsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ARTISTS_SCROLL_TO_TOP)
    PublishSubject<Integer> getArtistsScrollToTopProvider() {
        return PublishSubject.create();
    }
}
