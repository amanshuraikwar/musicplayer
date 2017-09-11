package app.sonu.com.musicplayer.di.module;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import javax.inject.Named;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.ActivityContext;
import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.ui.playlist.PlaylistFragment;
import app.sonu.com.musicplayer.ui.playlist.PlaylistMvpPresenter;
import app.sonu.com.musicplayer.ui.playlist.PlaylistPresenter;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsMvpPresenter;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsPresenter;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.ui.album.AlbumMvpPresenter;
import app.sonu.com.musicplayer.ui.album.AlbumPresenter;
import app.sonu.com.musicplayer.ui.albums.AlbumsMvpPresenter;
import app.sonu.com.musicplayer.ui.albums.AlbumsPresenter;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsMvpPresenter;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsPresenter;
import app.sonu.com.musicplayer.ui.artist.ArtistMvpPresenter;
import app.sonu.com.musicplayer.ui.artist.ArtistPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsMvpPresenter;
import app.sonu.com.musicplayer.ui.artists.ArtistsPresenter;
import app.sonu.com.musicplayer.mediaplayernew.manager.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.main.MainMvpPresenter;
import app.sonu.com.musicplayer.ui.main.MainPresenter;

import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerMvpPresenter;
import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerPresenter;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerMvpPresenter;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerPresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 29/6/17.
 */

@Module
public class UiModule {
    private Context mContext;

    public UiModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context getContext() {
        return this.mContext;
    }

    @Provides
    @PerActivity
    MainMvpPresenter getMainMvpPresenter(MainPresenter mainPresenter) {
        return mainPresenter;
    }

    @Provides
    @PerActivity
    MainPresenter getMainPresenter(DataManager dataManager,
                                   @Named(BusModule.PROVIDER_SELECTED_SONG)
                                   PublishSubject<MediaBrowserCompat.MediaItem> selectedSongSubject,
                                   @Named(BusModule.PROVIDER_ALBUM_CLICK)
                                   PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> albumClickSubject,
                                   @Named(BusModule.PROVIDER_ARTIST_CLICK)
                                           PublishSubject<MediaBrowserCompat.MediaItem>
                                           artistClickSubject,
                                   @Named(BusModule.PROVIDER_PLAYLIST_CLICK)
                                           PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>>
                                           playlistClickSubject,
                                   @Named(BusModule.PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
                                           PublishSubject<Integer> allSongsScrollToTopSubject,
                                   @Named(BusModule.PROVIDER_ALBUMS_SCROLL_TO_TOP)
                                           PublishSubject<Integer> albumsScrollToTopSubject,
                                   @Named(BusModule.PROVIDER_PLAYLISTS_SCROLL_TO_TOP)
                                           PublishSubject<Integer> playlistsScrollToTopSubject,
                                   @Named(BusModule.PROVIDER_ARTISTS_SCROLL_TO_TOP)
                                           PublishSubject<Integer> artistsScrollToTopSubject,
                                   @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL)
                                           PublishSubject<Integer> musicPlayerPanelPublishSubject
                                   ) {
        return new MainPresenter(
                dataManager,
                selectedSongSubject,
                albumClickSubject,
                artistClickSubject,
                allSongsScrollToTopSubject,
                albumsScrollToTopSubject,
                artistsScrollToTopSubject,
                playlistClickSubject,
                playlistsScrollToTopSubject,
                musicPlayerPanelPublishSubject,
                new MediaBrowserManager(MediaIdHelper.ALL_SONGS_ROOT_HINT,
                        MainPresenter.class.getSimpleName()));
    }

    @Provides
    @PerActivity
    AllSongsMvpPresenter getAllSongsMvpPresenter(AllSongsPresenter allSongsPresenter) {
        return allSongsPresenter;
    }

    @Provides
    @PerActivity
    AllSongsPresenter getAllSongsPresenter(DataManager dataManager,
                                           @Named(BusModule.PROVIDER_SELECTED_SONG)
                                                   PublishSubject<MediaBrowserCompat.MediaItem>
                                                   selectedSongSubject,
                                           @Named(BusModule.PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
                                                   PublishSubject<Integer> allSongsScrollToTopSubject) {
        return new AllSongsPresenter(dataManager,
                new MediaBrowserManager(
                        MediaIdHelper.ALL_SONGS_ROOT_HINT,
                        AllSongsPresenter.class.getSimpleName()),
                selectedSongSubject,
                allSongsScrollToTopSubject);
    }

    @Provides
    @PerActivity
    MiniPlayerPresenter getMiniPlayerPresenter(DataManager dataManager,
                                               @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL)
                                                       PublishSubject<Integer>
                                                       musicPlayerPanelPublishSubject) {
        return  new MiniPlayerPresenter(
                dataManager,
                new MediaBrowserManager(null, MiniPlayerPresenter.class.getSimpleName()),
                musicPlayerPanelPublishSubject);
    }

    @Provides
    @PerActivity
    MiniPlayerMvpPresenter getMiniPlayerMvpPresenter(MiniPlayerPresenter miniPlayerPresenter) {
        return miniPlayerPresenter;
    }

    @Provides
    @PerActivity
    MusicPlayerMvpPresenter getMusicPlayerMvpPresenter(MusicPlayerPresenter musicPlayerPresenter) {
        return musicPlayerPresenter;
    }

