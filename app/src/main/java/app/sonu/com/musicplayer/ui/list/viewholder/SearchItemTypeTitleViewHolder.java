package app.sonu.com.musicplayer.ui.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongSearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.visitable.SearchItemTypeTitleVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SongSearchResultVisitable;
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
