package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.ArtistSongVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class ArtistSongViewHolder extends BaseViewHolder<ArtistSongVisitable, SongOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_artist_song;

    @BindView(R.id.songTitleTv)
    TextView songTitleTv;

    @BindView(R.id.songDurationTv)
    TextView songDurationTv;

    @BindView(R.id.songAlbumTv)
    TextView songAlbumTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    public ArtistSongViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final ArtistSongVisitable visitable,
                     final SongOnClickListener onClickListener,
                     Context context) {
        songTitleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        songDurationTv.setText(
                getFormattedDuration(
                        visitable
                                .getMediaItem()
                                .getDescription()
                                .getExtras()
                                .getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                )
        );

        songAlbumTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

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
    }

    private String getFormattedDuration(long duration) {
        duration = duration/1000;
        String formattedDuration = "";

        formattedDuration += duration/60;
        formattedDuration += ":";
        formattedDuration += String.format("%02d", duration%60);

        return formattedDuration;
    }
}
