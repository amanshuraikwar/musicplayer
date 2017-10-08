package app.sonu.com.musicplayer.ui.artistalbums;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.AlbumOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.ui.base.medialist.MediaListFragment;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderActivity;
import app.sonu.com.musicplayer.util.LogHelper;

/**
 * Created by sonu on 4/10/17.
 */

public class ArtistAlbumsFragment extends MediaListFragment<ArtistAlbumsMvpPresenter>
        implements ArtistAlbumsMvpView{

    private static final String TAG = LogHelper.getLogTag(ArtistAlbumsFragment.class);

    private AlbumOnClickListener albumOnClickListener = new AlbumOnClickListener() {
        @Override
        public void onAlbumClick(MediaBrowserCompat.MediaItem item, View animatingView) {
            Log.d(TAG, "onAlbumClick:currentAlbum=" + item+" "+this);
            mPresenter.onAlbumClicked(item, animatingView);
        }

        @Override
        public void OnClick() {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MusicPlayerHolderActivity)getActivity()).getMusicPlayerHolderComponent()
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        mPresenter.onCreate(getActivity(), getArguments().getString(MusicService.KEY_MEDIA_ID));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(),
                1, GridLayoutManager.HORIZONTAL, false);
    }

    @Override
    protected List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> mediaList) {
        List<BaseVisitable> visitableList = new ArrayList<>();

        for (MediaBrowserCompat.MediaItem item : mediaList) {
            AlbumVisitable albumVisitable = new AlbumVisitable(item);
            albumVisitable.setOnClickListener(albumOnClickListener);
            visitableList.add(albumVisitable);
        }

        return visitableList;
    }
}
