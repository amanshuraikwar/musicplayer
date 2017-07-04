package app.sonu.com.musicplayer.ui.allsongs.list;

import java.util.List;

import app.sonu.com.musicplayer.base.list.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.base.list.BaseVisitable;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsRecyclerViewAdapter extends BaseRecyclerViewAdapter<AllSongsListTypeFactory>{

    public AllSongsRecyclerViewAdapter(List<BaseVisitable> elements, AllSongsListTypeFactory typeFactory) {
        super(elements, typeFactory);
    }
}
