package app.sonu.com.musicplayer.ui.artist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSongVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderPaddedVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.base.mediaitemdetail.MediaItemDetailFragment;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderActivity;

/**
 * Created by sonu on 21/9/17.
 */

public class ArtistFragment extends MediaItemDetailFragment<ArtistMvpPresenter>
        implements ArtistMvpView {

    private static final String TAG = AlbumFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate:called");

        ((MusicPlayerHolderActivity)getActivity()).getMusicPlayerHolderComponent()
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        mPresenter.onCreate(getActivity(),
                (MediaBrowserCompat.MediaItem) getArguments().getParcelable("mediaItem"));

    }

    @Override
    protected int getDefaultArtId() {
        return R.drawable.default_artist_art_square;
    }

    @Override
    protected List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> songList) {
        Log.d(TAG, "getVisitableList:called");

        List<BaseVisitable> visitableList = new ArrayList<>();

        visitableList.add(new MediaListHeaderPaddedVisitable("Songs", true,
                "SHUFFLE"));

        for (MediaBrowserCompat.MediaItem songItem : songList) {
            ArtistSongVisitable visitable = new ArtistSongVisitable(songItem);
            visitable.setOnClickListener(songOnClickListener);
            visitableList.add(visitable);
        }

        return visitableList;
    }
}
