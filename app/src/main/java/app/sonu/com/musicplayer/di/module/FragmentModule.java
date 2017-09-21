package app.sonu.com.musicplayer.di.module;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.PerFragment;
import app.sonu.com.musicplayer.mediaplayer.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.album.AlbumMvpPresenter;
import app.sonu.com.musicplayer.ui.album.AlbumPresenter;
import app.sonu.com.musicplayer.ui.artist.ArtistMvpPresenter;
import app.sonu.com.musicplayer.ui.artist.ArtistPresenter;
import app.sonu.com.musicplayer.ui.home.HomeMvpPresenter;
import app.sonu.com.musicplayer.ui.home.HomePresenter;
import app.sonu.com.musicplayer.ui.medialists.MediaListsMvpPresenter;
import app.sonu.com.musicplayer.ui.medialists.MediaListsPresenter;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderMvpPresenter;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderPresenter;
import app.sonu.com.musicplayer.ui.albums.AlbumsMvpPresenter;
import app.sonu.com.musicplayer.ui.albums.AlbumsPresenter;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsMvpPresenter;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsMvpPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsPresenter;
import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerMvpPresenter;
import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerPresenter;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerMvpPresenter;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerPresenter;
import app.sonu.com.musicplayer.ui.playlist.PlaylistMvpPresenter;
import app.sonu.com.musicplayer.ui.playlist.PlaylistPresenter;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsMvpPresenter;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsPresenter;
import app.sonu.com.musicplayer.ui.search.SearchMvpPresenter;
import app.sonu.com.musicplayer.ui.search.SearchPresenter;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 15/9/17.
 */
@Module
public class FragmentModule {
    @Provides
    @PerFragment
    AllSongsMvpPresenter getAllSongsMvpPresenter(AllSongsPresenter allSongsPresenter) {
        return allSongsPresenter;
    }

