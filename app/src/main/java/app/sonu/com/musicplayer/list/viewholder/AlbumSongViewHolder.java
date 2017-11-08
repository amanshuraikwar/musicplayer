package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumSongVisitable;
import app.sonu.com.musicplayer.util.DurationUtil;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class AlbumSongViewHolder extends BaseViewHolder<AlbumSongVisitable, SongOnClickListener> {

    private static final String TAG = LogHelper.getLogTag(AlbumSongViewHolder.class);

    @LayoutRes
    public static final int LAYOUT = R.layout.item_album_song;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.trackNoTv)
    TextView trackNoTv;

    @BindView(R.id.optionsIb)
    ImageButton optionsIb;

    public AlbumSongViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final AlbumSongVisitable visitable,
                     final SongOnClickListener onClickListener,
                     FragmentActivity activity) {

        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onSongClick(visitable.getMediaItem());
            }
        });

        optionsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "optionsIbOnClick:called");
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

        trackNoTv.setText(
                String.valueOf(extras.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)));

    }
}
