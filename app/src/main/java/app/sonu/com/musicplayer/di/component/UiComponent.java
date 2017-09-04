package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.mediaplayernew.MusicService;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.albums.AlbumsFragment;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.artist.ArtistFragment;
import app.sonu.com.musicplayer.ui.artists.ArtistsFragment;
import app.sonu.com.musicplayer.ui.main.MainActivity;

import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerFragment;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import dagger.Component;

/**
 * Created by sonu on 29/6/17.
 */
@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = UiModule.class)

public interface UiComponent {
    void inject(MainActivity mainActivity);
    void inject(AllSongsFragment allSongsFragment);
    void inject(AlbumsFragment albumsFragment);
    void inject(ArtistsFragment artistsFragment);
    void inject(MusicPlayerFragment musicPlayerFragment);
    void inject(MiniPlayerFragment miniPlayerFragment);
    void inject(AlbumFragment albumFragment);
    void inject(ArtistFragment artistFragment);
    void inject(MusicService musicService);
}

