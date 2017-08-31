package app.sonu.com.musicplayer.ui.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.ui.list.onclicklistener.ArtistOnClickListener;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class ArtistViewHolder extends BaseViewHolder<ArtistVisitable, ArtistOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_artist;

    @BindView(R.id.artistTitleTv)
    TextView artistTitleTv;

    @BindView(R.id.artistSubtitleTv)
    TextView artistSubtitleTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    public ArtistViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final ArtistVisitable visitable,
                     final ArtistOnClickListener onClickListener,
                     Context context) {
        artistTitleTv.setText(visitable.getMediaItem().getDescription().getTitle());

        artistSubtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_album_art_artist).circleCrop();

        if (visitable.getMediaItem().getDescription().getIconUri() != null) {
            Glide.with(context)
                    .load(visitable.getMediaItem().getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iconIv);
        } else {
            Glide.with(context).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(context.getDrawable(R.drawable.default_album_art_artist));
            } else {
                iconIv.setImageDrawable(
                        context
                                .getResources()
                                .getDrawable(R.drawable.default_album_art_artist));
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onArtistClick(visitable.getMediaItem());
            }
        });
    }
}
