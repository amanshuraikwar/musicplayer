package app.sonu.com.musicplayer.di.module;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.model.PlaylistUpdate;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */
@Module
public class BusModule {

    public static final String PROVIDER_ALBUM_CLICK = "ALBUM_CLICK";

    public static final String PROVIDER_ARTIST_CLICK = "ARTIST_CLICK";

    public static final String PROVIDER_PLAYLIST_CLICK = "PLAYLIST_CLICK";

    public static final String PROVIDER_QUEUE_INDEX_UPDATED = "QUEUE_INDEX_UPDATED";

    public static final String PROVIDER_ALL_SONGS_SCROLL_TO_TOP = "ALL_SONGS_SCROLL_TO_TOP";

    public static final String PROVIDER_ALBUMS_SCROLL_TO_TOP = "ALBUMS_SCROLL_TO_TOP";

    public static final String PROVIDER_ARTISTS_SCROLL_TO_TOP = "ARTISTS_SCROLL_TO_TOP";

    public static final String PROVIDER_PLAYLISTS_SCROLL_TO_TOP = "PLAYLISTS_SCROLL_TO_TOP";

    public static final String PROVIDER_PLAYLIST_UPDATED = "PLAYLIST_UPDATED";

    public static final String PROVIDER_PLAYLISTS_UPDATED = "PLAYLISTS_UPDATED";

    public static final String PROVIDER_PLAYLIST_ADDED = "PLAYLIST_ADDED";

    public static final String PROVIDER_PLAYLIST_REMOVED = "PLAYLIST_REMOVED";

    public static final String PROVIDER_MUSIC_PLAYER_PANEL_STATE = "MUSIC_PLAYER_PANEL_STATE";

    public static final String PROVIDER_MUSIC_PLAYER_PANEL_STATE_CHANGED = "MUSIC_PLAYER_PANEL_STATE_CHANGED";

    public static final String PROVIDER_MEDIALIST_SELECTED = "MEDIALIST_SELECTED";

    public static final String PROVIDER_NAVIGATION_ITEM_SELECTED = "NAVIGATION_ITEM_SELECTED";

    @Provides
    @Named(PROVIDER_ALBUM_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getAlbumClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_ARTIST_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getArtistClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLIST_CLICK)
    PublishSubject<Pair<MediaBrowserCompat.MediaItem, View>> getPlaylistClickProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_QUEUE_INDEX_UPDATED)
    PublishSubject<Integer> getQueueIndexUpdatedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_ALL_SONGS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAllSongsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_ALBUMS_SCROLL_TO_TOP)
    PublishSubject<Integer> getAlbumsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_ARTISTS_SCROLL_TO_TOP)
    PublishSubject<Integer> getArtistsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLISTS_SCROLL_TO_TOP)
    PublishSubject<Integer> getPlaylistsScrollToTopProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLIST_UPDATED)
    PublishSubject<PlaylistUpdate> getPlaylistUpdatedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLISTS_UPDATED)
    PublishSubject<List<PlaylistUpdate>> getPlaylistsUpdatedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLIST_ADDED)
    PublishSubject<MediaBrowserCompat.MediaItem> getPlaylistAddedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_PLAYLIST_REMOVED)
    PublishSubject<MediaBrowserCompat.MediaItem> getPlaylistRemovedProvider() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_MUSIC_PLAYER_PANEL_STATE)
    PublishSubject<Integer> getMusicPlayerPanelStateSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_MUSIC_PLAYER_PANEL_STATE_CHANGED)
    PublishSubject<SlidingUpPanelLayout.PanelState> getMusicPlayerPanelStateChangedSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_MEDIALIST_SELECTED)
    PublishSubject<Integer> getMedialistSelectedSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_NAVIGATION_ITEM_SELECTED)
    PublishSubject<Integer> getNavigationItemSelectedSubject() {
        return PublishSubject.create();
    }
}
