package app.sonu.com.musicplayer.list.visitable;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;

/**
 * Created by sonu on 22/9/17.
 */

public class MediaListHeaderVisitable extends BaseVisitable<MediaListHeaderOnClickListener,
        MediaListTypeFactory> {

    private String title;
    private boolean actionVisibility = false;
    private String actionText = "MORE";

    public MediaListHeaderVisitable(String title) {
        this.title = title;
    }

    public MediaListHeaderVisitable(String title, boolean actionVisibility) {
        this.title = title;
        this.actionVisibility = actionVisibility;
    }

    public MediaListHeaderVisitable(String title, boolean actionVisibility, String actionText) {
        this.title = title;
        this.actionVisibility = actionVisibility;
        this.actionText = actionText;
    }

    public String getTitle() {
        return title;
    }

    public boolean getActionVisibility() {
        return actionVisibility;
    }

    public String getActionText() {
        return actionText;
    }

    @Override
    public int type(MediaListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
