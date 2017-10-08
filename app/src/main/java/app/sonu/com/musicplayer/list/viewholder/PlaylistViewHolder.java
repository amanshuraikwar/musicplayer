package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.PlaylistOnClickListener;
import app.sonu.com.musicplayer.list.visitable.PlaylistVisitable;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsSource;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class PlaylistViewHolder extends BaseViewHolder<PlaylistVisitable, PlaylistOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_playlist;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    @BindView(R.id.optionsIb)
    ImageButton optionsIb;

    public PlaylistViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final PlaylistVisitable visitable,
                     final PlaylistOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        final RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_album_art);

        Bundle bundle = visitable.getMediaItem().getDescription().getExtras();

        if (bundle != null) {
            int iconDrawableId =
                    (int) bundle.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_ICON_DRAWABLE_ID);
            int color =
                    (int) bundle.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_COLOR);

            if (iconDrawableId != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconIv.setImageDrawable(context.getDrawable(iconDrawableId));
                } else {
                    iconIv.setImageDrawable(
                            context
                                    .getResources()
                                    .getDrawable(iconDrawableId));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconIv.setImageDrawable(context.getDrawable(R.drawable.ic_playlist_play_blue_grey_300_24dp));
                } else {
                    iconIv.setImageDrawable(
                            context
                                    .getResources()
                                    .getDrawable(R.drawable.ic_playlist_play_blue_grey_300_24dp));
                }
            }

            if (color != 0) {
                iconIv.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    iconIv
                            .setColorFilter(
                                    context.getResources().getColor(R.color.charcoal, null),
                                    PorterDuff.Mode.SRC_IN);
                } else {
                    iconIv
                            .setColorFilter(
                                    context.getResources().getColor(R.color.charcoal),
                                    PorterDuff.Mode.SRC_IN);
                }
            }
        }

        if (visitable.getPlaylistType() == Playlist.Type.AUTO.hashCode()) {
            optionsIb.setVisibility(View.GONE);
        } else {
            optionsIb.setVisibility(View.VISIBLE);
        }

        optionsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onOptionsIbClick(visitable.getMediaItem(), optionsIb);
            }
        });

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onPlaylistClick(visitable.getMediaItem(), iconIv);
            }
        });
    }
}
