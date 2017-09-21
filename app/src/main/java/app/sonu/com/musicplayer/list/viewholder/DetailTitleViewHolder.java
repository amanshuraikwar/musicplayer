package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.DetailTitleOnClickListener;
import app.sonu.com.musicplayer.list.visitable.DetailTitleVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class DetailTitleViewHolder extends BaseViewHolder<DetailTitleVisitable, DetailTitleOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_detail_title;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.parentRl)
    View parentView;

    public DetailTitleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final DetailTitleVisitable visitable,
                     final DetailTitleOnClickListener onClickListener,
                     Context context) {

        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        parentView.setBackgroundColor(visitable.getBackgroundColor());
        titleTv.setTextColor(visitable.getTitleTextColor());
        subtitleTv.setTextColor(visitable.getSubtitleTextColor());

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });
    }
}
