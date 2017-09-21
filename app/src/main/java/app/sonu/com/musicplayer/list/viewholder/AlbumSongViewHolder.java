package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumSongVisitable;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class AlbumSongViewHolder extends BaseViewHolder<AlbumSongVisitable, SongOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_album_song;

    @BindView(R.id.songTitleTv)
    TextView songTitleTv;

    @BindView(R.id.songDurationTv)
    TextView songDurationTv;

    @BindView(R.id.songArtistTv)
    TextView songArtistTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.trackNoTv)
    TextView trackNoTv;

    public AlbumSongViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final AlbumSongVisitable visitable,
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

        songArtistTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        trackNoTv.setText(visitable
                                .getMediaItem()
                                .getDescription()
                                .getExtras()
                                .getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)+"");

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
