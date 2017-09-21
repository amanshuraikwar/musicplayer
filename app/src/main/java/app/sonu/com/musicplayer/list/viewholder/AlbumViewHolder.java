package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.AlbumOnClickListener;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class AlbumViewHolder extends BaseViewHolder<AlbumVisitable, AlbumOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_album;

    @BindView(R.id.albumTitleTv)
    TextView albumTitleTv;

    @BindView(R.id.albumArtistTv)
    TextView albumArtistTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    public AlbumViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final AlbumVisitable visitable,
                     final AlbumOnClickListener onClickListener,
                     Context context) {
        albumTitleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        albumArtistTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_album_art);

        if (visitable.getMediaItem().getDescription().getIconUri() != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(visitable.getMediaItem().getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Bitmap> target,
                                                    boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource,
                                                       Object model,
                                                       Target<Bitmap> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {

                            updateUiColor(resource);
                            return false;
                        }
                    })
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(iconIv);
        } else {
            Glide.with(context).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(context.getDrawable(R.drawable.default_album_art));
            } else {
                iconIv.setImageDrawable(
                        context
                                .getResources()
                                .getDrawable(R.drawable.default_album_art));
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onAlbumClick(visitable.getMediaItem(), iconIv);
            }
        });
    }

    private void updateUiColor(Bitmap resource) {

        if (resource == null) {
            setUiColorWithSwatch(null);
        } else {
            ColorUtil.generatePalette(resource, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setUiColorWithSwatch(ColorUtil.getColorSwatch(palette));
                }
            });
        }
    }

    private void setUiColorWithSwatch(Palette.Swatch swatch) {

        int backgroundColor = ColorUtil.getBackgroundColor(swatch);
        int titleColor = ColorUtil.getTitleColor(swatch);
        int bodyColor = ColorUtil.getBodyColor(swatch);

        parentView.setBackgroundColor(backgroundColor);
        albumTitleTv.setTextColor(titleColor);
        albumArtistTv.setTextColor(bodyColor);
    }
}
