package app.sonu.com.musicplayer.list.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.base.BaseViewHolder;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.MediaItemSquareOnClickListener;
import app.sonu.com.musicplayer.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.ArtistOnClickListener;
import app.sonu.com.musicplayer.list.visitable.MediaItemSquareVisitable;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.ui.adapter.MediaListBuilder;
import app.sonu.com.musicplayer.ui.adapter.simple.SimpleMediaListAdapter;
import app.sonu.com.musicplayer.util.ColorUtil;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import butterknife.BindView;

/**
 * Created by sonu on 30/7/17.
 */

public class ArtistViewHolder extends BaseViewHolder<ArtistVisitable, ArtistOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_artist;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.parentRl)
    View parentView;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    @BindView(R.id.horizontalRv)
    RecyclerView horizontalRv;

    public ArtistViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final ArtistVisitable visitable,
                     final ArtistOnClickListener onClickListener,
                     final FragmentActivity activity) {
        titleTv.setText(visitable.getMediaItem().getDescription().getTitle());
        subtitleTv.setText(visitable.getMediaItem().getDescription().getSubtitle());

        RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.default_artist_art_square);

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
                iconIv.setImageDrawable(activity.getDrawable(R.drawable.default_artist_art_square));
            } else {
                iconIv.setImageDrawable(
                        activity
                                .getResources()
                                .getDrawable(R.drawable.default_artist_art_square));
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onArtistClick(visitable.getMediaItem(), iconIv);
            }
        });

        horizontalRv.setVisibility(View.GONE);

        iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (horizontalRv.getVisibility() == View.VISIBLE) {
                    horizontalRv.setVisibility(View.GONE);
                } else {
                    horizontalRv.setVisibility(View.VISIBLE);

                    horizontalRv.setLayoutManager(new LinearLayoutManager(activity,
                            LinearLayoutManager.HORIZONTAL, false));
                    horizontalRv.setAdapter(new SimpleMediaListAdapter(activity,
                            new MediaListTypeFactory(),
                            MediaIdHelper
                                    .createHierarchyAwareMediaId(
                                            null,
                                            MediaIdHelper.MEDIA_ID_ALBUMS_OF_ARTIST,
                                            MediaIdHelper
                                                    .getHierarchyId(
                                                            visitable
                                                                    .getMediaItem()
                                                                    .getDescription()
                                                                    .getMediaId())),
                            new MediaListBuilder() {
                                @Override
                                public List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList) {
                                    List<BaseVisitable> visitableList = new ArrayList<>();

                                    for (MediaBrowserCompat.MediaItem item : itemList) {
                                        MediaItemSquareVisitable visitable1 =
                                                new MediaItemSquareVisitable(item, R.drawable.ic_album_grey_24dp);
                                        visitable1.setOnClickListener(new MediaItemSquareOnClickListener() {
                                            @Override
                                            public void onClick(MediaBrowserCompat.MediaItem item) {
                                                onClickListener.onArtistAlbumClick(item);
                                            }

                                            @Override
                                            public void OnClick() {

                                            }
                                        });
                                        visitableList.add(visitable1);
                                    }

                                    return visitableList;
                                }
                    }));
                }
            }
        });
    }
}
