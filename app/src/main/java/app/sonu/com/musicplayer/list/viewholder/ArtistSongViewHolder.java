package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.ArtistSongVisitable;
import app.sonu.com.musicplayer.util.DurationUtil;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class ArtistSongViewHolder extends BaseViewHolder<ArtistSongVisitable, SongOnClickListener> {

    private static final String TAG = LogHelper.getLogTag(ArtistSongViewHolder.class);

    @LayoutRes
    public static final int LAYOUT = R.layout.item_artist_song;

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

    @BindView(R.id.extraInfoTv)
    TextView extraInfoTv;

    public ArtistSongViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final ArtistSongVisitable visitable,
                     final SongOnClickListener onClickListener,
                     Context context) {

        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
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
                onClickListener.onSongClick(visitable.getMediaItem());
            }
        });

        optionsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onOptionsIbClick(visitable.getMediaItem(), optionsIb);
            }
        });

        Bundle extras = visitable
                .getMediaItem()
                .getDescription()
                .getExtras();

        if (extras == null) {
            Log.e(TAG, "bind:extras is null");
            return;
        }

        extraInfoTv.setText(
                DurationUtil.getFormattedDuration(
                        extras.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                )
        );
    }
}
