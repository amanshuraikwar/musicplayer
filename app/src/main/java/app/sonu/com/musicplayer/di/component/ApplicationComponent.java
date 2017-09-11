package app.sonu.com.musicplayer.di.component;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.module.ApplicationModule;
import app.sonu.com.musicplayer.di.module.BusModule;
import app.sonu.com.musicplayer.di.module.DataModule;
import app.sonu.com.musicplayer.di.module.DbModule;
import app.sonu.com.musicplayer.di.module.LocalStorageModule;
import app.sonu.com.musicplayer.di.module.NetworkModule;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.di.module.PrefsModule;
import dagger.Component;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 9/3/17.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataModule.class, NetworkModule.class,
        PrefsModule.class, LocalStorageModule.class, DbModule.class, BusModule.class})
public interface ApplicationComponent {
    DataManager getDataManager();

    @Named(BusModule.PROVIDER_SELECTED_SONG)
    PublishSubject<MediaBrowserCompat.MediaItem> getSelectedSongSubject();

    @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL)
    PublishSubject<Integer> getMusicPlayerPanelSubject();

    @Named(BusModule.PROVIDER_ALBUM_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getAlbumClickProvider();

    @Named(BusModule.PROVIDER_PLAYLIST_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getPlaylistClickProvider();

    @Named(BusModule.PROVIDER_ARTIST_CLICK)
    PublishSubject<MediaBrowserCompat.MediaItem> getArtistClickProvider();

    @Named(BusModule.PROVIDER_QUEUE_INDEX_UPDATED)
    PublishSubject<Integer> getQueueIndexUpdatedProvider();

    @Named(BusModule.PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAllSongsScrollToTopProvider();

    @Named(BusModule.PROVIDER_ALBUMS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAlbumsScrollToTopProvider();

    @Named(BusModule.PROVIDER_PLAYLISTS_SCROLL_TO_TOP)
    PublishSubject<Integer> getPlaylistsScrollToTopProvider();

    @Named(BusModule.PROVIDER_ARTISTS_SCROLL_TO_TOP)
    PublishSubject<Integer> getArtistsScrollToTopProvider();

    @Named(BusModule.PROVIDER_PLAYLISTS_CHANGED)
    PublishSubject<String> getProviderPlaylistsChangedProvider();
}
