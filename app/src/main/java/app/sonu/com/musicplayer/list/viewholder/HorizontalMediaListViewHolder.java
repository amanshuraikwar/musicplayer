package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.visitable.HorizontalMediaListVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaItemSquareVisitable;
import app.sonu.com.musicplayer.ui.adapter.BaseMediaListAdapter;
import app.sonu.com.musicplayer.ui.adapter.MediaListBuilder;
import app.sonu.com.musicplayer.ui.adapter.simple.SimpleMediaListAdapter;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;

/**
 * Created by sonu on 8/11/17.
 */

public class HorizontalMediaListViewHolder extends BaseViewHolder<HorizontalMediaListVisitable,
        BaseListItemOnClickListener> {

    private static final String TAG = LogHelper.getLogTag(HorizontalMediaListViewHolder.class);

    @LayoutRes
    public static final int LAYOUT = R.layout.item_horizontal_media_list;

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    public HorizontalMediaListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final HorizontalMediaListVisitable visitable,
                     BaseListItemOnClickListener onClickListener, FragmentActivity activity) {
        Log.d(TAG, "bind:called");
        itemsRv.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false));
        itemsRv.setAdapter(new SimpleMediaListAdapter(activity,
                new MediaListTypeFactory(),
                visitable.getMediaId(), visitable.getListBuilder()));
    }
}
