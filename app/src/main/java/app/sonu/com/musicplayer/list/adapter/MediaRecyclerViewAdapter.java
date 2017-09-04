package app.sonu.com.musicplayer.list.adapter;

import java.util.List;

import app.sonu.com.musicplayer.base.list.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaRecyclerViewAdapter extends BaseRecyclerViewAdapter<MediaListTypeFactory>{

    public MediaRecyclerViewAdapter(List<BaseVisitable> elements, MediaListTypeFactory typeFactory) {
        super(elements, typeFactory);
    }
}
