package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SearchResultOnClickListener;
import app.sonu.com.musicplayer.list.visitable.SearchResultVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class SearchResultViewHolder extends BaseViewHolder<SearchResultVisitable, SearchResultOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_search_result;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.extraInfoTv)
    TextView extraInfoTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    public SearchResultViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final SearchResultVisitable visitable,
                     final SearchResultOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
//        songDurationTv.setText(
//                getFormattedDuration(
//                        visitable
//                                .getMediaItem()
//                                .getDescription()
//                                .getExtras()
//                                .getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
//                )
//        );

        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (visitable.getMediaItem().getDescription().getIconUri() != null) {
            Glide.with(context)
                    .load(visitable.getMediaItem().getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .into(iconIv);
        } else {
            Glide.with(context).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(context.getDrawable(R.drawable.default_song_art));
            } else {
                iconIv.setImageDrawable(
                        context
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onSearchResultClick(visitable.getMediaItem());
            }
        });
    }
}
