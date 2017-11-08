package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumArtVisitable;
import app.sonu.com.musicplayer.list.visitable.SongVisitable;
import app.sonu.com.musicplayer.util.DurationUtil;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;

/**
 * Created by sonu on 2/7/17.
 */

public class AlbumArtViewHolder extends BaseViewHolder<AlbumArtVisitable, BaseListItemOnClickListener> {

    private static final String TAG = LogHelper.getLogTag(AlbumArtViewHolder.class);

    @LayoutRes
    public static final int LAYOUT = R.layout.item_album_art;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    public AlbumArtViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final AlbumArtVisitable visitable,
                     final BaseListItemOnClickListener onClickListener,
                     FragmentActivity activity) {

        final RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_song_art);

        if (visitable.getQueueItem().getDescription().getIconUri() != null) {
            Glide.with(activity)
                    .load(visitable.getQueueItem().getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iconIv);
        } else {
            Glide.with(activity).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(activity.getDrawable(R.drawable.default_song_art));
            } else {
                iconIv.setImageDrawable(
                        activity
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
            }
        }

        iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick();
            }
        });
    }
}
