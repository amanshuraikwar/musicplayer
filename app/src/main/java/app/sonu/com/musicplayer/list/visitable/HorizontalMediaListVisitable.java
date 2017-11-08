package app.sonu.com.musicplayer.list.visitable;

import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.ui.adapter.MediaListBuilder;

/**
 * Created by sonu on 8/11/17.
 */

public class HorizontalMediaListVisitable
        extends BaseVisitable<BaseListItemOnClickListener, MediaListTypeFactory> {

    private String mediaId;
    private MediaListBuilder listBuilder;

    public HorizontalMediaListVisitable(String mediaId,
                                        MediaListBuilder listBuilder) {
        this.mediaId = mediaId;
        this.listBuilder = listBuilder;
    }

    public String getMediaId() {
        return mediaId;
    }

    public MediaListBuilder getListBuilder() {
        return listBuilder;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
