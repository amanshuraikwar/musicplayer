package app.sonu.com.musicplayer.ui.allsongs.list;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class SongViewHolder extends BaseViewHolder<SongVisitable, SongOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_song;

    @BindView(R.id.songTitleTv)
    TextView songTitleTv;

    @BindView(R.id.songDurationTv)
    TextView songDurationTv;

    @BindView(R.id.parentRl)
    View parentView;

    public SongViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final SongVisitable visitable, final SongOnClickListener onClickListener) {
        songTitleTv.setText(visitable.getSong().getTitle());
        songDurationTv.setText(getFormattedDuration(visitable.getSong().getDuration()));

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onSongClick(visitable.getSong());
            }
        });
    }

    private String getFormattedDuration(int duration) {
        duration = duration/1000;
        String formattedDuration = "";

        formattedDuration += duration/60;
        formattedDuration += ":";
        formattedDuration += String.format("%02d", duration%60);

        return formattedDuration;
    }
}
