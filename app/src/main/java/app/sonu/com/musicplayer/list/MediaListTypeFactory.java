package app.sonu.com.musicplayer.list;

import android.view.View;

import app.sonu.com.musicplayer.base.list.BaseTypeFactory;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistViewHolder;
import app.sonu.com.musicplayer.list.viewholder.DetailTitleViewHolder;
import app.sonu.com.musicplayer.list.viewholder.PlaylistViewHolder;
import app.sonu.com.musicplayer.list.viewholder.QueueItemViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumSongViewHolder;
import app.sonu.com.musicplayer.list.viewholder.AlbumViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ArtistSongViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SearchItemTypeTitleViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.ShuffleAllSongsViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SongSearchResultViewHolder;
import app.sonu.com.musicplayer.list.viewholder.SongViewHolder;
import app.sonu.com.musicplayer.list.visitable.AlbumSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumSongVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSongVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.list.visitable.DetailTitleVisitable;
import app.sonu.com.musicplayer.list.visitable.PlaylistVisitable;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.list.visitable.SearchItemTypeTitleVisitable;
import app.sonu.com.musicplayer.list.visitable.SearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.ShuffleAllSongsVisitable;
import app.sonu.com.musicplayer.list.visitable.SongSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.SongVisitable;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaListTypeFactory extends BaseTypeFactory {

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
    public int type(SearchResultVisitable searchVisitable) {
        return SearchResultViewHolder.LAYOUT;
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
    public int type(SearchItemTypeTitleVisitable searchItemTypeTitleVisitable) {
        return SearchItemTypeTitleViewHolder.LAYOUT;
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
    public int type(DetailTitleVisitable detailTitleVisitable) {
        return DetailTitleViewHolder.LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(View parent, int type) {
        BaseViewHolder viewHolder = null;

        switch (type) {
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
            case SearchResultViewHolder.LAYOUT:
                viewHolder = new SearchResultViewHolder(parent);
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
            case SearchItemTypeTitleViewHolder.LAYOUT:
                viewHolder = new SearchItemTypeTitleViewHolder(parent);
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
            case DetailTitleViewHolder.LAYOUT:
                viewHolder = new DetailTitleViewHolder(parent);
                break;
        }

        return viewHolder;
    }
}