    @Provides
    @PerActivity
    MusicPlayerPresenter getMusicPlayerPresenter(DataManager dataManager,
                                                 @Named(BusModule.PROVIDER_QUEUE_INDEX_UPDATED)
                                                         PublishSubject<Integer>
                                                         queueIndexUpdatedPublishSubject,
                                                 @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL)
                                                         PublishSubject<Integer>
                                                         musicPlayerPanelPublishSubject) {
        return new MusicPlayerPresenter(
                dataManager,
                new MediaBrowserManager(null, MusicPlayerPresenter.class.getSimpleName()),
                queueIndexUpdatedPublishSubject,
                musicPlayerPanelPublishSubject);
    }

    @Provides
    @PerActivity
    AlbumsMvpPresenter getAlbumsMvpPresenter(AlbumsPresenter albumsPresenter) {
        return albumsPresenter;
    }

    @Provides
    @PerActivity
    AlbumsPresenter getAlbumsPresenter(DataManager dataManager,
                                       @Named(BusModule.PROVIDER_ALBUM_CLICK)
                                       PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>>
                                               albumClickSubject,
                                       @Named(BusModule.PROVIDER_ALBUMS_SCROLL_TO_TOP)
                                               PublishSubject<Integer> albumsScrollToTopSubject) {
        return new AlbumsPresenter(
                dataManager,
                new MediaBrowserManager(
                        MediaIdHelper.ALBUMS_ROOT_HINT, AlbumsPresenter.class.getSimpleName()),
                albumClickSubject,
                albumsScrollToTopSubject);
    }

    @Provides
    @PerActivity
    PlaylistsMvpPresenter getPlaylistsMvpPresenter(PlaylistsPresenter playlistsPresenter) {
        return playlistsPresenter;
    }

    @Provides
    @PerActivity
    PlaylistsPresenter getPlaylistsPresenter(DataManager dataManager,
                                         @Named(BusModule.PROVIDER_PLAYLIST_CLICK)
                                                 PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>>
                                                 playlistClickSubject,
                                         @Named(BusModule.PROVIDER_PLAYLISTS_SCROLL_TO_TOP)
                                                 PublishSubject<Integer> playlistsScrollToTopSubject,
                                         @Named(BusModule.PROVIDER_PLAYLISTS_CHANGED)
                                                 PublishSubject<String> playlistsChangedSubject) {
        return new PlaylistsPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.PLAYLISTS_ROOT_HINT,
                        PlaylistsPresenter.class.getSimpleName()),
                playlistsScrollToTopSubject,
                playlistClickSubject,
                playlistsChangedSubject);
    }

    @Provides
    @PerActivity
    ArtistsMvpPresenter getArtistsMvpPresenter(ArtistsPresenter artistsPresenter) {
        return artistsPresenter;
    }

    @Provides
    @PerActivity
    ArtistsPresenter getArtistsPresenter(DataManager dataManager,
                                         @Named(BusModule.PROVIDER_ARTIST_CLICK)
                                                 PublishSubject<MediaBrowserCompat.MediaItem>
                                                 artistClickSubject,
                                         @Named(BusModule.PROVIDER_ARTISTS_SCROLL_TO_TOP)
                                                 PublishSubject<Integer> artistsScrollToTopSubject) {
        return new ArtistsPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ARTISTS_ROOT_HINT,
                        ArtistsPresenter.class.getSimpleName()),
                artistClickSubject,
                artistsScrollToTopSubject);
    }

    @Provides
    @PerActivity
    AlbumMvpPresenter getAlbumMvpPresenter(AlbumPresenter albumPresenter) {
        return albumPresenter;
    }

    @Provides
    @PerActivity
    AlbumPresenter getAlbumPresenter(DataManager dataManager,
                                           @Named(BusModule.PROVIDER_SELECTED_SONG)
                                                   PublishSubject<MediaBrowserCompat.MediaItem>
                                                   selectedSongSubject) {
        return new AlbumPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ALBUMS_ROOT_HINT,
                        AlbumPresenter.class.getSimpleName()),
                selectedSongSubject);
    }

    @Provides
    @PerActivity
    ArtistMvpPresenter getArtistMvpPresenter(ArtistPresenter artistPresenter) {
        return artistPresenter;
    }

    @Provides
    @PerActivity
    ArtistPresenter getArtistPresenter(DataManager dataManager,
                                     @Named(BusModule.PROVIDER_SELECTED_SONG)
                                             PublishSubject<MediaBrowserCompat.MediaItem>
                                             selectedSongSubject) {
        return new ArtistPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.ARTISTS_ROOT_HINT,
                        ArtistPresenter.class.getSimpleName()),
                selectedSongSubject);
    }

    @Provides
    @PerActivity
    PlaylistMvpPresenter getPlaylistMvpPresenter(PlaylistPresenter playlistPresenter) {
        return playlistPresenter;
    }

    @Provides
    @PerActivity
    PlaylistPresenter getPlaylistPresenter(DataManager dataManager,
                                          @Named(BusModule.PROVIDER_SELECTED_SONG)
                                             PublishSubject<MediaBrowserCompat.MediaItem>
                                             selectedSongSubject,
                                           @Named(BusModule.PROVIDER_PLAYLISTS_CHANGED)
                                                   PublishSubject<String> playlistsChangedSubject) {
        return new PlaylistPresenter(dataManager,
                new MediaBrowserManager(MediaIdHelper.PLAYLISTS_ROOT_HINT,
                        PlaylistPresenter.class.getSimpleName()),
                selectedSongSubject, playlistsChangedSubject);
    }
}
