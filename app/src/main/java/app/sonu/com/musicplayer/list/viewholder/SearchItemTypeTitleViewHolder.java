package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.visitable.SearchItemTypeTitleVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class SearchItemTypeTitleViewHolder extends BaseViewHolder<SearchItemTypeTitleVisitable,
        BaseListItemOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_search_item_type_title;

    @BindView(R.id.titleTv)
    TextView titleTv;

    public SearchItemTypeTitleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final SearchItemTypeTitleVisitable visitable,
                     final BaseListItemOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getTitle());

    }
}
