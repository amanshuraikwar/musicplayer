package app.sonu.com.musicplayer.ui.list;

import android.view.View;

import app.sonu.com.musicplayer.base.list.BaseTypeFactory;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;

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
        }

        return viewHolder;
    }
}
