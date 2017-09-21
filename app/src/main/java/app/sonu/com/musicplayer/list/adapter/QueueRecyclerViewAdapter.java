package app.sonu.com.musicplayer.list.adapter;

import java.util.List;

import app.sonu.com.musicplayer.list.base.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;

/**
 * Created by sonu on 2/7/17.
 */

public class QueueRecyclerViewAdapter extends BaseRecyclerViewAdapter<MediaListTypeFactory>{

    public QueueRecyclerViewAdapter(List<BaseVisitable> elements, MediaListTypeFactory typeFactory) {
        super(elements, typeFactory);
    }

    public void updateQueueIndex(int index) {
        int i = 0;
        for (BaseVisitable visitable : getElements()) {
            ((QueueItemVisitable) visitable).setIndexToDisplay(i - index);
            i++;
        }
        notifyDataSetChanged();
    }
}
