package app.sonu.com.musicplayer.ui.list.onclicklistener;

import android.support.v4.media.session.MediaSessionCompat;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;

/**
 * Created by sonu on 16/8/17.
 */

public interface QueueItemOnClickListener extends BaseListItemOnClickListener{
    void onQueueItemClick(MediaSessionCompat.QueueItem item);
}
