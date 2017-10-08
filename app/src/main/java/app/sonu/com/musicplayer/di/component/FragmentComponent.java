package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.di.PerFragment;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.ui.addsongstoplaylists.AddSongsToPlaylistsFragment;
import app.sonu.com.musicplayer.ui.artistalbums.ArtistAlbumsFragment;
import app.sonu.com.musicplayer.ui.albumcover.AlbumCoverFragment;
import app.sonu.com.musicplayer.ui.createplaylist.CreatePlaylistFragment;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.artist.ArtistFragment;
import app.sonu.com.musicplayer.ui.home.HomeFragment;
import app.sonu.com.musicplayer.ui.medialists.MediaListsFragment;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.EmptyMusicPlayerHolderFragment;
import app.sonu.com.musicplayer.ui.albums.AlbumsFragment;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.artists.ArtistsFragment;
import app.sonu.com.musicplayer.ui.miniplayer.MiniPlayerFragment;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import app.sonu.com.musicplayer.ui.playbackcontrols.PlaybackControlsFragment;
import app.sonu.com.musicplayer.ui.playingqueue.PlayingQueueFragment;
import app.sonu.com.musicplayer.ui.playlist.PlaylistFragment;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsFragment;
import app.sonu.com.musicplayer.ui.search.SearchFragment;
import dagger.Subcomponent;

/**
 * Created by sonu on 15/9/17.
 */

@PerFragment
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {
    void inject(HomeFragment fragment);
    void inject(MediaListsFragment fragment);
    void inject(EmptyMusicPlayerHolderFragment fragment);
    void inject(AlbumsFragment fragment);
    void inject(AllSongsFragment fragment);
    void inject(ArtistsFragment fragment);
    void inject(MiniPlayerFragment fragment);
    void inject(MusicPlayerFragment fragment);
    void inject(PlaylistsFragment fragment);
    void inject(AlbumFragment fragment);
    void inject(ArtistFragment fragment);
    void inject(PlaylistFragment fragment);
    void inject(SearchFragment fragment);
    void inject(CreatePlaylistFragment fragment);
    void inject(AddSongsToPlaylistsFragment fragment);
    void inject(ArtistAlbumsFragment fragment);
    void inject(AlbumCoverFragment fragment);
    void inject(PlayingQueueFragment fragment);
    void inject(PlaybackControlsFragment fragment);

    @Subcomponent.Builder
    interface Builder {
        Builder fragmentModule(FragmentModule module);
        FragmentComponent build();
    }
}
