package app.sonu.com.musicplayer.ui.adapter;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.list.base.BaseVisitable;

/**
 * Created by sonu on 8/11/17.
 */

public interface MediaListBuilder {
    List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList);
}
