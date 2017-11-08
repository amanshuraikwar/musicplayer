package app.sonu.com.musicplayer.ui.base.mediaitemdetail;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.ui.addsongstoplaylists.AddSongsToPlaylistsFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.ui.view.StateAwareAppBarLayout;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 21/9/17.
 */

public abstract class MediaItemDetailFragment<MvpPresenter extends MediaItemDetailFragmentMvpPresenter>
        extends BaseFragment<MvpPresenter> implements MediaItemDetailFragmentMvpView {

    private static final String TAG = MediaItemDetailFragment.class.getSimpleName();

    @BindView(R.id.artIv)
    ImageView artIv;

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    @BindView(R.id.toolbarLayout)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.appBarLayout)
    StateAwareAppBarLayout appBarLayout;

    @BindView(R.id.shuffleFab)
    FloatingActionButton shuffleFab;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.metadataRl)
    View metadataRl;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backIb)
    ImageButton backIb;

    protected int darkColor;

    protected SongOnClickListener songOnClickListener = new SongOnClickListener() {
        @Override
        public void onSongClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongClicked(item);
        }

        @Override
        public void onOptionsIbClick(final MediaBrowserCompat.MediaItem item, View optionsIb) {
            PopupMenu popup = new PopupMenu(getActivity(), optionsIb);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_song_options, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menuItemAddToPlaylist:
                            mPresenter.onAddToPlaylistClick(item);
                            break;
                    }

                    return false;
                }
            });
            popup.show();
        }

        @Override
        public void OnClick() {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make sure to override this and inject dependencies and call presenter.onCreate()
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_item_detail, container, false);
        ButterKnife.bind(this, view);

        if (itemsRv.getLayoutManager() == null) {
            itemsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onShuffleAllClick();
            }
        });

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBackIbClick();
            }
        });

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:called");
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayMetadata(String title, String subtitle, String artPath) {

        titleTv.setText(title);
        subtitleTv.setText(subtitle);

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (artPath != null) {
            Glide.with(this)
                    .asBitmap()
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Bitmap> target,
                                                    boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource,
                                                       Object model,
                                                       Target<Bitmap> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            updateUiColor(resource);
                            return false;
                        }


                    })
                    .load(artPath)
                    .apply(options)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(artIv);
        } else {
            Glide.with(this).clear(artIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                artIv.setImageDrawable(getActivity().getDrawable(getDefaultArtId()));
            } else {
                artIv.setImageDrawable(
                        getResources()
                                .getDrawable(getDefaultArtId()));
            }
            updateUiColor(null);
        }
    }

    @Override
    public void displayMediaList(List<MediaBrowserCompat.MediaItem> itemList) {
        itemsRv.setAdapter(
                new MediaRecyclerViewAdapter(getActivity(), new MediaListTypeFactory(),
                        getVisitableList(itemList)));
    }

    private void updateUiColor(Bitmap resource) {

        if (resource == null) {
            setUiColorWithPalette(null);
        } else {
            ColorUtil.generatePalette(resource, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setUiColorWithPalette(palette);
                }
            });
        }
    }

    private void setUiColorWithPalette(Palette palette) {

        darkColor = ColorUtil.getDarkColor(palette);
        toolbarLayout.setContentScrimColor(darkColor);
        shuffleFab.setColorFilter(darkColor, PorterDuff.Mode.SRC_IN);

        final Window window = getActivity().getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(0);
            window.setStatusBarColor(darkColor);
        }
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void close() {
        getActivity().finish();
    }

    @Override
    public void showAddToPlaylistsDialog(String songId) {
        AddSongsToPlaylistsFragment fragment = new AddSongsToPlaylistsFragment();
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_SONG_ID, songId);
        fragment.setArguments(b);
        fragment.show(getChildFragmentManager(), "AddSongsToPlaylistsFragment");
    }

    protected int getDefaultArtId() {
        return R.drawable.default_album_art;
    }

    protected int getSubtitleIconArtId() {
        return R.drawable.ic_info_outline_grey_24dp;
    }

    /**
     * converts mediaitem list to visitable list
     * @param songList input list
     * @return visitable list
     */
    protected abstract List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> songList);
}
