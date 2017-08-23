package app.sonu.com.musicplayer.di.module;

import android.support.v4.media.MediaBrowserCompat;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.ApplicationContext;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */
@Module
public class BusModule {

    public static final String PROVIDER_SELECTED_SONG = "PROVIDER_SELECTED_SONG";
    public static final String PROVIDER_PLAY_SONG = "PROVIDER_PLAY_SONG";
    public static final String PROVIDER_MUSIC_PLAYER_SLIDE = "PROVIDER_MUSIC_PLAYER_SLIDE";
    public static final String PROVIDER_MUSIC_PLAYER_PANEL = "PROVIDER_MUSIC_PLAYER_PANEL";
    public static final String PROVIDER_ALBUM_CLICK = "PROVIDER_ALBUM_CLICK";
    public static final String PROVIDER_ARTIST_CLICK = "PROVIDER_ARTIST_CLICK";

    @Provides
    @Singleton
    @Named(PROVIDER_SELECTED_SONG)
    PublishSubject<MediaBrowserCompat.MediaItem> getSelectedSongProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_PLAY_SONG)
    PublishSubject<MediaBrowserCompat.MediaItem> getPlaySongProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_MUSIC_PLAYER_SLIDE)
    PublishSubject<Float> getMusicPlayerSlideProvider() {
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
    PublishSubject<MediaBrowserCompat.MediaItem> getAlbumClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    @Named(PROVIDER_ARTIST_CLICK)
    PublishSubject<MediaBrowserCompat.MediaItem> getArtistClickProvider() {
        return PublishSubject.create();
    }
}
