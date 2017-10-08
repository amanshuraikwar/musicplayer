package app.sonu.com.musicplayer.ui.albumart;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 3/10/17.
 */

public class AlbumArtFragment extends Fragment {

    private static final String TAG = LogHelper.getLogTag(AlbumArtFragment.class);
    private MediaSessionCompat.QueueItem queueItem;

    @BindView(R.id.iconIv)
    ImageView iconIv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");
        View view = inflater.inflate(R.layout.item_album_art, container, false);
        ButterKnife.bind(this, view);

        queueItem = getArguments().getParcelable("queueItem");

        Log.i(TAG, "onCreateView:queueItem"+queueItem);

        final RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (queueItem.getDescription().getIconUri() != null) {
            Glide.with(getContext())
                    .load(queueItem.getDescription().getIconUri().getEncodedPath())
                    .apply(options)
                    .into(iconIv);
        } else {
            Glide.with(getContext()).clear(iconIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconIv.setImageDrawable(getContext().getDrawable(R.drawable.default_album_art));
            } else {
                iconIv.setImageDrawable(
                        getContext()
                                .getResources()
                                .getDrawable(R.drawable.default_album_art));
            }
        }

        return view;
    }

    public MediaSessionCompat.QueueItem getQueueItem() {
        return queueItem;
    }
}
