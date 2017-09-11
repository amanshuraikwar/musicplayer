package app.sonu.com.musicplayer.ui.album;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumSongVisitable;
import app.sonu.com.musicplayer.list.visitable.DetailTitleVisitable;
import app.sonu.com.musicplayer.ui.view.StateAwareAppBarLayout;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 21/8/17.
 */

public class AlbumFragment extends BaseFragment<AlbumMvpPresenter> implements AlbumMvpView{

    private static final String TAG = AlbumFragment.class.getSimpleName();

    public static final String BACK_STACK_TAG = "__albumfragment__";

    @BindView(R.id.artIv)
    ImageView artIv;

    @BindView(R.id.itemsRv)
    FastScrollRecyclerView itemsRv;

    @BindView(R.id.toolbarLayout)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.appBarLayout)
    StateAwareAppBarLayout appBarLayout;

    @BindView(R.id.backIb)
    ImageButton backIb;

    @BindView(R.id.shuffleFab)
    FloatingActionButton shuffleFab;

    private int backgroundColor, bodyColor, titleColor;

    private SongOnClickListener songOnClickListener = new SongOnClickListener() {
        @Override
        public void onSongClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongClicked(item);
        }

        @Override
        public void OnClick() {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        ButterKnife.bind(this, view);

        if (itemsRv.getLayoutManager() == null) {
            itemsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        appBarLayout.setOnStateChangeListener(new StateAwareAppBarLayout.OnStateChangeListener() {
            @Override
            public void onStateChange(StateAwareAppBarLayout.State toolbarChange) {
                Log.d(TAG, "state="+toolbarChange);
                switch (toolbarChange) {
                    case COLLAPSED:
                        backIb.setColorFilter(titleColor, PorterDuff.Mode.SRC_IN);
                        break;
                    default:
                        backIb.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                        break;
                }
            }
        });

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBackIbClick();
            }
        });

        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onShuffleAllClick();
            }
        });

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:called");

        DaggerUiComponent.builder()
                .uiModule(new UiModule(getActivity()))
                .applicationComponent(((MyApplication)getActivity().getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        mPresenter.onCreate(getActivity(),
                (MediaBrowserCompat.MediaItem) getArguments().getParcelable("mediaItem"));

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:called");
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void closeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void displayListData(final MediaBrowserCompat.MediaItem item,
                                String artPath,
                                final List<MediaBrowserCompat.MediaItem> itemList) {

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (artPath != null) {
            Glide.with(getActivity())
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
                            updateUi(item, resource, itemList);
                            return false;
                        }


                    })
                    .load(artPath)
                    .apply(options)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(artIv);
        } else {
            Glide.with(getActivity()).clear(artIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                artIv.setImageDrawable(getActivity().getDrawable(R.drawable.default_album_art));
            } else {
                artIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_album_art));
            }
            updateUi(item, null, itemList);
        }
    }

    public void updateUi(final MediaBrowserCompat.MediaItem item,
                         Bitmap art,
                         final List<MediaBrowserCompat.MediaItem> itemList) {
        if (art != null) {
            ColorUtil.generatePalette(art, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    itemsRv.setAdapter(
                            new MediaRecyclerViewAdapter(
                                    getVisitableList(
                                            item,
                                            ColorUtil.getColorSwatch(palette),
                                            itemList),
                                    new MediaListTypeFactory()));
                    updateUiColor(ColorUtil.getColorSwatch(palette));
                }
            });
        } else {
            itemsRv.setAdapter(
                    new MediaRecyclerViewAdapter(
                            getVisitableList(
                                    item,
                                    ColorUtil.getColorSwatch(null),
                                    itemList),
                            new MediaListTypeFactory()));
            updateUiColor(ColorUtil.getColorSwatch(null));
        }
    }

    private void updateUiColor(Palette.Swatch swatch) {
        backgroundColor = ColorUtil.getBackgroundColor(swatch);
        titleColor = ColorUtil.getTitleColor(swatch);
        bodyColor = ColorUtil.getBodyColor(swatch);

        toolbarLayout.setContentScrimColor(backgroundColor);
        shuffleFab.setBackgroundTintList(ColorStateList.valueOf(titleColor));
        shuffleFab.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);

        itemsRv.setThumbColor(backgroundColor);
        itemsRv.setTrackColor(ColorUtil.makeColorTransparent(bodyColor));

    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * converts mediaitem list to visitable list
     * @param songList input list
     * @return visitable list
     */
    private List<BaseVisitable> getVisitableList(MediaBrowserCompat.MediaItem item,
                                                 Palette.Swatch swatch,
                                                 List<MediaBrowserCompat.MediaItem> songList) {
        Log.d(TAG, "getVisitableList:called");

        List<BaseVisitable> visitableList = new ArrayList<>();

        int backgroundColor = ColorUtil.getBackgroundColor(swatch);
        int titleColor = ColorUtil.getTitleColor(swatch);
        int bodyColor = ColorUtil.getBodyColor(swatch);

        DetailTitleVisitable detailTitleVisitable = new DetailTitleVisitable(item);
        detailTitleVisitable.setBackgroundColor(backgroundColor);
        detailTitleVisitable.setTitleTextColor(titleColor);
        detailTitleVisitable.setSubtitleTextColor(bodyColor);
        visitableList.add(detailTitleVisitable);

        for (MediaBrowserCompat.MediaItem songItem : songList) {
            AlbumSongVisitable visitable = new AlbumSongVisitable(songItem);
            visitable.setOnClickListener(songOnClickListener);
            visitableList.add(visitable);
        }

        return visitableList;
    }
}
