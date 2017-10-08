package app.sonu.com.musicplayer;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import app.sonu.com.musicplayer.di.component.DaggerBusComponent;
import app.sonu.com.musicplayer.di.module.BusModule;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 14/9/17.
 */

public class AppBus {

    @Inject
    @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL_STATE)
    public PublishSubject<Integer> musicPlayerPanelStateSubject;

    @Inject
    @Named(BusModule.PROVIDER_MUSIC_PLAYER_PANEL_STATE_CHANGED)
    public PublishSubject<SlidingUpPanelLayout.PanelState> musicPlayerPanelStateChangedSubject;

    @Inject
    @Named(BusModule.PROVIDER_ALBUM_CLICK)
    public PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> albumClickSubject;

    @Inject
    @Named(BusModule.PROVIDER_ARTIST_CLICK)
    public PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> artistClickSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLIST_CLICK)
    public PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> playlistClickSubject;

    @Inject
    @Named(BusModule.PROVIDER_QUEUE_INDEX_UPDATED)
    public PublishSubject<Integer> queueIndexUpdatedSubject;

    @Inject
    @Named(BusModule.PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
    public PublishSubject<Integer> allSongsScrollToTopSubject;

    @Inject
    @Named(BusModule.PROVIDER_ALBUMS_SCROLL_TO_TOP)
    public PublishSubject<Integer> albumsScrollToTopSubject;

    @Inject
    @Named(BusModule.PROVIDER_ARTISTS_SCROLL_TO_TOP)
    public PublishSubject<Integer> artistsScrollToTopSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLISTS_SCROLL_TO_TOP)
    public PublishSubject<Integer> playlistsScrollToTopSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLIST_UPDATED)
    public PublishSubject<PlaylistUpdate> playlistUpdatedSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLISTS_UPDATED)
    public PublishSubject<List<PlaylistUpdate>> playlistsUpdatedSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLIST_ADDED)
    public PublishSubject<MediaBrowserCompat.MediaItem> playlistAddedSubject;

    @Inject
    @Named(BusModule.PROVIDER_PLAYLIST_REMOVED)
    public PublishSubject<MediaBrowserCompat.MediaItem> playlistRemovedSubject;

    @Inject
    @Named(BusModule.PROVIDER_MEDIALIST_SELECTED)
    public PublishSubject<Integer> medialistSelectedSubject;

    @Inject
    @Named(BusModule.PROVIDER_NAVIGATION_ITEM_SELECTED)
    public PublishSubject<Integer> navigationItemSelectedSubject;

    public AppBus() {
        DaggerBusComponent
                .builder()
                .busModule(new BusModule())
                .build()
                .inject(this);
    }
}
