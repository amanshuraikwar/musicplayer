package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaListHeaderViewHolder extends BaseViewHolder<MediaListHeaderVisitable,
        MediaListHeaderOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_media_list_header;

    @BindView(R.id.headerActionBtn)
    Button headerActionBtn;

    @BindView(R.id.titleTv)
    TextView titleTv;

    public MediaListHeaderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final MediaListHeaderVisitable visitable,
                     final MediaListHeaderOnClickListener onClickListener,
                     FragmentActivity activity) {

        titleTv.setText(visitable.getTitle());

        if (visitable.getActionVisibility()) {
            headerActionBtn.setVisibility(View.VISIBLE);
            headerActionBtn.setText(visitable.getActionText());
        } else {
            headerActionBtn.setVisibility(View.GONE);
        }

        if (onClickListener != null) {
            headerActionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onActionClick();
                }
            });
        }
    }
}
