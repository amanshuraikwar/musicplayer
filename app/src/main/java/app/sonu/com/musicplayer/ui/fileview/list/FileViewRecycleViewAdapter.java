package app.sonu.com.musicplayer.ui.fileview.list;

import app.sonu.com.musicplayer.base.list.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.base.list.BaseVisitable;

import java.util.List;

/**
 * Created by sonu on 30/6/17.
 */

public class FileViewRecycleViewAdapter extends BaseRecyclerViewAdapter<FileViewListTypeFactory> {

    public FileViewRecycleViewAdapter(List<BaseVisitable> elements,
                                      FileViewListTypeFactory typeFactory) {
        super(elements, typeFactory);
    }
}
