package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.visitable.ShuffleAllSongsVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 4/9/17.
 */

public class ShuffleAllSongsViewHolder extends BaseViewHolder<ShuffleAllSongsVisitable,
        BaseListItemOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_shuffle_all_songs;

    @BindView(R.id.parentRl)
    View parentView;

    public ShuffleAllSongsViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ShuffleAllSongsVisitable visitable,
                     final BaseListItemOnClickListener onClickListener,
                     FragmentActivity activity) {
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick();
            }
        });
    }
}
