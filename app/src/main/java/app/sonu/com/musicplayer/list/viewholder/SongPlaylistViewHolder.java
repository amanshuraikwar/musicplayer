
package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongPlaylistOnClickListener;
import app.sonu.com.musicplayer.list.visitable.SongPlaylistVisitable;
import app.sonu.com.musicplayer.util.LogHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class SongPlaylistViewHolder extends BaseViewHolder<SongPlaylistVisitable, SongPlaylistOnClickListener> {

    private static final String TAG = LogHelper.getLogTag(SongPlaylistViewHolder.class);

    @LayoutRes
    public static final int LAYOUT = R.layout.item_song_playlist;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.checkbox)
    CheckBox checkbox;

    @BindView(R.id.titleTv)
    TextView titleTv;

    public SongPlaylistViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final SongPlaylistVisitable visitable,
                     final SongPlaylistOnClickListener onClickListener,
                     Context context) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onPlaylistClick(visitable.getMediaItem());
            }
        });

        Bundle extras = visitable.getMediaItem().getDescription().getExtras();

        if (extras == null) {
            Log.e(TAG, "bind:extras is null");
            return;
        }


        if (extras.getBoolean(MediaMetadataHelper.CUSTOM_METADATA_KEY_IS_SONG_IN_PLAYLIST)) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }
    }
}
