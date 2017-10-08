package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.list.adapter.AlbumCoverPagerAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.list.visitable.AlbumArtVisitable;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.ui.addsongstoplaylists.AddSongsToPlaylistsFragment;
import app.sonu.com.musicplayer.ui.albumart.AlbumArtFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;

import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.QueueRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.onclicklistener.QueueItemOnClickListener;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderFragment;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerFragment extends BaseFragment<MusicPlayerMvpPresenter>
        implements MusicPlayerMvpView {

    String TAG = MusicPlayerFragment.class.getSimpleName();

    private MusicPlayerHolderComponent mMusicPlayerHolderComponent;

    @BindView(R.id.musicPlayerSupl)
    SlidingUpPanelLayout musicPlayerSupl;

    @BindView(R.id.musicPlayerUpperHalfRl)
    View musicPlayerUpperHalfRl;

    @BindView(R.id.musicPlayerLowerHalfLl)
    View musicPlayerLowerHalfLl;

    @OnClick(R.id.collapseIv)
    void onCollapseIvClick() {
        mPresenter.onCollapseIvClick();
    }

    @BindView(R.id.heartLikeButton)
    LikeButton heartLikeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicPlayerHolderComponent =
                ((MusicPlayerHolderFragment)getParentFragment()).getMusicPlayerHolderComponent();

        mMusicPlayerHolderComponent
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        mPresenter.onCreate(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        ButterKnife.bind(this, view);

        musicPlayerSupl.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                // todo tooggle small album art
            }
        });

        heartLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                mPresenter.onHeartIvClick();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                mPresenter.onHeartIvClick();
            }
        });

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public MusicPlayerHolderComponent getMusicPlayerHolderComponent() {
        return mMusicPlayerHolderComponent;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFavButtonEnabled() {
        heartLikeButton.setLiked(true);
    }

    @Override
    public void showFavButtonDisabled() {
        heartLikeButton.setLiked(false);
    }

    @Override
    public void metadataChanged() {
        // for working of nested supl
        mPresenter.setAntiDragView(musicPlayerLowerHalfLl);
        mPresenter.setSupl(musicPlayerSupl);
    }

    @Override
    public void setUmanoScrollView(RecyclerView recyclerView) {
        musicPlayerSupl.setScrollableView(recyclerView);
    }
}
