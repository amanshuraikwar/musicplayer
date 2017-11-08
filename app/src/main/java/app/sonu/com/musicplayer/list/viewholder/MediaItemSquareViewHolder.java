package app.sonu.com.musicplayer.list.viewholder;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.AlbumOnClickListener;
import app.sonu.com.musicplayer.list.onclicklistener.MediaItemSquareOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.visitable.HorizontalMediaListVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaItemSquareVisitable;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class MediaItemSquareViewHolder extends BaseViewHolder<MediaItemSquareVisitable,
        MediaItemSquareOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_media_item_square;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.parentLl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    @BindView(R.id.itemIdentifierIv)
    ImageView itemIdentifierIv;

    public MediaItemSquareViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final MediaItemSquareVisitable visitable,
                     final MediaItemSquareOnClickListener onClickListener,
                     FragmentActivity activity) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_album_art);

        if (visitable.getMediaItem().getDescription().getIconUri() != null) {
            Glide.with(activity)
                    .asBitmap()
                    .load(visitable.getMediaItem().getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(iconIv);
        } else {
            Glide.with(activity).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(activity.getDrawable(R.drawable.default_album_art));
            } else {
                iconIv.setImageDrawable(
                        activity
                                .getResources()
                                .getDrawable(R.drawable.default_album_art));
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(visitable.getMediaItem());
            }
        });

        itemIdentifierIv.setImageDrawable(ContextCompat.getDrawable(activity,
                visitable.getItemIdentifierDrawable()));
    }
}
