package app.sonu.com.musicplayer.list.visitable;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;

/**
 * Created by sonu on 22/9/17.
 */

public class MediaListHeaderVisitable extends BaseVisitable<MediaListHeaderOnClickListener,
        MediaListTypeFactory> {

    private String title;
    private boolean iconVisibility;
    private int iconIvDrawableId = R.drawable.ic_playlist_add_black_24dp;

    public MediaListHeaderVisitable(String title, boolean iconVisibility, int iconIvDrawableId) {
        this.title = title;
        this.iconVisibility = iconVisibility;
        if (iconIvDrawableId != 0) {
            this.iconIvDrawableId = iconIvDrawableId;
        }
    }

    public String getTitle() {
        return title;
    }

    public boolean getIconVisibility() {
        return iconVisibility;
    }

    public int getIconIvDrawableId() {
        return iconIvDrawableId;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
