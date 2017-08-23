package app.sonu.com.musicplayer.ui.list.viewholder;

import android.content.Context;
import android.graphics.Color;
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

    private int disabledColor = Color.parseColor("#a6a6a6");
    private int enabledTitleColor = Color.parseColor("#484848");
    private int enabledSubtitleColor = Color.parseColor("#757575");

    @BindView(R.id.indexTv)
    TextView indexTv;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.moreOptionsIv)
    ImageView moreOptionsIv;

    @BindView(R.id.parentRl)
    View parentRl;

    public QueueItemViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final QueueItemVisitable visitable,
                     final QueueItemOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());
        indexTv.setText(visitable.getIndexToDisplay()+"");

        if (visitable.getIndexToDisplay() < 0) {
            titleTv.setTextColor(disabledColor);
            subtitleTv.setTextColor(disabledColor);
            indexTv.setTextColor(disabledColor);
        } else {
            titleTv.setTextColor(enabledTitleColor);
            subtitleTv.setTextColor(enabledSubtitleColor);
            indexTv.setTextColor(enabledSubtitleColor);
        }

        parentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onQueueItemClick(visitable.getMediaItem());
            }
        });
    }
}
