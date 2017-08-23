package app.sonu.com.musicplayer.ui.list.visitable;

import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongSearchResultOnClickListener;

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
