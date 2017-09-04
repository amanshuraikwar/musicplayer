package app.sonu.com.musicplayer.list.visitable;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;

/**
 * Created by sonu on 4/9/17.
 */

public class ShuffleAllSongsVisitable extends BaseVisitable<BaseListItemOnClickListener,
        MediaListTypeFactory>{

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
