package app.sonu.com.musicplayer.ui.list;

import android.view.View;

import app.sonu.com.musicplayer.base.list.BaseTypeFactory;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.AlbumSearchResultViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.AlbumViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.ArtistSearchResultViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.ArtistViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.QueueItemViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.SearchItemTypeTitleViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.SearchResultViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.SongSearchResultViewHolder;
import app.sonu.com.musicplayer.ui.list.viewholder.SongViewHolder;
import app.sonu.com.musicplayer.ui.list.visitable.AlbumSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.ArtistSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SearchItemTypeTitleVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SongSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SongVisitable;

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
        }

        return viewHolder;
    }
}