    @Provides
    @PerFragment
    AllSongsPresenter getAllSongsPresenter(DataManager dataManager,
                                           AppBus appBus) {
        return new AllSongsPresenter(dataManager,
                new MediaBrowserManager(
                        MediaIdHelper.ALL_SONGS_ROOT_HINT,
                        AllSongsPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    MiniPlayerPresenter getMiniPlayerPresenter(DataManager dataManager,
                                               AppBus appBus) {
        return  new MiniPlayerPresenter(
                dataManager,
                new MediaBrowserManager(null, MiniPlayerPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    MiniPlayerMvpPresenter getMiniPlayerMvpPresenter(MiniPlayerPresenter miniPlayerPresenter) {
        return miniPlayerPresenter;
    }

    @Provides
    @PerFragment
    MusicPlayerMvpPresenter getMusicPlayerMvpPresenter(MusicPlayerPresenter musicPlayerPresenter) {
        return musicPlayerPresenter;
    }

    @Provides
    @PerFragment
    MusicPlayerPresenter getMusicPlayerPresenter(DataManager dataManager,
                                                 AppBus appBus,
                                                 PerSlidingUpPanelBus slidingUpPanelBus) {
        return new MusicPlayerPresenter(
                dataManager,
                new MediaBrowserManager(null, MusicPlayerPresenter.class.getSimpleName()),
                appBus,
                slidingUpPanelBus);
    }

    @Provides
    @PerFragment
    AlbumsMvpPresenter getAlbumsMvpPresenter(AlbumsPresenter albumsPresenter) {
        return albumsPresenter;
    }

    @Provides
    @PerFragment
    AlbumsPresenter getAlbumsPresenter(DataManager dataManager,
                                       AppBus appBus) {
        return new AlbumsPresenter(
                dataManager,
                new MediaBrowserManager(
                        MediaIdHelper.ALBUMS_ROOT_HINT, AlbumsPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    PlaylistsMvpPresenter getPlaylistsMvpPresenter(PlaylistsPresenter playlistsPresenter) {
        return playlistsPresenter;
    }

    @Provides
    @PerFragment
    PlaylistsPresenter getPlaylistsPresenter(DataManager dataManager,
                                             AppBus appBus) {
        return new PlaylistsPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.PLAYLISTS_ROOT_HINT,
                        PlaylistsPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    ArtistsMvpPresenter getArtistsMvpPresenter(ArtistsPresenter artistsPresenter) {
        return artistsPresenter;
    }

    @Provides
    @PerFragment
    ArtistsPresenter getArtistsPresenter(DataManager dataManager,
                                         AppBus appBus) {
        return new ArtistsPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ARTISTS_ROOT_HINT,
                        ArtistsPresenter.class.getSimpleName()),
                appBus);
    }



    @Provides
    @PerFragment
    PlaylistMvpPresenter getPlaylistMvpPresenter(PlaylistPresenter playlistPresenter) {
        return playlistPresenter;
    }

    @Provides
    @PerFragment
    PlaylistPresenter getPlaylistPresenter(DataManager dataManager,
                                           AppBus appBus) {
        return new PlaylistPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.PLAYLISTS_ROOT_HINT,
                        PlaylistPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    HomeMvpPresenter getHomeMusicPlayerHolderMvpPresenter(
            HomePresenter homeMusicPlayerHolderPresenter) {
        return homeMusicPlayerHolderPresenter;
    }

    @Provides
    @PerFragment
    HomePresenter getHomeMusicPlayerHolderPresenter(DataManager dataManager,
                                                    AppBus appBus,
                                                    PerSlidingUpPanelBus slidingUpPanelBus) {
        return new HomePresenter(dataManager,
                new MediaBrowserManager(null,
                        HomePresenter.class.getSimpleName()),
                appBus,
                slidingUpPanelBus);
    }


    @Provides
    @PerFragment
    MediaListsMvpPresenter getHomeMusicPlayerHolderChildMvpPresenter(
            MediaListsPresenter mediaListsPresenter) {
        return mediaListsPresenter;
    }

    @Provides
    @PerFragment
    MediaListsPresenter getHomeMusicPlayerHolderChildPresenter(
            DataManager dataManager, AppBus appBus) {
        return new MediaListsPresenter(dataManager, appBus);
    }

    @Provides
    @PerFragment
    MusicPlayerHolderMvpPresenter getMusicPlayerHolderMvpPresenter(
            MusicPlayerHolderPresenter musicPlayerHolderPresenter) {
        return musicPlayerHolderPresenter;
    }

    @Provides
    @PerFragment
    MusicPlayerHolderPresenter getMusicPlayerHolderPresenter(DataManager dataManager,
                                                             AppBus appBus,
                                                             PerSlidingUpPanelBus slidingUpPanelBus) {
        return new MusicPlayerHolderPresenter(dataManager,
                new MediaBrowserManager(null,
                        HomePresenter.class.getSimpleName()),
                appBus,
                slidingUpPanelBus);
    }

    @Provides
    @PerFragment
    AlbumMvpPresenter getAlbumMvpPresenter(AlbumPresenter albumPresenter) {
        return albumPresenter;
    }

    @Provides
    @PerFragment
    AlbumPresenter getAlbumPresenter(DataManager dataManager,
                                     AppBus appBus) {
        return new app.sonu.com.musicplayer.ui.album.AlbumPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ALBUMS_ROOT_HINT,
                        app.sonu.com.musicplayer.ui.album.AlbumPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    ArtistMvpPresenter getArtistMvpPresenter(ArtistPresenter artistPresenter) {
        return artistPresenter;
    }

    @Provides
    @PerFragment
    ArtistPresenter getArtistPresenter(DataManager dataManager,
                                       AppBus appBus) {
        return new ArtistPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ARTISTS_ROOT_HINT,
                        ArtistPresenter.class.getSimpleName()),
                appBus);
    }

    @Provides
    @PerFragment
    SearchMvpPresenter getSearchMvpPresenter(SearchPresenter searchPresenter) {
        return searchPresenter;
    }

    @Provides
    @PerFragment
    SearchPresenter getSearchresenter(DataManager dataManager,
                                       AppBus appBus) {
        return new SearchPresenter(dataManager,
                new MediaBrowserManager(null,
                        SearchPresenter.class.getSimpleName()),
                appBus);
    }
}
