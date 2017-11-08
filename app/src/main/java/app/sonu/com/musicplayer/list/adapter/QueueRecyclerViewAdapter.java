package app.sonu.com.musicplayer.list.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.sonu.com.musicplayer.list.base.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;

/**
 * Created by sonu on 2/7/17.
 */

public class QueueRecyclerViewAdapter extends BaseRecyclerViewAdapter<MediaListTypeFactory>{

    public QueueRecyclerViewAdapter(FragmentActivity activity, MediaListTypeFactory typeFactory,
                                    List<BaseVisitable> elements) {
        super(activity, typeFactory, elements);
    }

    public void updateQueueIndex(int index) {
        int i = 0;
        for (BaseVisitable visitable : getVisitableList()) {
            ((QueueItemVisitable) visitable).setIndexToDisplay(i - index);
            i++;
        }
        notifyDataSetChanged();
    }
}
