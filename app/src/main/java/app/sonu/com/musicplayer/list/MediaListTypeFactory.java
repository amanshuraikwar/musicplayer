package app.sonu.com.musicplayer.list;

import android.view.View;

import app.sonu.com.musicplayer.list.base.BaseTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumArtViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistViewHolder;
import app.sonu.com.musicplayer.list.viewholder.HorizontalMediaListViewHolder;
import app.sonu.com.musicplayer.list.viewholder.MediaItemSquareViewHolder;
import app.sonu.com.musicplayer.list.viewholder.MediaListHeaderViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SongPlaylistViewHolder;
import app.sonu.com.musicplayer.list.viewholder.PlaylistViewHolder;
import app.sonu.com.musicplayer.list.viewholder.QueueItemViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumSongViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistSongViewHolder;
import app.sonu.com.musicplayer.list.viewholder.MediaListHeaderPaddedViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ShuffleAllSongsViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SongSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SongViewHolder;
import app.sonu.com.musicplayer.list.visitable.AlbumArtVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumSongVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSongVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.list.visitable.HorizontalMediaListVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaItemSquareVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import app.sonu.com.musicplayer.list.visitable.PlaylistVisitable;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderPaddedVisitable;
import app.sonu.com.musicplayer.list.visitable.ShuffleAllSongsVisitable;
import app.sonu.com.musicplayer.list.visitable.SongPlaylistVisitable;
import app.sonu.com.musicplayer.list.visitable.SongSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.SongVisitable;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaListTypeFactory extends BaseTypeFactory {

    public int type(AlbumArtVisitable albumArtVisitable) {
        return AlbumArtViewHolder.LAYOUT;
    }
    public int type(SongVisitable songVisitable) {
        return SongViewHolder.LAYOUT;
    }

    public int type(AlbumVisitable albumVisitable) {
        return AlbumViewHolder.LAYOUT;
    }

    public int type(ArtistVisitable artistVisitable) {
        return ArtistViewHolder.LAYOUT;
    }

    public int type(QueueItemVisitable queueItemVisitable) {
        return QueueItemViewHolder.LAYOUT;
    }

    public int type(SongSearchResultVisitable songSearchResultVisitable) {
        return SongSearchResultViewHolder.LAYOUT;
    }

    public int type(AlbumSearchResultVisitable albumSearchVisitable) {
        return AlbumSearchResultViewHolder.LAYOUT;
    }

    public int type(ArtistSearchResultVisitable artistSearchVisitable) {
        return ArtistSearchResultViewHolder.LAYOUT;
    }

    public int type(MediaListHeaderPaddedVisitable mediaListHeaderPaddedVisitable) {
        return MediaListHeaderPaddedViewHolder.LAYOUT;
    }

    public int type(AlbumSongVisitable albumSongVisitable) {
        return AlbumSongViewHolder.LAYOUT;
    }

    public int type(ArtistSongVisitable artistSongVisitable) {
        return ArtistSongViewHolder.LAYOUT;
    }

    public int type(ShuffleAllSongsVisitable shuffleAllSongsVisitable) {
        return ShuffleAllSongsViewHolder.LAYOUT;
    }

    public int type(PlaylistVisitable playlistVisitable) {
        return PlaylistViewHolder.LAYOUT;
    }

    public int type(MediaListHeaderVisitable mediaListHeaderVisitable) {
        return MediaListHeaderViewHolder.LAYOUT;
    }

    public int type(SongPlaylistVisitable songPlaylistVisitable) {
        return SongPlaylistViewHolder.LAYOUT;
    }

    public int type(HorizontalMediaListVisitable horizontalMediaListVisitable) {
        return HorizontalMediaListViewHolder.LAYOUT;
    }

    public int type(MediaItemSquareVisitable mediaItemSquareVisitable) {
        return MediaItemSquareViewHolder.LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(View parent, int type) {
        BaseViewHolder viewHolder = null;

        switch (type) {
            case AlbumArtViewHolder.LAYOUT:
                viewHolder = new AlbumArtViewHolder(parent);
                break;
            case SongViewHolder.LAYOUT:
                viewHolder = new SongViewHolder(parent);
                break;
            case AlbumViewHolder.LAYOUT:
                viewHolder = new AlbumViewHolder(parent);
                break;
            case ArtistViewHolder.LAYOUT:
                viewHolder = new ArtistViewHolder(parent);
                break;
            case QueueItemViewHolder.LAYOUT:
                viewHolder = new QueueItemViewHolder(parent);
                break;
            case SongSearchResultViewHolder.LAYOUT:
                viewHolder = new SongSearchResultViewHolder(parent);
                break;
            case AlbumSearchResultViewHolder.LAYOUT:
                viewHolder = new AlbumSearchResultViewHolder(parent);
                break;
            case ArtistSearchResultViewHolder.LAYOUT:
                viewHolder = new ArtistSearchResultViewHolder(parent);
                break;
            case MediaListHeaderPaddedViewHolder.LAYOUT:
                viewHolder = new MediaListHeaderPaddedViewHolder(parent);
                break;
            case AlbumSongViewHolder.LAYOUT:
                viewHolder = new AlbumSongViewHolder(parent);
                break;
            case ArtistSongViewHolder.LAYOUT:
                viewHolder = new ArtistSongViewHolder(parent);
                break;
            case ShuffleAllSongsViewHolder.LAYOUT:
                viewHolder = new ShuffleAllSongsViewHolder(parent);
                break;
            case PlaylistViewHolder.LAYOUT:
                viewHolder = new PlaylistViewHolder(parent);
                break;
            case MediaListHeaderViewHolder.LAYOUT:
                viewHolder = new MediaListHeaderViewHolder(parent);
                break;
            case SongPlaylistViewHolder.LAYOUT:
                viewHolder = new SongPlaylistViewHolder(parent);
                break;
            case HorizontalMediaListViewHolder.LAYOUT:
                viewHolder = new HorizontalMediaListViewHolder(parent);
                break;
            case MediaItemSquareViewHolder.LAYOUT:
                viewHolder = new MediaItemSquareViewHolder(parent);
                break;
        }

        return viewHolder;
    }
}
