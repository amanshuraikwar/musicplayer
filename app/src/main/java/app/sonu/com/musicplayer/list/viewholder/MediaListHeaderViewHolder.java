package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import app.sonu.com.musicplayer.list.visitable.SearchItemTypeTitleVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class MediaListHeaderViewHolder extends BaseViewHolder<MediaListHeaderVisitable,
        MediaListHeaderOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_list_title;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    @BindView(R.id.titleTv)
    TextView titleTv;

    public MediaListHeaderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final MediaListHeaderVisitable visitable,
                     final MediaListHeaderOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getTitle());

        if (visitable.getIconVisibility()) {
            iconIv.setVisibility(View.VISIBLE);
        } else {
            iconIv.setVisibility(View.GONE);
        }

        iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onIconIvClick();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconIv.setImageDrawable(context.getDrawable(visitable.getIconIvDrawableId()));
        } else {
            iconIv.setImageDrawable(
                    context
                            .getResources()
                            .getDrawable(visitable.getIconIvDrawableId()));
        }
    }
}
