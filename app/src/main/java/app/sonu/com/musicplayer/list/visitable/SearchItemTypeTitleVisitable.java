package app.sonu.com.musicplayer.list.visitable;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;

/**
 * Created by sonu on 2/7/17.
 */

public class SearchItemTypeTitleVisitable extends BaseVisitable<BaseListItemOnClickListener,
        MediaListTypeFactory> {

    private String title;

    public SearchItemTypeTitleVisitable(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
