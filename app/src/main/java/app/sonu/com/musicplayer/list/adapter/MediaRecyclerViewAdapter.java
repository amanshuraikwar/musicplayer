package app.sonu.com.musicplayer.list.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

import app.sonu.com.musicplayer.list.base.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaRecyclerViewAdapter extends BaseRecyclerViewAdapter<MediaListTypeFactory>{

    public MediaRecyclerViewAdapter(@NonNull FragmentActivity activity,
                                    @NonNull MediaListTypeFactory typeFactory,
                                    @NonNull List<BaseVisitable> elements) {
        super(activity, typeFactory, elements);
    }
}
