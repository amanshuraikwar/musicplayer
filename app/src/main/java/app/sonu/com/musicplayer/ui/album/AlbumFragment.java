package app.sonu.com.musicplayer.ui.album;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;
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
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 21/8/17.
 */

public class AlbumFragment extends BaseFragment<AlbumMvpPresenter> implements AlbumMvpView{

    private static final String TAG = AlbumFragment.class.getSimpleName();

    public static final String BACK_STACK_TAG = "__albumfragment__";

    @BindView(R.id.elasticDragDismissLayout)
    ElasticDragDismissFrameLayout elasticDragDismissFrameLayout;

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.subtitleTv)
    TextView subtitleTv;

    @BindView(R.id.artIv)
    ImageView artIv;

    @BindView(R.id.itemsRl)
    FastScrollRecyclerView itemsRl;

    @BindView(R.id.topBarRl)
    View topBarRl;

    @BindView(R.id.backIb)
    ImageButton backIb;

    @BindView(R.id.albumMetadataRl)
    View albumMetadataRl;

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


        if (itemsRl.getLayoutManager() == null) {
            itemsRl.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        ViewCompat.setNestedScrollingEnabled(itemsRl, false);

        elasticDragDismissFrameLayout.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset,
                               float elasticOffsetPixels,
                               float rawOffset,
                               float rawOffsetPixels) {

            }

            @Override
            public void onDragDismissed() {
                mPresenter.onDragDismissed();
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
    public void displayList(List<MediaBrowserCompat.MediaItem> itemList) {
        itemsRl.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
        itemsRl.invalidate();
    }

    @Override
    public void displayAlbumData(String title, String subtitle, String artPath) {
        titleTv.setText(title);
        subtitleTv.setText(subtitle);

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
                            updateUiColor(resource);
                            return false;
                        }


                    })
                    .load(artPath)
                    .apply(options)
                    .into(artIv);
        } else {
            Glide.with(getActivity()).clear(artIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                artIv.setImageDrawable(getActivity().getDrawable(R.drawable.default_album_art_album));
            } else {
                artIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_album_art_album));
            }
            updateUiColor(null);
        }
    }

    private void updateUiColor(Bitmap resource) {
        if (resource == null) {
            setUiColorWithSwatch(null);
        } else {
            ColorUtil.generatePalette(resource, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setUiColorWithSwatch(ColorUtil.getColorSwatch(palette));
                }
            });
        }
    }

    private void setUiColorWithSwatch(Palette.Swatch swatch) {

        int backgroundColor = ColorUtil.getBackgroundColor(swatch);
        int titleColor = ColorUtil.getTitleColor(swatch);
        int bodyColor = ColorUtil.getBodyColor(swatch);

        topBarRl.setBackgroundColor(ColorUtil.makeColorTransparent(backgroundColor));
        backIb.setColorFilter(titleColor, PorterDuff.Mode.SRC_IN);
        albumMetadataRl.setBackgroundColor(backgroundColor);

        titleTv.setTextColor(titleColor);
        subtitleTv.setTextColor(bodyColor);
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
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> songList) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem item : songList) {
            AlbumSongVisitable visitable = new AlbumSongVisitable(item);
            visitable.setOnClickListener(songOnClickListener);
            visitableList.add(visitable);
        }

        return visitableList;
    }
}
