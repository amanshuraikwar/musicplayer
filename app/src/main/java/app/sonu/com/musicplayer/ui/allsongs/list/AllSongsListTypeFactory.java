package app.sonu.com.musicplayer.ui.allsongs.list;

import android.view.View;

import app.sonu.com.musicplayer.base.list.BaseTypeFactory;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsListTypeFactory extends BaseTypeFactory {

    public int type(SongVisitable songVisitable) {
        return SongViewHolder.LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(View parent, int type) {
        BaseViewHolder viewHolder = null;

        switch (type) {
            case SongViewHolder.LAYOUT:
                viewHolder = new SongViewHolder(parent);
                break;
        }

        return viewHolder;
    }
}
