package app.sonu.com.musicplayer.di.component;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
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

    @Named(BusModule.PROVIDER_PLAY_SONG)
    PublishSubject<MediaBrowserCompat.MediaItem> getPlaySongSubject();

    @Named(BusModule.PROVIDER_MUSIC_PLAYER_SLIDE)
    PublishSubject<Float> getMusicPlayerSlideSubject();

    @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL)
    PublishSubject<Integer> getMusicPlayerPanelSubject();

    @Named(BusModule.PROVIDER_ALBUM_CLICK)
    PublishSubject<MediaBrowserCompat.MediaItem> getAlbumClickProvider();

    @Named(BusModule.PROVIDER_ARTIST_CLICK)
    PublishSubject<MediaBrowserCompat.MediaItem> getArtistClickProvider();
}
