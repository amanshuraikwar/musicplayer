package app.sonu.com.musicplayer.ui.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.list.onclicklistener.QueueItemOnClickListener;
import app.sonu.com.musicplayer.ui.list.visitable.QueueItemVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 16/8/17.
 */

public class QueueItemViewHolder extends BaseViewHolder<QueueItemVisitable, QueueItemOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_queue;

    @BindView(R.id.indexTv)
    TextView indexTv;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.moreOptionsIv)
    ImageView moreOptionsIv;

    public QueueItemViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(QueueItemVisitable visitable,
                     QueueItemOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());
        indexTv.setText(visitable.getIndexToDisplay()+"");
    }
}
